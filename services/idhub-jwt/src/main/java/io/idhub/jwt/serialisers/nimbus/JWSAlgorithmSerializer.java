package io.idhub.jwt.serialisers.nimbus;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nimbusds.jose.JWSAlgorithm;

import java.io.IOException;

public class JWSAlgorithmSerializer extends StdSerializer<JWSAlgorithm> {

    public JWSAlgorithmSerializer() {
        this(null);
    }

    public JWSAlgorithmSerializer(Class<JWSAlgorithm> t) {
        super(t);
    }

    @Override
    public void serialize(JWSAlgorithm jwsAlgorithm, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeObject(jwsAlgorithm.getName());
    }
}
