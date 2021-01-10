package io.idhub.jwt.error;

import org.springframework.http.HttpStatus;

public interface IDHubErrorType {

    /**
     * HTTP status to set for the REST response
     *
     * @return Status
     */
    HttpStatus getHttpStatus();

    IDHubErrorSource getSource();

    /**
     * The message to print to the end user
     *
     * @return
     */
    String getMessage();

    /**
     * The code reference corresponding to this issue.
     *
     * @return
     */
    String getCode();

}
