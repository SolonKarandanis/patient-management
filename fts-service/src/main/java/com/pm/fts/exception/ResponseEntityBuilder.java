package com.pm.fts.exception;

import org.springframework.http.ResponseEntity;

public class ResponseEntityBuilder {
    private ResponseEntityBuilder() {
        throw new IllegalStateException("Utility class");
    }

    public static ResponseEntity<Object> build(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
