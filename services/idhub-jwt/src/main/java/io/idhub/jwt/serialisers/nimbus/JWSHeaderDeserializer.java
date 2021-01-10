package io.idhub.jwt.serialisers.nimbus;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nimbusds.jose.JWSHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;

public class JWSHeaderDeserializer extends StdDeserializer<JWSHeader> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JWSHeaderDeserializer.class);

    public JWSHeaderDeserializer() {
        this(null);
    }

    public JWSHeaderDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public JWSHeader deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String jwkSerialised = jsonParser.readValueAsTree().toString();
        try {
            return JWSHeader.parse(jwkSerialised);
        } catch (ParseException e) {
            LOGGER.error("can't deserialize JWK {}", jwkSerialised, e);
            return null;
        }
    }
}
