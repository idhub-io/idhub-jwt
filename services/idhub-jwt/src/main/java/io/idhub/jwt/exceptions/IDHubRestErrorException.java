package io.idhub.jwt.exceptions;

import io.idhub.jwt.error.IDHubError;
import io.idhub.jwt.error.IDHubErrorType;
import io.idhub.jwt.error.UpstreamServerError;
import lombok.Getter;

import java.util.List;

@Getter

public class IDHubRestErrorException extends Exception implements IDHubErrorException {

    private IDHubErrorType idHubErrorType;
    private Object[] args;
    private Throwable cause;
    private UpstreamServerError upstreamServerError;

    public IDHubRestErrorException(IDHubErrorType idHubErrorType, Object... args) {
        super(String.format(idHubErrorType.getMessage(), args));
        this.idHubErrorType = idHubErrorType;
        this.args = args;
    }

    public IDHubRestErrorException(IDHubErrorType idHubErrorType, Throwable cause, Object... args) {
        super(String.format(idHubErrorType.getMessage(), args));
        this.idHubErrorType = idHubErrorType;
        this.cause = cause;
        this.args = args;
    }

    public IDHubRestErrorException(IDHubErrorType idHubErrorType, UpstreamServerError upstreamServerError, Throwable cause, Object... args) {
        super(String.format(idHubErrorType.getMessage(), args));
        this.idHubErrorType = idHubErrorType;
        this.cause = cause;
        this.args = args;
        this.upstreamServerError = upstreamServerError;
    }

    public List<IDHubError> getIDHubErrors() {
        return List.of(IDHubError.builder()
                                 .code(idHubErrorType.getCode())
                                 .source(idHubErrorType.getSource())
                                 .message(String.format(idHubErrorType.getMessage(), args))
                                 .upstreamServerError(upstreamServerError)
                                 .build());
    }
}
