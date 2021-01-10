package io.idhub.jwt.serialisers.spring;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.http.MediaType;

import java.io.IOException;

public class MediaTypeSerializer extends StdSerializer<MediaType> {
    public MediaTypeSerializer() {
        this(null);
    }

    public MediaTypeSerializer(Class<MediaType> t) {
        super(t);
    }

    @Override
    public void serialize(MediaType mediaType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeString(mediaType.toString());
    }
}
