package io.idhub.jwt.serialisers.spring;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.http.MediaType;

import java.io.IOException;


public class MediaTypeDeserializer extends StdDeserializer<MediaType> {

    public MediaTypeDeserializer() {
        this(null);
    }

    public MediaTypeDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public MediaType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        return MediaType.parseMediaType(jsonParser.getText());
    }
}
