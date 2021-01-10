package io.idhub.jwt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Indexed;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationConfig {

    @Id
    @JsonIgnore
    private UUID id;
    private String applicationId;

    public JWSAlgorithm defaultSigningAlgorithm;
    public JWEAlgorithm defaultEncryptionAlgorithm;
    public EncryptionMethod defaultEncryptionMethod;

    public Long keyValidPeriod;

    public UUID currentSignKid;
    public UUID currentEncKid;
}
