package io.idhub.jwt.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityContextRepository implements ServerSecurityContextRepository {

    private static final String X_IDHUB_PRINCIPAL_ID_HEADER_NAME = "x-idhub-principal-id";
    private static final AnonymousAuthenticationToken ANONYMOUS = new AnonymousAuthenticationToken("anonymous",
                                                                                                   "anonymous", Stream.of(new SimpleGrantedAuthority("NONE")).collect(Collectors.toSet()));

    @Override
    public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
        // Don't know yet where this is for.
        return null;
    }

    @Override
    public Mono<SecurityContext> load(final ServerWebExchange serverWebExchange) {
        final String identity = serverWebExchange.getRequest().getHeaders().getFirst(X_IDHUB_PRINCIPAL_ID_HEADER_NAME);
        log.debug("Identity {}", identity);
        if (StringUtils.isEmpty(identity)) {
            return Mono.just(new SecurityContextImpl(ANONYMOUS));
        }

        return Mono.just(new SecurityContextImpl(new UsernamePasswordAuthenticationToken(identity, "", Collections.EMPTY_SET)));
    }
}