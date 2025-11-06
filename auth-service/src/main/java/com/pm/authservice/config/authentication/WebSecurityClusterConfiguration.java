package com.pm.authservice.config.authentication;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@ConditionalOnExpression("${hazelcast.session.management.enabled}==true")
@Configuration
@EnableWebSecurity
public class WebSecurityClusterConfiguration {
}
