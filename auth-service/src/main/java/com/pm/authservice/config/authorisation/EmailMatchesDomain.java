package com.pm.authservice.config.authorisation;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.function.Supplier;

public class EmailMatchesDomain implements AuthorizationManager<RequestAuthorizationContext> {
    @Override
    public @Nullable AuthorizationResult authorize(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext object) {
        Authentication authentication = authenticationSupplier.get();

        return new AuthorizationDecision(false);
    }
}
