package com.pm.authservice.domain.exception;

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String messageKey) {
        super(messageKey);
    }
}
