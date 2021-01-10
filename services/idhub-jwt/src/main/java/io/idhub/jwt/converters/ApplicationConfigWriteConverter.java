package io.idhub.jwt.converters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.idhub.jwt.model.ApplicationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.mapping.OutboundRow;
import org.springframework.data.r2dbc.mapping.SettableValue;

import java.util.Optional;

@WritingConverter
@Slf4j
@RequiredArgsConstructor
public class ApplicationConfigWriteConverter implements Converter<ApplicationConfig, OutboundRow> {

    public OutboundRow convert(ApplicationConfig source) {
        OutboundRow row = new OutboundRow();

        Optional.ofNullable(source.getId()).ifPresent(v -> row.put("id", SettableValue.from(v)));
        Optional.ofNullable(source.getApplicationId()).ifPresent(v -> row.put("application_id", SettableValue.from(v)));
        Optional.ofNullable(source.getDefaultSigningAlgorithm()).ifPresent(v -> row.put("default_signing_algorithm", SettableValue.from(v.getName())));
        Optional.ofNullable(source.getDefaultEncryptionAlgorithm()).ifPresent(v -> row.put("default_encryption_algorithm", SettableValue.from(v.getName())));
        Optional.ofNullable(source.getDefaultEncryptionMethod()).ifPresent(v -> row.put("default_encryption_method", SettableValue.from(v.getName())));
        Optional.ofNullable(source.getKeyValidPeriod()).ifPresent(v -> row.put("key_valid_period", SettableValue.from(v)));
        Optional.ofNullable(source.getCurrentSignKid()).ifPresent(v -> row.put("current_sign_kid", SettableValue.from(v)));
        Optional.ofNullable(source.getCurrentEncKid()).ifPresent(v -> row.put("current_enc_kid", SettableValue.from(v)));
        return row;
    }
}
