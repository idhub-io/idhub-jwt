package io.idhub.jwt.serialisers.nimbus;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nimbusds.jose.jwk.KeyUse;

import java.io.IOException;

public class KeyUseSerializer extends StdSerializer<KeyUse> {

    public KeyUseSerializer() {
        this(null);
    }

    public KeyUseSerializer(Class<KeyUse> t) {
        super(t);
    }

    @Override
    public void serialize(KeyUse keyUse, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeObject(keyUse.toString());
    }
}
