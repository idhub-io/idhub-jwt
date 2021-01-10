package io.idhub.jwt.serialisers.spring;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

public class AnonymizeHttpHeadersSerializer extends StdSerializer<HttpHeaders> {
    public AnonymizeHttpHeadersSerializer() {
        this(null);
    }

    public AnonymizeHttpHeadersSerializer(Class<HttpHeaders> t) {
        super(t);
    }

    @Override
    public void serialize(HttpHeaders httpHeaders, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        HttpHeaders mutableHeaders = new HttpHeaders();
        mutableHeaders.addAll(httpHeaders);
        mutableHeaders.remove(HttpHeaders.AUTHORIZATION);
        jsonGenerator.writeObject(mutableHeaders);
    }
}
