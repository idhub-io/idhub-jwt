package io.idhub.jwt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.JSONObject;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JWTDecoded {
    private JSONObject header;
    private JWTFormat format;
    private JSONObject body;
    private Boolean isValid ;
}
