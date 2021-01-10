package io.idhub.jwt.api.app;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import io.idhub.jwt.model.ApplicationConfig;
import io.idhub.jwt.model.Key;
import io.idhub.jwt.respositories.ApplicationConfigRepository;
import io.idhub.jwt.respositories.KeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping(path = "/public/app/{application-id}")
@Slf4j
@RequiredArgsConstructor
public class PublicApplicationKeysRestController {

    private final KeyRepository keyRepository;
    private final ApplicationConfigRepository applicationConfigRepository;

    @GetMapping(path = "/jwks_uri")
    public Mono<JWKSet> getKeys(
            @PathVariable(value = "application-id") String applicationId
    ) {

        return applicationConfigRepository.findByApplicationId(applicationId)
                .flatMap(c ->
                        keyRepository.findByApplicationId(applicationId)
                                .filter(k -> {
                                    if (k.getValidityWindowStop().isAfterNow()) {
                                        return true;
                                    }
                                    if (KeyUse.ENCRYPTION.equals(k.getJwk().getKeyUse())) {
                                        return false;
                                    }
                                    return k.getValidityWindowStop().isAfter(DateTime.now().plus(c.getKeyValidPeriod()));
                                })
                                .map(Key::getJwk)
                                .collectList()
                                .map(JWKSet::new)
                );

    }
}
