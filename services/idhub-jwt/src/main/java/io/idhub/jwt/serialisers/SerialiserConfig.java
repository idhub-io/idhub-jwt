package io.idhub.jwt.serialisers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jwt.JWTClaimsSet;

import io.idhub.jwt.serialisers.nimbus.*;
import io.idhub.jwt.serialisers.reactive.SslInfoDeserializer;
import io.idhub.jwt.serialisers.reactive.SslInfoSerializer;
import io.idhub.jwt.serialisers.spring.MediaTypeDeserializer;
import io.idhub.jwt.serialisers.spring.MediaTypeSerializer;
import org.joda.time.DateTime;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.SslInfo;


@Configuration(proxyBeanMethods = false)
public class SerialiserConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer objectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> {
            jacksonObjectMapperBuilder.serializerByType(SslInfo.class, new SslInfoSerializer());
            jacksonObjectMapperBuilder.deserializerByType(SslInfo.class, new SslInfoDeserializer());

            jacksonObjectMapperBuilder.deserializerByType(MediaType.class, new MediaTypeDeserializer());
            jacksonObjectMapperBuilder.serializerByType(MediaType.class, new MediaTypeSerializer());

            jacksonObjectMapperBuilder.deserializerByType(JWSHeader.class, new JWSHeaderDeserializer());
            jacksonObjectMapperBuilder.serializerByType(JWSHeader.class, new JWSHeaderSerializer());

            jacksonObjectMapperBuilder.deserializerByType(JWSAlgorithm.class, new JWSAlgorithmDeserializer());
            jacksonObjectMapperBuilder.serializerByType(JWSAlgorithm.class, new JWSAlgorithmSerializer());


            jacksonObjectMapperBuilder.deserializerByType(EncryptionMethod.class, new EncryptionMethodDeserializer());
            jacksonObjectMapperBuilder.serializerByType(EncryptionMethod.class, new EncryptionMethodSerializer());

            jacksonObjectMapperBuilder.deserializerByType(JWEAlgorithm.class, new JweAlgorithmDeserializer());
            jacksonObjectMapperBuilder.serializerByType(JWEAlgorithm.class, new JweAlgorithmSerializer());

            jacksonObjectMapperBuilder.deserializerByType(JWK.class, new JWKDeserializer());
            jacksonObjectMapperBuilder.serializerByType(JWK.class, new JWKSerializer());

            jacksonObjectMapperBuilder.deserializerByType(JWKSet.class, new JWKSetDeserializer());
            jacksonObjectMapperBuilder.serializerByType(JWKSet.class, new JWKSetSerializer());

            jacksonObjectMapperBuilder.deserializerByType(JWTClaimsSet.class, new JWTClaimsSetDeserializer());
            jacksonObjectMapperBuilder.serializerByType(JWTClaimsSet.class, new JWTClaimsSetSerializer());

            jacksonObjectMapperBuilder.deserializerByType(KeyUse.class, new KeyUseDeserializer());
            jacksonObjectMapperBuilder.serializerByType(KeyUse.class, new KeyUseSerializer());

            jacksonObjectMapperBuilder.deserializerByType(DateTime.class, new IsoDateTimeDeserializer());
            jacksonObjectMapperBuilder.serializerByType(DateTime.class, new IsoDateTimeSerializer());


            jacksonObjectMapperBuilder.deserializerByType(KeyUse.class, new KeyUseDeserializer());
            jacksonObjectMapperBuilder.serializerByType(KeyUse.class, new  KeyUseSerializer());

            jacksonObjectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);
        };
    }
}
