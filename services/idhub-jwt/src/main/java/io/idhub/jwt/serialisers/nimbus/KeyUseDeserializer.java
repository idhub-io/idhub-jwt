package io.idhub.jwt.serialisers.nimbus;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nimbusds.jose.jwk.KeyUse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;

public class KeyUseDeserializer extends StdDeserializer<KeyUse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeyUseDeserializer.class);

    public KeyUseDeserializer() {
        this(null);
    }

    public KeyUseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public KeyUse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String jwkSerialised = jsonParser.readValueAs(String.class);
        try {
            return KeyUse.parse(jwkSerialised);
        } catch (ParseException e) {
            LOGGER.error("can't deserialize JWK {}", jwkSerialised, e);
            throw deserializationContext.wrongTokenException(jsonParser, this.handledType(), JsonToken.VALUE_STRING, e.getMessage());
        }
    }
}
