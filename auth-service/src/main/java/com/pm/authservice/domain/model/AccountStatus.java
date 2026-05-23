package com.pm.authservice.domain.model;

public enum AccountStatus {
    ACTIVE("account.active"),
    INACTIVE("account.inactive"),
    DELETED("account.deleted");

    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static AccountStatus fromValue(String value) {
        return switch (value) {
            case "account.active"   -> ACTIVE;
            case "account.inactive" -> INACTIVE;
            case "account.deleted"  -> DELETED;
            default -> throw new IllegalArgumentException("Unknown AccountStatus value: " + value);
        };
    }
}
