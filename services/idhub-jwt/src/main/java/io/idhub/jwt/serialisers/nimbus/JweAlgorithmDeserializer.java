package io.idhub.jwt.serialisers.nimbus;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nimbusds.jose.JWEAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JweAlgorithmDeserializer extends StdDeserializer<JWEAlgorithm> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JweAlgorithmDeserializer.class);

    public JweAlgorithmDeserializer() {
        this(null);
    }

    public JweAlgorithmDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public JWEAlgorithm deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        return JWEAlgorithm.parse(jsonParser.getText());
    }
}
