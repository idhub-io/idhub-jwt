package io.idhub.jwt.api.app;

import io.idhub.jwt.model.ApplicationConfig;
import io.idhub.jwt.respositories.ApplicationConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping(path = "/app/config")
@Slf4j
@RequiredArgsConstructor
public class ConfigRestController {

    private final ApplicationConfigRepository applicationConfigRepository;

    @GetMapping(path = "/")
    public Mono<ApplicationConfig> getConfig(
            Principal principal
    ) {
        String applicationId = principal.getName();
        return applicationConfigRepository.findByApplicationId(applicationId);
    }

    @PutMapping(path = "/")
    public Mono<ApplicationConfig> putConfig(
            Principal principal,
            @RequestBody ApplicationConfig body
    ) {
        String applicationId = principal.getName();
        return applicationConfigRepository.findByApplicationId(applicationId)
                .flatMap(c -> {
                    if (body.getDefaultSigningAlgorithm() != null) {
                        c.setDefaultSigningAlgorithm(body.getDefaultSigningAlgorithm());
                    }
                    if (body.getDefaultEncryptionAlgorithm() != null) {
                        c.setDefaultEncryptionAlgorithm(body.getDefaultEncryptionAlgorithm());
                    }
                    if (body.getDefaultEncryptionMethod() != null) {
                        c.setDefaultEncryptionMethod(body.getDefaultEncryptionMethod());
                    }
                    if (body.getKeyValidPeriod() != null) {
                        c.setKeyValidPeriod(body.getKeyValidPeriod());
                    }
                    if (body.getCurrentSignKid() != null) {
                        c.setCurrentSignKid(body.getCurrentSignKid());
                    }
                    if (body.getCurrentEncKid() != null) {
                        c.setCurrentEncKid(body.getCurrentEncKid());
                    }

                    return applicationConfigRepository.save(c);
                });
    }
}
