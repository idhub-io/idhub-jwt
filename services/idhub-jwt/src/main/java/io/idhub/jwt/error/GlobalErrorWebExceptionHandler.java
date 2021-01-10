package io.idhub.jwt.error;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.idhub.jwt.exceptions.IDHubErrorException;
import io.idhub.jwt.services.TraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Component
@Order(-2)
@Slf4j
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler {

    private TraceService traceService;

    public GlobalErrorWebExceptionHandler(TraceService traceService, ErrorAttributes errorAttributes,
                                          ResourceProperties resourceProperties,
                                          ApplicationContext applicationContext,
                                          ServerCodecConfigurer configurer) {
        super(errorAttributes, resourceProperties, applicationContext);
        this.setMessageWriters(configurer.getWriters());
        this.traceService = traceService;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions
                .route(RequestPredicates.all(), request -> {
                    Throwable ex = errorAttributes.getError(request);
                    log.debug("We caught the following exception", ex);
                    HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                    final HttpHeaders headers = new HttpHeaders();

                    IDHubRestErrorResponse error = IDHubRestErrorResponse.builder()
                                                                         .traceId(traceService.traceId())
                                                                         .message("Please contact IDHub using the trace ID specified in the response.")
                                                                         .errors(new ArrayList<>())
                                                                         .timestamp(Instant.now())
                                                                         .build();

                    if (ex instanceof IDHubErrorException) {
                        IDHubErrorException idHubError = (IDHubErrorException) ex;
                        httpStatus = idHubError.getIdHubErrorType().getHttpStatus();
                        error.getErrors().addAll(idHubError.getIDHubErrors());
                    }

                    if (ex instanceof ResponseStatusException) {
                        ResponseStatusException responseStatusException = (ResponseStatusException) ex;
                        httpStatus = responseStatusException.getStatus();
                        headers.addAll(responseStatusException.getResponseHeaders());
                        error.setMessage(responseStatusException.getReason() + " - " + error.getMessage());

                        error.getErrors().addAll(extractResponseStatusExceptionErrors(ex));
                    }

                    error.setHttpStatus(httpStatus);
                    log.debug("We return the following response: {}", error);

                    return ServerResponse.status(httpStatus)
                                         .headers(httpHeaders -> httpHeaders.addAll(headers))
                                         .bodyValue(error);
                });
    }


    private List<IDHubError> extractResponseStatusExceptionErrors(Throwable ex) {
        List<IDHubError> errors = new ArrayList<>();

        if (ex instanceof WebExchangeBindException) {
            WebExchangeBindException webExchangeBindException = (WebExchangeBindException) ex;
            errors.addAll(convertWebExchangeBindExceptionToIDHubError(webExchangeBindException));
        } else if (NestedExceptionUtils.getMostSpecificCause(ex) instanceof InvalidFormatException) {
            errors.add(convertInvalidFormatExceptionIntoIDHubError((InvalidFormatException) NestedExceptionUtils.getMostSpecificCause(ex)));

        } else {
            errors.add(IDHubError.builder().code(JWTErrorType.ErrorCode.IDHUB_GENERIC_ERROR_CODE.getValue())
                                 .message(NestedExceptionUtils.getMostSpecificCause(ex).getMessage())
                                 .build());
        }
        return errors;
    }

    private IDHubError convertInvalidFormatExceptionIntoIDHubError(InvalidFormatException ex) {
        return IDHubError.builder()
                         .code(JWTErrorType.ErrorCode.IDHUB_INVALID_FORMAT_BAD_REQUEST.getValue())
                         .message(ex.getOriginalMessage())
                         .build();
    }

    private List<IDHubError> convertWebExchangeBindExceptionToIDHubError(WebExchangeBindException webExchangeBindException) {
        return webExchangeBindException.getAllErrors().stream().map(error ->
                                                                            IDHubError.builder()
                                                                                      .code(extractWebExchangeBinExceptionCodeOrGetDefault(error))
                                                                                      .message(error.getDefaultMessage())
                                                                                      .build()).collect(Collectors.toList());
    }

    private String extractWebExchangeBinExceptionCodeOrGetDefault(org.springframework.validation.ObjectError error) {
        if (Objects.nonNull(error) && Objects.nonNull(error.getCodes())) {
            List<String> codes = Arrays.asList(error.getCodes());
            if (!CollectionUtils.isEmpty(codes)) {
                return codes.get(0);
            }
        }
        return JWTErrorType.ErrorCode.IDHUB_VALIDATION_FAILURE_BAD_REQUEST.getValue();
    }
}