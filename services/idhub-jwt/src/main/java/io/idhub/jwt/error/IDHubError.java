package io.idhub.jwt.error;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * The error generated by the platform
 */
public class IDHubError {

    /**
     * The code reference for this error
     */
    private String code;

    private IDHubErrorSource source;
    /**
     * The error message with more context about the issue
     */
    private String message;

    /**
     * The documentation url to reach for having more context about the issue.
     */
    private String doc;

    /**
     * The upstream error that occurred at the upstream side.
     */
    @JsonProperty(value = "upstream_server_error")
    private UpstreamServerError upstreamServerError;

    /**
     * Generic error body when error cannot be converted into code or message properties
     */
    private String error;

}
