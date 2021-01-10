package io.idhub.jwt.api.keys;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import io.idhub.jwt.model.ApplicationConfig;
import io.idhub.jwt.model.Key;
import io.idhub.jwt.respositories.ApplicationConfigRepository;
import io.idhub.jwt.respositories.KeyRepository;
import io.idhub.jwt.services.JWKService;
import io.idhub.jwt.services.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.reactivestreams.Publisher;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/keys")
@Slf4j
@RequiredArgsConstructor
public class KeysRestController {
    public static final int WINDOWS_NB_YEARS = 1;

    private final KeyRepository keyRepository;
    private final ApplicationConfigRepository applicationConfigRepository;
    private final JWKService jwkService;

    @GetMapping(path = "/")
    public Flux<Key> getKeys(
            Principal principal
    ) {
        String applicationId = principal.getName();
        return keyRepository.findByApplicationId(applicationId);
    }

    @PutMapping(path = "/rotate")
    public Flux<Key> rotate(
            Principal principal
    ) {
        String applicationId = principal.getName();
        return applicationConfigRepository.findByApplicationId(applicationId)
                .flatMapMany(c -> {
                    DateTime endOfLife = DateTime.now().plus(c.getKeyValidPeriod());
                    return setValidityWindow(applicationId, endOfLife)
                            .flatMap(keys -> createNewKeys(c))
                            .flatMapMany(keys -> keyRepository.findByApplicationId(applicationId))
                            ;
                })
                ;
    }

    @PutMapping(path = "/reset")
    public Flux<Key> reset(
            Principal principal
    ) {
        String applicationId = principal.getName();
        return applicationConfigRepository.findByApplicationId(applicationId)
                .flatMapMany(c -> {
                    DateTime endOfLife = DateTime.now();
                    return setValidityWindow(applicationId, endOfLife)
                            .flatMap(keys -> createNewKeys(c))
                            .flatMapMany(keys -> keyRepository.findByApplicationId(applicationId))
                            ;
                })
                ;
    }

    private Mono<List<Key>> setValidityWindow(String applicationId, DateTime endOfLife) {
        return keyRepository.findByApplicationId(applicationId)
                .filter(k -> k.getValidityWindowStop().isAfter(endOfLife))
                .map(k -> {
                    k.setValidityWindowStop(endOfLife);
                    return k;
                }).collectList()
                .flatMapMany(keys -> keyRepository.saveAll(keys))
                .flatMap(keys -> keyRepository.findByApplicationId(applicationId))
                .switchIfEmpty(keyRepository.findByApplicationId(applicationId))
                .collectList()
                ;
    }

    private Mono<ApplicationConfig> createNewKeys(ApplicationConfig c) {
        return Mono.zip(
                keyRepository.save(
                        Key.builder()
                                .applicationId(c.getApplicationId())
                                .jwk(jwkService.generateJWK(c.getDefaultSigningAlgorithm(), KeyUse.SIGNATURE))
                                .validityWindowStop(DateTime.now().plusYears(WINDOWS_NB_YEARS))
                                .build()).map(k -> {
                    c.setCurrentSignKid(k.getId());
                    return k;
                }),

                keyRepository.save(
                        Key.builder()
                                .applicationId(c.getApplicationId())
                                .jwk(jwkService.generateJWK(c.getDefaultEncryptionAlgorithm(), KeyUse.ENCRYPTION))
                                .validityWindowStop(DateTime.now().plusYears(WINDOWS_NB_YEARS))
                                .build()).map(k -> {
                    c.setCurrentEncKid(k.getId());
                    return k;
                })
        ).flatMap(r -> applicationConfigRepository.save(c));
    }

}
