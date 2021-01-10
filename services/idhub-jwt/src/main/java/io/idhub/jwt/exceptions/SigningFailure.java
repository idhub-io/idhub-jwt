package io.idhub.jwt.exceptions;

public class SigningFailure extends Exception {

    public SigningFailure() {
    }

    public SigningFailure(String message) {
        super(message);
    }

    public SigningFailure(String message, Throwable cause) {
        super(message, cause);
    }

    public SigningFailure(Throwable cause) {
        super(cause);
    }

    public SigningFailure(String message, Throwable cause, boolean enableSuppression, boolean
            writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
