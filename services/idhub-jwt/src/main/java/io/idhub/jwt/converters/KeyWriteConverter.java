package io.idhub.jwt.converters;

import io.idhub.jwt.model.ApplicationConfig;
import io.idhub.jwt.model.Key;
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
public class KeyWriteConverter implements Converter<Key, OutboundRow> {

    public OutboundRow convert(Key source) {
        OutboundRow row = new OutboundRow();

        Optional.ofNullable(source.getId()).ifPresent(v -> row.put("id", SettableValue.from(v)));
        Optional.ofNullable(source.getApplicationId()).ifPresent(v -> row.put("application_id", SettableValue.from(v)));
        Optional.ofNullable(source.getValidityWindowStop()).ifPresent(v -> row.put("validity_window_stop", SettableValue.from(v)));
        Optional.ofNullable(source.getJwk()).ifPresent(v -> row.put("jwk", SettableValue.from(v.toJSONString())));
        return row;
    }
}
