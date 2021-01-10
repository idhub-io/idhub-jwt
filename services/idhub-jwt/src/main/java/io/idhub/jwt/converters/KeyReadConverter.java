package io.idhub.jwt.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import io.idhub.jwt.model.ApplicationConfig;
import io.idhub.jwt.model.Key;
import io.r2dbc.spi.Row;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;

@ReadingConverter
@Slf4j
@RequiredArgsConstructor
public class KeyReadConverter implements Converter<Row, Key> {

    public Key convert(Row source) {

        try {
            return Key.builder()
                    .id(source.get("id", UUID.class))
                    .applicationId(source.get("application_id", String.class))
                    .validityWindowStop(new DateTime(source.get("validity_window_stop", Date.class)))
                    .jwk(JWK.parse(source.get("jwk", String.class)))
                    .build();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
