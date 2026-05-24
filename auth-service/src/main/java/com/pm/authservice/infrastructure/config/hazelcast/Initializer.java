package com.pm.authservice.infrastructure.config.hazelcast;

import com.hazelcast.config.Config;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

public class Initializer extends AbstractHttpSessionApplicationInitializer {

    public Initializer() {
        super(Config.class); // <2>
    }
}
