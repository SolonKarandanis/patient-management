package com.pm.camelintegration.components.rest;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RestDsl extends RouteBuilder {

    //http:localhost:8080/camel-integration/javadsl/weather/{city}
    @Override
    public void configure() throws Exception {
        from("javadsl/weather/{city}?produces=application/json")
                .process();
    }
}
