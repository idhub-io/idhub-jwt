package io.idhub.jwt.serialisers.reactive;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.nimbusds.jose.util.X509CertUtils;
import org.springframework.http.server.reactive.SslInfo;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SslInfoSerializer extends StdSerializer<SslInfo> {

    public static final String SESSION_ID = "sessionId";
    public static final String CLIENT_CERTIFICATE = "clientCertificate";

    public SslInfoSerializer() {
        this(null);
    }

    public SslInfoSerializer(Class<SslInfo> t) {
        super(t);
    }

    @Override
    public void serialize(SslInfo sslInfo, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
            throws IOException {
        if (sslInfo != null) {
            jsonGenerator.writeStartObject();
            if (sslInfo.getSessionId() != null) {
                jsonGenerator.writeObjectField(SESSION_ID, sslInfo.getSessionId());
            }
            if (sslInfo.getPeerCertificates() != null) {
                jsonGenerator.writeObjectField(CLIENT_CERTIFICATE, Stream.of(sslInfo.getPeerCertificates()).map(X509CertUtils::toPEMString).collect(Collectors.toList()));
            }
            jsonGenerator.writeEndObject();

        }
    }
}
