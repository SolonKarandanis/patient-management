package com.pm.authservice.config.hazelcast;

import com.pm.authservice.config.authentication.WebSecurityClusterConfiguration;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer {
    public SecurityInitializer() {
        super(WebSecurityClusterConfiguration.class, HazelcastHttpSessionConfig.class, HazelcastConfig.class);
    }
}
