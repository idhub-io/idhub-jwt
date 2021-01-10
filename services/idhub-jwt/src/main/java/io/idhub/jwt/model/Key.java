package io.idhub.jwt.model;

import com.nimbusds.jose.jwk.JWK;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Key {

    @Id
    private UUID id;
    private String applicationId;
    private DateTime validityWindowStop;
    private JWK jwk;
}
