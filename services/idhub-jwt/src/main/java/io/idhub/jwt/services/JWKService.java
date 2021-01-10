package io.idhub.jwt.services;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.*;

@Service
@Slf4j
public class JWKService {

    private static Map<Algorithm, AlgorithmParameterSpec> ALGORITHMS_SPEC = new HashMap<>();

    static {
        ALGORITHMS_SPEC.put(JWSAlgorithm.ES256, Curve.P_256.toECParameterSpec());
        ALGORITHMS_SPEC.put(JWSAlgorithm.ES384, Curve.P_384.toECParameterSpec());
        ALGORITHMS_SPEC.put(JWSAlgorithm.ES512, Curve.P_521.toECParameterSpec());
    }

    public JWK generateJWK(Algorithm algorithm, KeyUse keyUse) {

        KeyPair keyPair = generateKeyPair(algorithm);
        return toJwk(UUID.randomUUID().toString(), keyPair, keyUse);
    }

    private KeyPair generateKeyPair(Algorithm algorithm) {
        KeyType keyType = KeyType.forAlgorithm(algorithm);

        try {
            KeyPair keyPair;
            if (KeyType.RSA.equals(keyType)) {
                log.debug("Generated RSA key");
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                keyGen.initialize(2048);
                log.debug("Create KeyPair");
                keyPair = keyGen.generateKeyPair();
            } else if (KeyType.EC.equals(keyType)) {
                log.debug("Generated EC key");
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", new BouncyCastleProvider());
                keyGen.initialize(ALGORITHMS_SPEC.get(algorithm), new SecureRandom());
                log.debug("Create KeyPair");
                keyPair = keyGen.generateKeyPair();
            } else {
                log.debug("Unsupported keyType " + keyType);
                throw new IllegalArgumentException("Unsupported keyType " + keyType);
            }
            log.debug("KeyPair created");
            return keyPair;
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            log.error("Couldn't generate a new key", e);
            throw new RuntimeException(e);
        }
    }

    private JWK toJwk(String kid, KeyPair keyPair, KeyUse use) {
        if (keyPair == null) {
            return null;
        }

        if (keyPair.getPublic() instanceof RSAPublicKey) {
            log.debug("RSA keys");
            return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                    .privateKey((RSAPrivateKey) keyPair.getPrivate())
                    .keyUse(use)
                    .keyID(kid)
                    .build();
        } else if (keyPair.getPublic() instanceof ECPublicKey) {
            log.debug("EC keys");
            ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();
            Curve curve = Curve.forECParameterSpec((ecPublicKey).getParams());
            return new ECKey.Builder(curve, ecPublicKey)
                    .privateKey((ECPrivateKey) keyPair.getPrivate())
                    .keyUse(use)
                    .keyID(kid)
                    .build();
        } else {
            log.error("keyPair should be a RSA or EC type");
            throw new IllegalArgumentException("keyPair should be a RSA or EC type ");
        }
    }
}
