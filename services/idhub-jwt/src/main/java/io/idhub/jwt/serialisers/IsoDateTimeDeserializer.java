package io.idhub.jwt.serialisers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

public class IsoDateTimeDeserializer extends StdDeserializer<DateTime> {

    public IsoDateTimeDeserializer() {
        this(null);
    }

    public IsoDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String date = jsonParser.getText();
        return ISODateTimeFormat.dateTimeParser().parseDateTime(date);
    }
}