package com.pm.fts.exception;

import jakarta.validation.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiError err = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND,
                "Resource Not Found",
                details);
        return ResponseEntityBuilder.build(err);
    }

    @ExceptionHandler(UnsupportedEsQueryException.class)
    public ResponseEntity<Object> handleUnsupportedEsQueryException(
            UnsupportedEsQueryException ex) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiError err = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                details);
        return ResponseEntityBuilder.build(err);
    }

    //  This exception is thrown when a method parameter has the wrong type!
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiError err = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                "Type Mismatch",
                details);
        return ResponseEntityBuilder.build(err);
    }

    //  This exception reports the result of constraint violations!
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(
            Exception ex,
            WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getMessage());
        ApiError err = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                "Constraint Violations",
                details);
        return ResponseEntityBuilder.build(err);
    }

    //  This exception will be triggered if the request body is invalid!
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        ProblemDetail body = createProblemDetail(ex, HttpStatus.BAD_REQUEST, "Malformed JSON request", null, null, request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    //This exception will be raised when a handler method argument annotated with @Valid failed validation!
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        String detailMessageCode = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getObjectName() + " : " + error.getDefaultMessage())
                .toString();
        ProblemDetail body = createProblemDetail(ex, HttpStatus.BAD_REQUEST, "Validation Errors", detailMessageCode, null, request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    //This exception occurs when a controller method does not receive a required parameter.
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        String detailMessageCode = ex.getParameterName() + " parameter is missing";
        ProblemDetail body = createProblemDetail(ex, HttpStatus.BAD_REQUEST, "Missing Parameters", detailMessageCode, null, request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    //The Exception says that the specified request media type (Content type) is not supported!
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        ProblemDetail body = createProblemDetail(ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type", builder.toString(), null, request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {
        String detailMessageCode = String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL());
        ProblemDetail body = createProblemDetail(ex, HttpStatus.NOT_FOUND, "Method Not Found", detailMessageCode, null, request);
        return handleExceptionInternal(ex, body, headers, status, request);
    }


    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(
            Exception ex,
            WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ApiError err = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST,
                "Error occurred",
                details);
        return ResponseEntityBuilder.build(err);
    }
}
