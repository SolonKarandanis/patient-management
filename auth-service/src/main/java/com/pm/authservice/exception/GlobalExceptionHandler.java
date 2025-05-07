package com.pm.authservice.exception;

import com.pm.authservice.util.AppConstants;
import com.pm.authservice.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String EXCEPTION_LINE_GAP = " ---------- ";

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(value = { RuntimeException.class })
    public ResponseEntity<Object> handleRuntimeException(final RuntimeException e, final WebRequest request) {
        log.debug(" HANDLER: handleRuntimeException [message: {}, class: {}] ", e.getMessage(), e.getClass().getName());
        if (e instanceof AccessDeniedException) {
            /* Raised when the implementation of the permission evaluator returns false (unauthorized). */
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
        } else if (e instanceof ConstraintViolationException) {
            String message = e.getMessage();
            int index = message.indexOf(':');
            if (index >= 0) {
                message = message.substring(index + 1);
            }
            return ResponseEntity.badRequest().body(message.trim());
        } else {
            return getInternalServerErrorResponse(e, request);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(value = { NotFoundException.class })
    public ResponseEntity<Object> handleNoResultFoundException(final NotFoundException e, final WebRequest request) {
        log.debug(" HANDLER: handleNoResultFoundException");
        if (e instanceof NotFoundException) {
            /* Empty result set, handle as HTTP 404. */
            return ResponseEntity.notFound().build();
        } else {
            return getInternalServerErrorResponse(e, request);
        }
    }

    @ExceptionHandler(value = { BusinessException.class })
    public ResponseEntity<Object> handleBusinessException(final BusinessException e, final WebRequest request) {
        log.debug(" HANDLER: handleBusinessException [message: {}, class: {}] ", e.getMessage(), e.getClass().getName());
        if (e instanceof BusinessException) {
            /* Validation error, handle as HTTP 400 and translate the error message. */
            return ResponseEntity.badRequest().body(serializeErrorMessageToJson(getTranslatedErrorMessage(e, request)));

        } else {
            return getInternalServerErrorResponse(e, request);
        }
    }

    @ExceptionHandler(value = { AuthException.class })
    public ResponseEntity<Object> handleAuthException(final AuthException e, final WebRequest request) {
        log.debug(" HANDLER: handleAuthException [message: {}, class: {}] ", e.getMessage(), e.getClass().getName());
        if (e instanceof AuthException) {
            /* Validation error, handle as HTTP 400 and translate the error message. */
            return ResponseEntity.badRequest().body(serializeErrorMessageToJson(getTranslatedErrorMessage(e, request)));

        } else {
            return getInternalServerErrorResponse(e, request);
        }
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleResourceNotFoundException(ResourceNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("https://api.bookmarks.com/errors/not-found"));
        problemDetail.setProperty("errorCategory", "Generic");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    private ResponseEntity<Object> getInternalServerErrorResponse(final Exception e, final WebRequest request) {
        log.error(EXCEPTION_LINE_GAP);
        log.error(" EXCEPTION: ", e);
        log.error(EXCEPTION_LINE_GAP);
        /* Internal server error, handle as HTTP 500. */
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(serializeErrorMessageToJson(getTranslatedErrorMessage("prompt.500", new Object[] {}, request)));
    }

    private ResponseEntity<Object> getBadGatewayResponse(final Exception e, final WebRequest request) {
        log.error(EXCEPTION_LINE_GAP);
        log.error(" EXCEPTION: ", e);
        log.error(EXCEPTION_LINE_GAP);
        /* Internal server error, handle as HTTP 500. */
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(serializeErrorMessageToJson(getTranslatedErrorMessage("prompt.502", new Object[] {}, request)));
    }

    private String getTranslatedErrorMessage(final RepException exception, final WebRequest request) {
        return getTranslatedErrorMessage(exception.getLocalizedMessage(), exception.getKeyArgs(), request);
    }

    private String getTranslatedErrorMessage(final String errorMessage, final Object[] messageArguments, final WebRequest request) {
        String translatedMessage = "";

        try {
            translatedMessage = messageSource.getMessage(errorMessage, messageArguments, getRequestLocale(request));
        } catch (NoSuchMessageException e) {
            /* If message is not resolved, then assume that it is a plain-text message. */
            log.debug(" CASE: Translation not found for: {} ", errorMessage);
            translatedMessage = messageArguments != null && messageArguments.length > 0 ? StringUtils.replaceParametersInString(errorMessage, messageArguments)
                    : errorMessage;
        }
        return translatedMessage;
    }

    private Locale getRequestLocale(final WebRequest request) {
        String langIsoCode = request.getHeader(AppConstants.HEADER_NAME_LANGUAGE_ISO);
        log.debug(" Header Language IsoCode: {} ", langIsoCode);
        return (StringUtils.hasLength(langIsoCode)) ? new Locale(langIsoCode) : Locale.ENGLISH;
    }

    private String serializeErrorMessagesToJson(final List<String> errorMessages) {
        String output = "";

        try {
            JSONArray json = new JSONArray();
            errorMessages.forEach(json::put);
            output = json.toString();
        } catch (JSONException e) {
            log.error(" ERROR - Cannot serialize to JSON: ", e);
            output = "[{Application error occurred. Please report this issue to Helpdesk.}]";
        }
        return output;
    }

    private String serializeErrorMessageToJson(final String errorMessage) {
        return serializeErrorMessagesToJson(Stream.of(errorMessage).toList());
    }
}
