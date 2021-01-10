package io.idhub.jwt.serialisers.nimbus;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nimbusds.jose.JWSHeader;

import java.io.IOException;

public class JWSHeaderSerializer extends StdSerializer<JWSHeader> {

    public JWSHeaderSerializer() {
        this(null);
    }

    public JWSHeaderSerializer(Class<JWSHeader> t) {
        super(t);
    }

    @Override
    public void serialize(JWSHeader jwsHeader, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {

        jsonGenerator.writeObject(jwsHeader.toJSONObject());
    }
}
