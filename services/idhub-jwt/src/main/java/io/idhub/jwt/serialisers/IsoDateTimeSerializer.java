package io.idhub.jwt.serialisers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

public class IsoDateTimeSerializer extends StdSerializer<DateTime> {

    public IsoDateTimeSerializer() {
        this(null);
    }

    public IsoDateTimeSerializer(Class<DateTime> t) {
        super(t);
    }

    @Override
    public void serialize(DateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeObject(ISODateTimeFormat.dateTime().print(dateTime));
    }
}