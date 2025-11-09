package com.pm.camelintegration.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
public class RestEndpoint extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration().component("servlet").bindingMode(RestBindingMode.json);

        rest("/api")
            .post("/sendMessageToArtemis")
            .to("direct:sendMessageToArtemis")
            .post("/sendMessageToRabbitMQ")
            .to("direct:sendMessageToRabbitMQ");

        from("direct:sendMessageToArtemis")
            .log("Received message for Artemis: ${body}")
            .to("activemq:queue:patient_queue");

        from("direct:sendMessageToRabbitMQ")
            .log("Received message for RabbitMQ: ${body}")
            .to("rabbitmq:patient_exchange?queue=patient_queue&autoDelete=false");
    }
}
