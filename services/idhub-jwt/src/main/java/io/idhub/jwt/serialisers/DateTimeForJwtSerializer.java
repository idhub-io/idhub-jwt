package io.idhub.jwt.serialisers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.DateTime;

import java.io.IOException;

public class DateTimeForJwtSerializer extends StdSerializer<DateTime> {

    public DateTimeForJwtSerializer() {
        this(null);
    }

    public DateTimeForJwtSerializer(Class<DateTime> t) {
        super(t);
    }

    @Override
    public void serialize(DateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeObject(dateTime.getMillis() / 1000);
    }
}
