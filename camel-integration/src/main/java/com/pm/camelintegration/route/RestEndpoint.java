package com.pm.camelintegration.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class RestEndpoint extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet").bindingMode(RestBindingMode.json);

        from("direct:sendMessageToArtemis")
            .log("Received message for Artemis: ${body}")
            .to("activemq:queue:patient_queue");
    }
}
