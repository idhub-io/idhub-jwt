package io.idhub.jwt.serialisers.nimbus;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nimbusds.jwt.JWTClaimsSet;

import java.io.IOException;

public class JWTClaimsSetSerializer extends StdSerializer<JWTClaimsSet> {

    public JWTClaimsSetSerializer() {
        super(JWTClaimsSet.class);
    }

    @Override
    public void serialize(JWTClaimsSet claimsSet, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeObject(claimsSet.toJSONObject());
    }
}
