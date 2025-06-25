package com.pm.authservice.config.authorisation;

import com.pm.authservice.service.RoleService;
import com.pm.authservice.service.UserService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    protected UserService usersService;
    protected RoleService roleService;

    public CustomMethodSecurityExpressionHandler(
            UserService usersService,
            RoleService roleService
    ){
        this.usersService = usersService;
        this.roleService = roleService;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        CustomMethodSecurityExpressionRoot root = getCustomMethodSecurityExpressionRoot(authentication);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(new AuthenticationTrustResolverImpl());
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }

    protected CustomMethodSecurityExpressionRoot getCustomMethodSecurityExpressionRoot(Authentication authentication) {
        CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication, usersService,roleService);
        return root;
    }

    @Override
    public StandardEvaluationContext createEvaluationContextInternal(final Authentication auth, final MethodInvocation mi) {
        StandardEvaluationContext standardEvaluationContext = super.createEvaluationContextInternal(auth, mi);
        ((StandardTypeLocator) standardEvaluationContext.getTypeLocator()).registerImport("com.pm.authservice.service");
        return standardEvaluationContext;
    }
}
