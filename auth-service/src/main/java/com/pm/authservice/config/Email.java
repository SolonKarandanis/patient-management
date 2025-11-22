package com.pm.authservice.config;

public record Email(String address, String domain) {
    public Email(String email) {
        this(email.split("@")[0], email.split("@")[1]);
    }

    @Override
    public String toString() {
        return "%s@%s".formatted(address, domain);
    }
}
