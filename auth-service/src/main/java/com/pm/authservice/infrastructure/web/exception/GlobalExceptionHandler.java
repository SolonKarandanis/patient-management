package com.pm.authservice.infrastructure.web.exception;

import com.pm.authservice.domain.exception.BusinessRuleException;
import com.pm.authservice.domain.exception.UserNotFoundException;
import com.pm.authservice.infrastructure.util.AppConstants;
import com.pm.authservice.infrastructure.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String EXCEPTION_LINE_GAP = " ---------- ";

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(value = { RuntimeException.class })
    public ProblemDetail handleRuntimeException(final RuntimeException e, final WebRequest request) {
        log.debug(" HANDLER: handleRuntimeException [message: {}, class: {}] ", e.getMessage(), e.getClass().getName());
        if (e instanceof AccessDeniedException) {
            /* Raised when the implementation of the permission evaluator returns false (unauthorized). */
            return problemDetail(HttpStatus.FORBIDDEN, "Forbidden", null);
        } else if (e instanceof ConstraintViolationException) {
            String message = e.getMessage();
            int index = message.indexOf(':');
            if (index >= 0) {
                message = message.substring(index + 1);
            }
            return problemDetail(HttpStatus.BAD_REQUEST, "Bad Request", message.trim());
        } else {
            return getInternalServerErrorResponse(e, request);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage()));
        ProblemDetail problemDetail = problemDetail(HttpStatus.BAD_REQUEST, "Validation Failed",
                "One or more fields failed validation.");
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }

    @ExceptionHandler(value = { NotFoundException.class })
    public ProblemDetail handleNoResultFoundException(final NotFoundException e, final WebRequest request) {
        log.debug(" HANDLER: handleNoResultFoundException");
        return problemDetail(HttpStatus.NOT_FOUND, "Not Found", e.getMessage());
    }

    @ExceptionHandler(value = { BusinessException.class })
    public ProblemDetail handleBusinessException(final BusinessException e, final WebRequest request) {
        log.debug(" HANDLER: handleBusinessException [message: {}, class: {}] ", e.getMessage(), e.getClass().getName());
        /* Validation error, handle as HTTP 400 and translate the error message. */
        return problemDetail(HttpStatus.BAD_REQUEST, "Business Error", getTranslatedErrorMessage(e, request));
    }

    @ExceptionHandler(value = { AuthException.class })
    public ProblemDetail handleAuthException(final AuthException e, final WebRequest request) {
        log.debug(" HANDLER: handleAuthException [message: {}, class: {}] ", e.getMessage(), e.getClass().getName());
        /* Validation error, handle as HTTP 400 and translate the error message. */
        return problemDetail(HttpStatus.BAD_REQUEST, "Authentication Error", getTranslatedErrorMessage(e, request));
    }

    @ExceptionHandler(value = {BusinessRuleException.class})
    public ProblemDetail handleBusinessRuleException(final BusinessRuleException e, final WebRequest request) {
        log.debug(" HANDLER: handleBusinessRuleException [message: {}]", e.getMessage());
        return problemDetail(HttpStatus.BAD_REQUEST, "Business Rule Violation",
                getTranslatedErrorMessage(e.getMessage(), null, request));
    }

    @ExceptionHandler(value = {UserNotFoundException.class})
    public ProblemDetail handleDomainUserNotFoundException(final UserNotFoundException e) {
        log.debug(" HANDLER: handleDomainUserNotFoundException");
        return problemDetail(HttpStatus.NOT_FOUND, "Not Found", e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException e) {
        return problemDetail(HttpStatus.NOT_FOUND, "Resource Not Found", e.getMessage());
    }

    private ProblemDetail getInternalServerErrorResponse(final Exception e, final WebRequest request) {
        log.error(EXCEPTION_LINE_GAP);
        log.error(" EXCEPTION: ", e);
        log.error(EXCEPTION_LINE_GAP);
        /* Internal server error, handle as HTTP 500. */
        return problemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                getTranslatedErrorMessage("prompt.500", new Object[] {}, request));
    }

    private ProblemDetail getBadGatewayResponse(final Exception e, final WebRequest request) {
        log.error(EXCEPTION_LINE_GAP);
        log.error(" EXCEPTION: ", e);
        log.error(EXCEPTION_LINE_GAP);
        return problemDetail(HttpStatus.BAD_GATEWAY, "Bad Gateway",
                getTranslatedErrorMessage("prompt.502", new Object[] {}, request));
    }

    private ProblemDetail problemDetail(final HttpStatus status, final String title, final String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
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
}
