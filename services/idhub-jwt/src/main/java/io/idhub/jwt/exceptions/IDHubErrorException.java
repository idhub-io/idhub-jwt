package io.idhub.jwt.exceptions;

import io.idhub.jwt.error.IDHubError;
import io.idhub.jwt.error.IDHubErrorType;
import io.idhub.jwt.error.UpstreamServerError;

import java.util.List;

public interface IDHubErrorException {

    IDHubErrorType getIdHubErrorType();

    Object[] getArgs();

    Throwable getCause();

    UpstreamServerError getUpstreamServerError();

    List<IDHubError> getIDHubErrors();
}
