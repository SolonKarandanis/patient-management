package com.pm.authservice.domain.model;

public final class AuthorityConstants {
    private AuthorityConstants() {}

    public static final String ROLE_SYSTEM_ADMIN = "user.role.sa";
    public static final String ROLE_DOCTOR       = "user.role.doctor";
    public static final String ROLE_PATIENT      = "user.role.patient";
    public static final String ROLE_GUEST        = "user.no.role";
}
