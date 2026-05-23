package com.pm.authservice.domain.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String messageKey) {
        super(messageKey);
    }
}
