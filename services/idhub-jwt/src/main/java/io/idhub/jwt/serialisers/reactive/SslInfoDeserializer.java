package io.idhub.jwt.serialisers.reactive;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.nimbusds.jose.util.X509CertUtils;
import org.springframework.http.server.reactive.SslInfo;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class SslInfoDeserializer extends StdDeserializer<SslInfo> {

    private ObjectMapper mapper = new ObjectMapper();

    public SslInfoDeserializer() {
        this(null);
    }

    public SslInfoDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SslInfo deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String sessionId = "";
        List<X509Certificate> clientCertificates = Collections.EMPTY_LIST;
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String tok = jsonParser.getCurrentName();
            if (SslInfoSerializer.SESSION_ID.equals(tok)) {
                jsonParser.nextToken();
                sessionId = jsonParser.getText();
            }
            if (SslInfoSerializer.CLIENT_CERTIFICATE.equals(tok)) {
                jsonParser.nextToken();
                ArrayNode node = mapper.readTree(jsonParser);
                Iterator<JsonNode> iterator = node.elements();
                X509Certificate[] array = new X509Certificate[node.size()];
                for (int i = 0; i < node.size(); i++) {
                    if (iterator.hasNext()) {
                        array[i] = X509CertUtils.parse(iterator.next().asText());
                    }
                }
            }
        }

        String finalSessionId = sessionId;
        return new SslInfo() {
            @Override public String getSessionId() {
                return finalSessionId;
            }

            @Override public X509Certificate[] getPeerCertificates() {
                return clientCertificates.toArray(new X509Certificate[0]);
            }
        };
    }
}
