package io.idhub.jwt.api.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jwt.*;
import io.idhub.jwt.exceptions.IDHubRestErrorException;
import io.idhub.jwt.exceptions.IDHubRestRunTimeErrorException;
import io.idhub.jwt.model.JWTDecoded;
import io.idhub.jwt.model.JWTFormat;
import io.idhub.jwt.model.Key;
import io.idhub.jwt.model.KeyFormat;
import io.idhub.jwt.respositories.ApplicationConfigRepository;
import io.idhub.jwt.respositories.KeyRepository;
import io.idhub.jwt.services.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URL;
import java.security.Principal;
import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

import static io.idhub.jwt.error.JWTErrorType.IDHUB_TOKEN_FORMAT_INVALID;
import static io.idhub.jwt.error.JWTErrorType.IDHUB_TOKEN_SIGNATURE_INVALID;

@RestController
@RequestMapping(path = "/jwt")
@Slf4j
@RequiredArgsConstructor
public class JWTRestController {

    private final KeyRepository keyRepository;
    private final ApplicationConfigRepository applicationConfigRepository;
    private final JWTService jwtService;

    @PostMapping(path = "/create")
    public Mono<String> createJwt(
            Principal principal,
            @RequestBody JWTClaimsSet jwtClaimsSet,
            @RequestParam(value = "format", defaultValue = "SIGNED") JWTFormat format
            ) {
        String applicationId = principal.getName();
        return applicationConfigRepository
                .findByApplicationId(applicationId)
                .flatMap(c -> {
                    switch (format) {
                        case SIGNED:
                        default:
                            return keyRepository.findByIdAndApplicationId(c.getCurrentSignKid(), applicationId)
                                    .flatMap(signingKey -> Mono.just(jwtService.signClaims(jwtClaimsSet, signingKey.getJwk(), c.getDefaultSigningAlgorithm()).serialize()));
                    }

                });
    }

    @PostMapping(path = "/read")
    public Mono<JWTDecoded> createJwt(
            Principal principal,
            @RequestBody String jwtSerialised
    ) throws ParseException {
        JWT jwt = JWTParser.parse(jwtSerialised);
        // Check the JWT type
        if (jwt instanceof PlainJWT) {
            PlainJWT plainObject = (PlainJWT)jwt;
            return Mono.just(JWTDecoded.builder()
                    .format(JWTFormat.PLAIN)
                    .header(plainObject.getHeader().toJSONObject())
                    .body(plainObject.getJWTClaimsSet().toJSONObject())
                    .isValid(true)
                    .build());
        } else if (jwt instanceof SignedJWT) {
            SignedJWT jwsObject = (SignedJWT)jwt;
            return Mono.just(
                    JWTDecoded.builder()
                            .format(JWTFormat.SIGNED)
                            .header(jwsObject.getHeader().toJSONObject())
                            .body(jwsObject.getJWTClaimsSet().toJSONObject())
                            .build());
            // continue with signature verification...
        } else if (jwt instanceof EncryptedJWT) {
           throw new IllegalArgumentException();
        } else {
            throw new IllegalArgumentException();
        }
    }

    @PostMapping(path = "/verify")
    public Mono<JWTDecoded> verifyJwt(
            Principal principal,
            @RequestBody String jwtSerialised,
            @RequestHeader(value = "expectedIssuer") Optional<String> expectedIssuer,
            @RequestHeader(value = "expectedAudience") Optional<String> expectedAudience,
            @RequestHeader(value = "pem") Optional<String> pem,
            @RequestHeader(value = "jwk", required = false) Optional<String> jwkSerialised,
            @RequestHeader(value = "jwks-uri", required = false) Optional<URL> jwkUri

    ) throws ParseException {
        String applicationId = principal.getName();

        JWT jwt = JWTParser.parse(jwtSerialised);
        if (jwt instanceof PlainJWT) {
            PlainJWT plainObject = (PlainJWT)jwt;
            return Mono.just(JWTDecoded.builder()
                    .format(JWTFormat.PLAIN)
                    .header(plainObject.getHeader().toJSONObject())
                    .body(plainObject.getJWTClaimsSet().toJSONObject())
                    .isValid(true)
                    .build());
        } else if (jwt instanceof SignedJWT) {
            SignedJWT jwsObject = (SignedJWT)jwt;


            JWTDecoded.JWTDecodedBuilder jwtDecoded = null;
            try {
                jwtDecoded = JWTDecoded.builder()
                        .format(JWTFormat.SIGNED)
                        .header(jwsObject.getHeader().toJSONObject())
                        .body(jwsObject.getJWTClaimsSet().toJSONObject());
            } catch (ParseException e) {
                throw new IDHubRestRunTimeErrorException(IDHUB_TOKEN_FORMAT_INVALID, e);
            }

            if (pem.isPresent()) {
                JWK jwkFromPem;
                try {
                    jwkFromPem = JWK.parseFromPEMEncodedX509Cert(pem.get());
                } catch (JOSEException e) {
                    throw new IDHubRestRunTimeErrorException(IDHUB_TOKEN_FORMAT_INVALID, e);
                }
                try {
                    jwtService.verifyJWS(jwtSerialised, expectedIssuer, expectedAudience, jwkFromPem);
                    jwtDecoded.isValid(true);
                } catch (IDHubRestErrorException e) {
                    jwtDecoded.isValid(false);
                }
            } else if (jwkSerialised.isPresent()) {
                try {
                    JWK jwk = JWK.parse(jwkSerialised.get());
                    jwtService.verifyJWS(jwtSerialised, expectedIssuer, expectedAudience,jwk);
                    jwtDecoded.isValid(true);
                } catch (IDHubRestErrorException e) {
                    jwtDecoded.isValid(false);
                }
            } else if (jwkUri.isPresent()) {
                JWKSet jwkSet;
                try {
                    jwkSet = JWKSet.load(jwkUri.get());
                } catch (IOException e) {
                    throw new IDHubRestRunTimeErrorException(IDHUB_TOKEN_FORMAT_INVALID, e);
                }
                try {
                    jwtService.verifyJWS(jwtSerialised, expectedIssuer, expectedAudience, jwkSet);
                    jwtDecoded.isValid(true);
                } catch (IDHubRestErrorException e) {
                    jwtDecoded.isValid(false);
                }
            }
            return Mono.just(jwtDecoded.build());
        } else if (jwt instanceof EncryptedJWT) {
            throw new IllegalArgumentException();
        } else {
            throw new IllegalArgumentException();
        }
    }
}
