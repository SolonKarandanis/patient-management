package com.pm.authservice.config.authorisation;

import com.pm.authservice.service.RoleService;
import com.pm.authservice.user.service.UserService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.WebApplicationContext;

import java.util.function.Supplier;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    protected WebApplicationContext webAppContext;

    protected AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();


    public CustomMethodSecurityExpressionHandler(
            WebApplicationContext webAppContext
    ){
        super();
        this.webAppContext = webAppContext;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        CustomMethodSecurityExpressionRoot root = getCustomMethodSecurityExpressionRoot(authentication);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }

    protected CustomMethodSecurityExpressionRoot getCustomMethodSecurityExpressionRoot(Authentication authentication) {
        CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication, webAppContext);
        return root;
    }

    @Override
    public StandardEvaluationContext createEvaluationContextInternal(final Authentication auth, final MethodInvocation mi) {
        StandardEvaluationContext standardEvaluationContext = super.createEvaluationContextInternal(auth, mi);
        ((StandardTypeLocator) standardEvaluationContext.getTypeLocator()).registerImport("com.pm.authservice.service");
        return standardEvaluationContext;
    }

    @Override
    public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication, MethodInvocation mi) {
        MethodSecurityExpressionOperations root = createSecurityExpressionRoot(authentication, mi);
        MethodBasedEvaluationContext ctx = new MethodBasedEvaluationContext(root, mi.getMethod(), mi.getArguments(), getParameterNameDiscoverer());
        ctx.setBeanResolver(getBeanResolver());
        return ctx;
    }

    private MethodSecurityExpressionOperations createSecurityExpressionRoot(Supplier<Authentication> authentication, MethodInvocation invocation) {
        CustomMethodSecurityExpressionRoot root = getCustomMethodSecurityExpressionRoot(authentication.get());
        root.setTarget(invocation.getThis());
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        root.setDefaultRolePrefix("");
        return root;
    }
}
