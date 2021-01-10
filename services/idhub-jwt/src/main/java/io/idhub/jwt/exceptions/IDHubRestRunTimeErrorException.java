package io.idhub.jwt.exceptions;


import io.idhub.jwt.error.IDHubError;
import io.idhub.jwt.error.IDHubErrorType;
import io.idhub.jwt.error.UpstreamServerError;
import lombok.Getter;

import java.util.List;

@Getter
public class IDHubRestRunTimeErrorException extends RuntimeException implements IDHubErrorException {

    private IDHubErrorType idHubErrorType;
    private Object[] args;
    private Throwable cause;
    private UpstreamServerError upstreamServerError;
    private final List<IDHubError> IDHubErrors;

    public IDHubRestRunTimeErrorException(IDHubErrorType idHubErrorType, Object... args) {
        this(idHubErrorType, null, args);
    }

    public IDHubRestRunTimeErrorException(IDHubErrorType idHubErrorType, Throwable cause, Object... args) {
        this(idHubErrorType, (UpstreamServerError) null, cause, args);
    }

    public IDHubRestRunTimeErrorException(IDHubErrorType idHubErrorType, List<IDHubError> IDHubErrors, Throwable cause, Object... args) {
        super(String.format(idHubErrorType.getMessage(), args));
        this.idHubErrorType = idHubErrorType;
        this.cause = cause;
        this.args = args;
        this.IDHubErrors = IDHubErrors;
    }

    public IDHubRestRunTimeErrorException(IDHubErrorType idHubErrorType, UpstreamServerError upstreamServerError, Throwable cause, Object... args) {
        super(String.format(idHubErrorType.getMessage(), args));
        this.idHubErrorType = idHubErrorType;
        this.cause = cause;
        this.args = args;
        this.upstreamServerError = upstreamServerError;
        this.IDHubErrors = List.of(IDHubError.builder()
                                             .code(idHubErrorType.getCode())
                                             .source(idHubErrorType.getSource())
                                             .message(String.format(idHubErrorType.getMessage(), args))
                                             .upstreamServerError(upstreamServerError)
                                             .build());
    }
}
