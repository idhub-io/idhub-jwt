package io.idhub.jwt.serialisers.nimbus;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nimbusds.jose.EncryptionMethod;

import java.io.IOException;

public class EncryptionMethodSerializer extends StdSerializer<EncryptionMethod> {

    public EncryptionMethodSerializer() {
        this(null);
    }

    public EncryptionMethodSerializer(Class<EncryptionMethod> t) {
        super(t);
    }

    @Override
    public void serialize(EncryptionMethod encryptionMethod, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeObject(encryptionMethod.getName());
    }
}
