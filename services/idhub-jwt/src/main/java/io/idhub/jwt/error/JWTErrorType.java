package io.idhub.jwt.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum JWTErrorType  implements IDHubErrorType {

    IDHUB_TOKEN_SIGNING_FAILED(HttpStatus.BAD_REQUEST,
                              IDHubErrorSource.CLIENT,
                              JWTErrorType.ErrorCode.IDHUB_TOKEN_SIGNING_FAILED,
                              "The signature failed."),

    IDHUB_KEY_NOT_SUPPORTED(HttpStatus.INTERNAL_SERVER_ERROR,
            IDHubErrorSource.SERVER,
            ErrorCode.IDHUB_KEY_NOT_SUPPORTED,
            "The key is not supported. %s"),
    IDHUB_TOKEN_SIGNATURE_INVALID(HttpStatus.FORBIDDEN,
            IDHubErrorSource.USER,
            ErrorCode.IDHUB_TOKEN_SIGNATURE_INVALID,
            "The token signature is invalid."),

    IDHUB_TOKEN_FORMAT_INVALID(HttpStatus.BAD_REQUEST,
            IDHubErrorSource.CLIENT,
            ErrorCode.IDHUB_TOKEN_FORMAT_INVALID,
            "The token should be a JWT."),

    IDHUB_TOKEN_UNKNOWN_KEY(HttpStatus.FORBIDDEN,
            IDHubErrorSource.USER,
            ErrorCode.IDHUB_TOKEN_UNKNOWN_KEY,
            "The token was signed/encrypted by a key with id '%s' which is unknown by ID Hub."),


    IDHUB_TOKEN_ISSUER_INVALID(
            HttpStatus.UNAUTHORIZED,
            IDHubErrorSource.CLIENT,
            ErrorCode.IDHUB_TOKEN_ISSUER_INVALID,
            "Invalid token issuer [%s]"),
    IDHUB_TOKEN_AUDIENCE_INVALID(
            HttpStatus.UNAUTHORIZED,
            IDHubErrorSource.CLIENT,
            ErrorCode.IDHUB_TOKEN_AUDIENCE_INVALID,
            "Invalid token audience [%s]"),
    IDHUB_TOKEN_EXPIRED(
            HttpStatus.UNAUTHORIZED,
            IDHubErrorSource.CLIENT,
            ErrorCode.IDHUB_TOKEN_EXPIRED,
            "Token expired. Token [%s]"),


    IDHUB_KEY_USED_AS_DEFAULT(
            HttpStatus.BAD_REQUEST,
            IDHubErrorSource.CLIENT,
            ErrorCode.IDHUB_KEY_USED_AS_DEFAULT,
            "Key %s is used as default key for either signing or encrypting. Please change default before deleting the key"),
            ;


    @Getter
    @RequiredArgsConstructor
    public enum ErrorCode {
        IDHUB_INVALID_FORMAT_BAD_REQUEST("idhub.invalid.format.bad.request"),
        IDHUB_GENERIC_ERROR_CODE("idhub.generic.error"),
        IDHUB_VALIDATION_FAILURE_BAD_REQUEST("idhub.validation.failure.bad.request"),

        IDHUB_TOKEN_SIGNING_FAILED("idhub.signature.failed"),
        IDHUB_KEY_NOT_SUPPORTED("idhub.key.not.supported"),
        IDHUB_TOKEN_SIGNATURE_INVALID("idhub.token.signature.invalid"),
        IDHUB_TOKEN_FORMAT_INVALID("idhub.token.format.invalid"),
        IDHUB_TOKEN_UNKNOWN_KEY("idhub.token.unknown.key"),
        IDHUB_TOKEN_ISSUER_INVALID("idhub.token.issuer.invalid"),
        IDHUB_TOKEN_AUDIENCE_INVALID("idhub.token.audience.invalid"),
        IDHUB_TOKEN_EXPIRED("idhub.token.expired"),
        IDHUB_KEY_USED_AS_DEFAULT("idhub.key.used.as.default"),
        ;

        private final String value;
    }


    private final HttpStatus httpStatus;
    private final IDHubErrorSource source;
    private final JWTErrorType.ErrorCode code;
    private final String message;

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public IDHubErrorSource getSource() {
        return source;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code.value;
    }

}
