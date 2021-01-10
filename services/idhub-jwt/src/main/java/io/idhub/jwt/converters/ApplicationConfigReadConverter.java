package io.idhub.jwt.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import io.idhub.jwt.model.ApplicationConfig;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ReadingConverter
@Slf4j
@RequiredArgsConstructor
public class ApplicationConfigReadConverter implements Converter<Row, ApplicationConfig> {

    public ApplicationConfig convert(Row source) {

        return ApplicationConfig.builder()
                .id(source.get("id", UUID.class))
                .applicationId(source.get("application_id", String.class))
                .defaultSigningAlgorithm(JWSAlgorithm.parse(source.get("default_signing_algorithm", String.class)))
                .defaultEncryptionAlgorithm(JWEAlgorithm.parse(source.get("default_encryption_algorithm", String.class)))
                .defaultEncryptionMethod(EncryptionMethod.parse(source.get("default_encryption_method", String.class)))
                .keyValidPeriod(source.get("key_valid_period", Long.class))
                .currentSignKid(source.get("current_sign_kid", UUID.class))
                .currentEncKid(source.get("current_enc_kid", UUID.class))
                .build();
    }
}
