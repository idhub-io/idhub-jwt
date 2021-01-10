package io.idhub.jwt.serialisers.nimbus;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nimbusds.jose.JWSAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JWSAlgorithmDeserializer extends StdDeserializer<JWSAlgorithm> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWSAlgorithmDeserializer.class);

    public JWSAlgorithmDeserializer() {
        this(null);
    }

    public JWSAlgorithmDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public JWSAlgorithm deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        return JWSAlgorithm.parse(jsonParser.getText());
    }
}
