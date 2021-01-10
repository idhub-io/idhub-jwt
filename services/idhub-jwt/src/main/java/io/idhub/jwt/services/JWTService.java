package io.idhub.jwt.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.idhub.jwt.exceptions.IDHubRestErrorException;
import io.idhub.jwt.exceptions.IDHubRestRunTimeErrorException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static io.idhub.jwt.error.JWTErrorType.*;

@Service
@Slf4j
public class JWTService {

    public SignedJWT signClaims(JWTClaimsSet claimsSet, JWK signingJwk, JWSAlgorithm algorithm) throws IDHubRestRunTimeErrorException {
        log.debug("Signing payload {}", claimsSet);
        try {
            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder(claimsSet)
                    .notBeforeTime(DateTime.now().toDate())
                    .issueTime(DateTime.now().toDate())
                    .jwtID(UUID.randomUUID().toString());

            claimsSet = builder.build();

            JWSSigner signer = getJWSSigner(signingJwk);

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader
                            .Builder(algorithm)
                            .type(JOSEObjectType.JWT)
                            .keyID(signingJwk.getKeyID())
                            .build(),
                    claimsSet);

            signedJWT.sign(signer);
            return signedJWT;
        } catch (JOSEException e) {
            log.error("Signing failed.", e);
            throw new IDHubRestRunTimeErrorException(IDHUB_TOKEN_SIGNING_FAILED);
        }
    }


    /**
     * Encrypt the given payload into a JWE (https://tools.ietf.org/html/rfc7516), using the settings loaded from the configuration file
     *
     * @param payloadSerialised the payload serialised as a string
     * @return a JWE serialised as a String
     * @throws JOSEException Issue raised by the nimbus library, when trying to create the JWE
     */
    public String encryptPayload(String payloadSerialised, JWK encryptionKey, JWEAlgorithm jweAlgorithm, EncryptionMethod encryptionMethod) throws IDHubRestErrorException {
        if (encryptionKey.getAlgorithm() != null && encryptionKey.getAlgorithm() instanceof JWEAlgorithm) {
            jweAlgorithm = (JWEAlgorithm) encryptionKey.getAlgorithm();
            log.debug("Algorithm defined in encryption key '{}' and is a JWE algorithm '{}'", encryptionKey.toPublicJWK(), encryptionKey.getAlgorithm());
        }

        JWEHeader header = new JWEHeader.Builder(jweAlgorithm, encryptionMethod)
                .keyID(encryptionKey.getKeyID())
                .build();
        Payload payload = new Payload(payloadSerialised);
        // Create the JWE object and encrypt it
        JWEObject jwe;
        try {
            if (encryptionKey.getKeyType() == KeyType.EC) {
                ECKey ecKey = (ECKey) encryptionKey;
                jwe = new JWEObject(header, payload);
                jwe.encrypt(new ECDHEncrypter(ecKey));
            } else if (encryptionKey.getKeyType() == KeyType.RSA) {
                RSAKey rsaKey = (RSAKey) encryptionKey;
                jwe = new JWEObject(header, payload);
                jwe.encrypt(new RSAEncrypter(rsaKey));
            } else {
                log.warn("Key type '{}' not supported.", encryptionKey.getKeyType());
                throw new IDHubRestErrorException(IDHUB_KEY_NOT_SUPPORTED, "Key type '" + encryptionKey.getKeyType() + "' not supported.");
            }
        } catch (JOSEException e) {
            log.warn("Key type '{}' not supported.", encryptionKey.getKeyType(), e);
            throw new IDHubRestErrorException(IDHUB_KEY_NOT_SUPPORTED, e, "Key type '" + encryptionKey.getKeyType() + "' not supported.");
        }
        return jwe.serialize();
    }

    /**
     * Decrypt the given JWE (https://tools.ietf.org/html/rfc7516) and return the String serialised payload
     *
     * @param jweSerialised the JWE serialised as a String
     * @return the String serialised payload inside the JWE
     * @throws JOSEException  Issue raised by the nimbus library, when trying to create the JWE
     * @throws ParseException Issue raised by the nimbus library, when trying to parse the serialised JWE
     */
    public String decryptJWE(String jweSerialised, JWKSet jwkSet) throws IDHubRestErrorException {
        JWEObject jwe;
        try {
            jwe = JWEObject.parse(jweSerialised);
        } catch (ParseException e) {
            log.debug("Token is not a JWE: '{}'.", jweSerialised);
            throw new IDHubRestErrorException(IDHUB_TOKEN_FORMAT_INVALID);
        }
        String keyID = jwe.getHeader().getKeyID();

        JWK keyByKeyId = jwkSet.getKeyByKeyId(keyID);
        if (keyByKeyId == null) {
            keyByKeyId = jwkSet.getKeyByKeyId(keyID);
        }
        if (keyByKeyId == null) {
            log.warn("JWT signed with an unknown key id '{}'.", keyID);
            throw new IDHubRestErrorException(IDHUB_TOKEN_UNKNOWN_KEY, keyID);
        }
        try {
            if (keyByKeyId.getKeyType() == KeyType.EC) {
                ECKey ecKey = (ECKey) keyByKeyId;
                jwe.decrypt(new ECDHDecrypter(ecKey));
            } else if (keyByKeyId.getKeyType() == KeyType.RSA) {
                RSAKey rsaKey = (RSAKey) keyByKeyId;
                jwe.decrypt(new RSADecrypter(rsaKey));
            }
        } catch (JOSEException e) {
            log.warn("Key type '{}' not supported.", keyByKeyId.getKeyType(), e);
            throw new IDHubRestErrorException(IDHUB_KEY_NOT_SUPPORTED, e, "Key type '" + keyByKeyId.getKeyType() + "' not supported.");
        }
        return jwe.getPayload().toString();
    }


    public JWSSigner getJWSSigner(JWK signingJwk) {
        try {
            if (signingJwk.getKeyType() == KeyType.EC) {
                log.debug("Key {} is an EC", signingJwk.getKeyID());
                return new ECDSASigner((ECKey) signingJwk);
            } else if (signingJwk.getKeyType() == KeyType.RSA) {
                log.debug("Key {} is an RSA", signingJwk.getKeyID());
                return new RSASSASigner((RSAKey) signingJwk);
            } else {
                log.warn("Key type '{}' not supported.", signingJwk.getKeyType());
                throw new IDHubRestRunTimeErrorException(IDHUB_KEY_NOT_SUPPORTED, "Key type '" + signingJwk.getKeyType() + "' not supported.");
            }
        } catch (JOSEException e) {
            log.warn("Key type '{}' not supported.", signingJwk.getKeyType(), e);
            throw new IDHubRestRunTimeErrorException(IDHUB_KEY_NOT_SUPPORTED, e, "Key type '" + signingJwk.getKeyType() + "' not supported.");
        }
    }


    public JWTClaimsSet verifyJWS(String jwsSerialised, Optional<String> expectedIssuer, Optional<String> expectedAudience, JWKSet jwkSet) throws IDHubRestErrorException {
        SignedJWT jws;
        JWTClaimsSet jwtClaimsSet;
        try {
            jws = SignedJWT.parse(jwsSerialised);
            jwtClaimsSet = jws.getJWTClaimsSet();
        } catch (ParseException e) {
            log.debug("Token is not a JWS: '{}'.", jwsSerialised);
            throw new IDHubRestErrorException(IDHUB_TOKEN_FORMAT_INVALID);
        }

        String keyID = jws.getHeader().getKeyID();
        JWK keyByKeyId = jwkSet.getKeyByKeyId(keyID);
        if (keyByKeyId == null) {
            keyByKeyId = jwkSet.getKeyByKeyId(keyID);
        }
        if (keyByKeyId == null) {
            log.warn("JWT signed with an unknown key id '{}'.", keyID);
            throw new IDHubRestErrorException(IDHUB_TOKEN_UNKNOWN_KEY, keyID);
        }
        return verifyJWS(jwsSerialised, expectedIssuer, expectedAudience, jws, jwtClaimsSet, keyByKeyId);
    }

    public JWTClaimsSet verifyJWS(String jwsSerialised, Optional<String> expectedIssuer, Optional<String> expectedAudience, JWK key) throws IDHubRestErrorException {
        SignedJWT jws;
        JWTClaimsSet jwtClaimsSet;
        try {
            jws = SignedJWT.parse(jwsSerialised);
            jwtClaimsSet = jws.getJWTClaimsSet();
        } catch (ParseException e) {
            log.debug("Token is not a JWS: '{}'.", jwsSerialised);
            throw new IDHubRestErrorException(IDHUB_TOKEN_FORMAT_INVALID);
        }

        return verifyJWS(jwsSerialised, expectedIssuer, expectedAudience, jws, jwtClaimsSet, key);
    }


    private JWTClaimsSet verifyJWS(String jwsSerialised, Optional<String> expectedIssuer, Optional<String> expectedAudience, SignedJWT jws, JWTClaimsSet jwtClaimsSet, JWK keyByKeyId) throws IDHubRestErrorException {
        JWSVerifier jwsVerifier;
        try {
            if (keyByKeyId.getKeyType() == KeyType.EC) {
                ECKey ecKey = (ECKey) keyByKeyId;
                jwsVerifier = new ECDSAVerifier(ecKey);
            } else if (keyByKeyId.getKeyType() == KeyType.RSA) {
                RSAKey rsaKey = (RSAKey) keyByKeyId;
                jwsVerifier = new RSASSAVerifier(rsaKey);
            } else {
                log.warn("Key type '{}' not supported.", keyByKeyId.getKeyType());
                throw new IDHubRestErrorException(IDHUB_KEY_NOT_SUPPORTED, "Key type '" + keyByKeyId.getKeyType() + "' not supported.");
            }

        } catch (JOSEException e) {
            log.warn("Key type '{}' not supported.", keyByKeyId.getKeyType(), e);
            throw new IDHubRestErrorException(IDHUB_KEY_NOT_SUPPORTED, e, "Key type '" + keyByKeyId.getKeyType() + "' not supported.");
        }
        try {
            if (!jws.verify(jwsVerifier)) {
                log.debug("Token signature invalid: {}", jwsSerialised);
                throw new IDHubRestErrorException(IDHUB_TOKEN_SIGNATURE_INVALID);
            }
            Instant now = Instant.now();
            if (expectedIssuer.isPresent() && !expectedIssuer.get().equals(jwtClaimsSet.getIssuer())) {
                log.error("Expected issuer {} is not equal to JWT issuer {}", expectedIssuer, jwtClaimsSet.getIssuer());
                throw new IDHubRestErrorException(IDHUB_TOKEN_ISSUER_INVALID, jwtClaimsSet.getIssuer());
            } else if (expectedAudience.isPresent() && !jwtClaimsSet.getAudience().contains(expectedAudience.get())) {
                log.error("Expected audiences {} doesn't contain JWT audience {}", jwtClaimsSet.getAudience(), expectedAudience);
                throw new IDHubRestErrorException(IDHUB_TOKEN_AUDIENCE_INVALID, jwtClaimsSet.getAudience());
            } else if (jwtClaimsSet.getExpirationTime() != null && now.isAfter(jwtClaimsSet.getExpirationTime().toInstant())) {
                log.warn("JWT expired, {} > now={}", jwtClaimsSet.getExpirationTime().toInstant(), now);
                throw new IDHubRestErrorException(IDHUB_TOKEN_EXPIRED, jwtClaimsSet.getExpirationTime());
            }
            return jwtClaimsSet;
        } catch (JOSEException e) {
            log.error("Couldn't verify the signature of the JWT '{}'.", jwsSerialised, e);
            throw new IDHubRestErrorException(IDHUB_TOKEN_SIGNATURE_INVALID, e);
        }
    }
}
