package com.pm.fts.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ApiError {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private HttpStatus status;
    private String message;
    private List errors;

    public ApiError() {}

    public ApiError(LocalDateTime timestamp, HttpStatus status, String message, List errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ErrorBuilder bulder(){
        return new ErrorBuilder();
    }

    public class ErrorBuilder {
        public LocalDateTime timestamp;
        public HttpStatus status;
        public String message;
        public List errors;

        public ErrorBuilder withTime(LocalDateTime timestamp){
            this.timestamp=timestamp;
            return this;
        }

        public ErrorBuilder withStatus(HttpStatus status){
            this.status=status;
            return this;
        }

        public ErrorBuilder withMessage(String message){
            this.message=message;
            return this;
        }

        public ErrorBuilder withErros(List errors){
            this.errors=errors;
            return this;
        }

        public ApiError build(){
            return new ApiError(timestamp, status, message, errors);
        }
    }

    /**
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the status
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the errors
     */
    public List getErrors() {
        return errors;
    }

    /**
     * @param errors the errors to set
     */
    public void setErrors(List errors) {
        this.errors = errors;
    }

}
