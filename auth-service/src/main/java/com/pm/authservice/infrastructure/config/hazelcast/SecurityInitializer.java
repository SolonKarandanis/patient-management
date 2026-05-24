package com.pm.authservice.infrastructure.config.hazelcast;

import com.pm.authservice.infrastructure.security.config.WebSecurityClusterConfiguration;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {
    public SecurityInitializer() {
        super(WebSecurityClusterConfiguration.class, HazelcastHttpSessionConfig.class, HazelcastConfig.class);
    }
}
