package com.pm.authservice.config.authorisation;

import com.pm.authservice.service.RoleService;
import com.pm.authservice.service.UserService;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {

    private ApplicationContext applicationContext;
    protected UserService usersService;
    protected RoleService roleService;

    public MethodSecurityConfig(ApplicationContext applicationContext) {
        this.applicationContext=applicationContext;
    }

    @Bean
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        CustomMethodSecurityExpressionHandler expressionHandler = new CustomMethodSecurityExpressionHandler(usersService,roleService);
//        expressionHandler.setPermissionEvaluator(new CustomPermissionEvaluator());
        expressionHandler.setApplicationContext(applicationContext);
        setCustomMethodSecurityExpressionHandler(expressionHandler);
        return expressionHandler;
    }

    @Getter
    private CustomMethodSecurityExpressionHandler methodSecurityExpressionHandler;

    private void setCustomMethodSecurityExpressionHandler(
            CustomMethodSecurityExpressionHandler expressionHandler) {
        this.methodSecurityExpressionHandler = expressionHandler;
    }
}
