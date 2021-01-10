package io.idhub.jwt.filters;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j

public class InitApplicationGlobalFilter implements WebFilter {
    public static final int WINDOWS_NB_YEARS = 1;

    private final ApplicationConfigRepository applicationConfigRepository;
    private final KeyRepository keyRepository;
    private final JWTService jwtService;
    private final JWKService jwkService;

    @Value("${idhub.jwt.default.jws-algorithm}")
    private JWSAlgorithm defaultJWSAlgorithm;
    @Value("${idhub.jwt.default.encryption-algorithm}")
    public JWEAlgorithm defaultEncryptionAlgorithm;
    @Value("${idhub.jwt.default.encryption-method}")
    public EncryptionMethod defaultEncryptionMethod;

    @Value("${idhub.jwt.default.key-valid-period}")
    public Long keyValidPeriod;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             WebFilterChain chain) {
        return exchange.getPrincipal()
                .map(Optional::of)
                .switchIfEmpty(Mono.just(Optional.empty()))
                .flatMap(p -> {
                    if (p.isPresent() && !(p.get() instanceof AnonymousAuthenticationToken)) {
                        String applicationId = p.get().getName();
                        return applicationConfigRepository.findByApplicationId(applicationId)
                                .switchIfEmpty(initApp(applicationId))
                                .flatMap(a -> chain.filter(exchange));
                    }
                    return chain.filter(exchange);
                });
    }

    public Mono<ApplicationConfig> initApp(String applicationId) {
        return applicationConfigRepository.save(
                ApplicationConfig.builder()
                        .applicationId(applicationId)
                        .defaultSigningAlgorithm(defaultJWSAlgorithm)
                        .defaultEncryptionAlgorithm(defaultEncryptionAlgorithm)
                        .defaultEncryptionMethod(defaultEncryptionMethod)
                        .keyValidPeriod(keyValidPeriod)
                        .build()
        ).flatMap(c ->
                Mono.zip(
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
                ).flatMap(r -> applicationConfigRepository.save(c))
        );
    }
}
