package io.idhub.jwt.serialisers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.joda.time.DateTime;

import java.io.IOException;


public class DateTimeForJwtDeserializer extends StdDeserializer<DateTime> {

    public DateTimeForJwtDeserializer() {
        this(null);
    }

    public DateTimeForJwtDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String date = jsonParser.getText();
        return DateTime.now().withMillis(Long.valueOf(date) * 1000);
    }
}
