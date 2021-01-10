package io.idhub.jwt.serialisers.nimbus;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.ParseException;

@Slf4j
public class JWTClaimsSetDeserializer extends StdDeserializer<JWTClaimsSet> {

    public JWTClaimsSetDeserializer() {
        super(JWTClaimsSet.class);
    }

    @Override
    public JWTClaimsSet deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String jwkSerialised = jsonParser.readValueAsTree().toString();
        try {
            return JWTClaimsSet.parse(jwkSerialised);
        } catch (ParseException e) {
            log.error("can't deserialize JWTClaimsSet {}", jwkSerialised, e);
            return null;
        }
    }
}
