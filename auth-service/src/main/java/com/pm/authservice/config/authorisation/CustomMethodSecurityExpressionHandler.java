package com.pm.authservice.config.authorisation;

import org.aopalliance.intercept.MethodInvocation;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.WebApplicationContext;

import java.util.function.Supplier;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    protected WebApplicationContext webAppContext;


    public CustomMethodSecurityExpressionHandler(
            WebApplicationContext webAppContext
    ){
        super();
        this.webAppContext = webAppContext;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(@NonNull Authentication authentication, @Nullable MethodInvocation invocation) {
        CustomMethodSecurityExpressionRoot root = getCustomMethodSecurityExpressionRoot(authentication);
        root.setPermissionEvaluator(getPermissionEvaluator());
        return root;
    }

    protected CustomMethodSecurityExpressionRoot getCustomMethodSecurityExpressionRoot(Authentication authentication) {
        return new CustomMethodSecurityExpressionRoot(authentication, webAppContext);
    }

    @Override
    public StandardEvaluationContext createEvaluationContextInternal(@NonNull final Authentication auth, @Nullable final MethodInvocation mi) {
        StandardEvaluationContext standardEvaluationContext = super.createEvaluationContextInternal(auth, mi);
        ((StandardTypeLocator) standardEvaluationContext.getTypeLocator()).registerImport("com.pm.authservice.service");
        return standardEvaluationContext;
    }


    @Override
    public EvaluationContext createEvaluationContext(@NonNull Supplier<? extends @Nullable Authentication> authentication, @Nullable MethodInvocation mi) {
        Authentication auth = authentication.get();
        if (auth == null) {
            throw new IllegalArgumentException("Authentication must not be null");
        }
        MethodSecurityExpressionOperations root = createSecurityExpressionRoot(auth, mi);
        MethodBasedEvaluationContext ctx = new MethodBasedEvaluationContext(root, mi.getMethod(), mi.getArguments(), getParameterNameDiscoverer());
        ctx.setBeanResolver(getBeanResolver());
        return ctx;
    }

}
