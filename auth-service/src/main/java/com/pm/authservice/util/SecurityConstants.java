package com.pm.authservice.util;

public class SecurityConstants {

    private SecurityConstants() {

    }

    public static final String JWT_SECRET = "t3pCSx2wx1ExbQ5z43XXB8my/KR24aon4EH/niU9iZi1I3S69rk1QhlMFFsTrZIY";
    public static final long EXPIRATION_TIME = 86400000; //24hours
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final String BASIC_TOKEN_PREFIX =  "Basic ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String SIGN_IN_URI_ENDING = "/signin";
    public static final String SIGN_UP_URI_ENDING = "/signup";
    public static final String REALM_HEADER = "WWW-Authenticate";
    public static final String[] AUTH_WHITELIST = {
            "/api-docs",
            "/api-docs/swagger-config",
            "/test/*",
            "/actuator/**",
            "/swagger-ui/*",
            "/swagger-ui.html",
            "/swagger-resources",
            "/swagger-resources/**",
            "/roles",
    };
    public static final String[] ALLOWED_ORIGIN_PATTERNS = {
            "http://localhost:4200","http://localhost:3011","http://localhost:8080","http://www.dut.com"
    };
    public static final String[] ALLOWED_METHODS= {
            "GET", "HEAD","PUT", "POST", "PATCH", "DELETE", "OPTIONS"
    };
    public static final String[] ALLOWED_HEADERS= {
            "Access-Control-Allow-Headers",
            "Origin",
            "Accept",
            "X-Requested-With",
            "X-Api-Key",
            "Content-Type",
            "Access-Control-Request-Method",
            "Access-Control-Allow-Origin",
            AUTHORIZATION_HEADER,
            "Connection",
            "Lang-ISO"
    };

}
