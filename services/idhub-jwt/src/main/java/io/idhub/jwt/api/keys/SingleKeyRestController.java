package io.idhub.jwt.api.keys;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import io.idhub.jwt.exceptions.IDHubRestRunTimeErrorException;
import io.idhub.jwt.model.Key;
import io.idhub.jwt.model.KeyFormat;
import io.idhub.jwt.respositories.ApplicationConfigRepository;
import io.idhub.jwt.respositories.KeyRepository;
import io.idhub.jwt.services.JWKService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

import static io.idhub.jwt.error.JWTErrorType.IDHUB_KEY_USED_AS_DEFAULT;

@RestController
@RequestMapping(path = "/keys")
@Slf4j
@RequiredArgsConstructor
public class SingleKeyRestController {

    public static final int WINDOWS_NB_YEARS = 1;
    private final KeyRepository keyRepository;
    private final ApplicationConfigRepository applicationConfigRepository;
    private final JWKService JWKService;

    @PostMapping(path = "/")
    public Mono<Key> createKey(
            Principal principal,
            ServerWebExchange exchange,
            @RequestParam(value = "use", defaultValue = "SIGNATURE") KeyUse keyUse,
            @RequestBody(required = false) final JWK jwk
    ) {
        exchange.getResponse().setStatusCode(HttpStatus.CREATED);
        String applicationId = principal.getName();
        return applicationConfigRepository.findByApplicationId(applicationId)
                .flatMap(c -> {
                    JWK jwkForKey = jwk;
                    if (jwk == null) {
                        Algorithm algorithm = c.getDefaultSigningAlgorithm();

                        if (KeyUse.ENCRYPTION.equals(keyUse)) {
                            algorithm = c.getDefaultEncryptionAlgorithm();
                        }
                        jwkForKey = JWKService.generateJWK(algorithm, keyUse);
                    }
                    Key key = Key.builder()
                            .applicationId(applicationId)
                            .validityWindowStop(DateTime.now().plusYears(WINDOWS_NB_YEARS))
                            .jwk(jwkForKey)
                            .build();
                    return keyRepository.save(key);
                });
    }

    @DeleteMapping(path = "/{key-id}")
    public Mono<Void> deleteKey(
            Principal principal,
            ServerWebExchange exchange,
            @PathVariable(value = "key-id") UUID keyId
    ) {
        String applicationId = principal.getName();
        exchange.getResponse().setStatusCode(HttpStatus.NO_CONTENT);

        return applicationConfigRepository.findByApplicationId(applicationId)
                .flatMap(c -> {
                    if (c.getCurrentSignKid().equals(keyId) || c.getCurrentEncKid().equals(keyId)) {
                        throw new IDHubRestRunTimeErrorException(IDHUB_KEY_USED_AS_DEFAULT, keyId);
                    }
                    return keyRepository.deleteByIdAndApplicationId(keyId, applicationId);

                })
                ;
    }

    @GetMapping(path = "/{key-id}")
    public Mono getKey(
            Principal principal,
            @PathVariable(value = "key-id") UUID keyId
    ) {
        String applicationId = principal.getName();
        return keyRepository.findByIdAndApplicationId(keyId, applicationId);
    }

    @GetMapping(path = "/{key-id}/jwk")
    public Mono<JWK> getKeyAsJWK(
            Principal principal,
            @PathVariable(value = "key-id") UUID keyId,
            @RequestParam(value = "format", defaultValue = "PUBLIC_JWK") KeyFormat keyFormat
    ) {
        String applicationId = principal.getName();
        return keyRepository.findByIdAndApplicationId(keyId, applicationId)
                .map(k -> {
                    switch (keyFormat) {
                        case PUBLIC_JWK:
                            return k.getJwk().toPublicJWK();
                        case PRIVATE_JWK:
                        default:
                            return k.getJwk();
                    }
                });
    }
    
}
