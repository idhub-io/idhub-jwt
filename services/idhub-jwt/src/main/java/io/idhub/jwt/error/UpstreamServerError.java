package io.idhub.jwt.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.net.URI;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpstreamServerError {
    private URI uri;
    private HttpMethod method;

    private HttpStatus status;
    private HttpHeaders headers;
    private String body;
}
