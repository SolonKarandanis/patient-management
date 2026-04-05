package com.pm.authservice.config.authorisation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.web.context.WebApplicationContext;

@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {


    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }


    @Bean("expressionHandler")
    protected MethodSecurityExpressionHandler createExpressionHandler(WebApplicationContext webAppContext) {
        return new CustomMethodSecurityExpressionHandler(webAppContext);
    }

}
