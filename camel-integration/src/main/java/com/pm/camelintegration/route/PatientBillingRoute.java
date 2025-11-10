package com.pm.camelintegration.route;

import com.pm.camelintegration.grpc.BillingServiceGrpcClient;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PatientBillingRoute extends RouteBuilder {

    private final BillingServiceGrpcClient billingServiceGrpcClient;

    public PatientBillingRoute(BillingServiceGrpcClient billingServiceGrpcClient) {
        this.billingServiceGrpcClient = billingServiceGrpcClient;
    }

    @Override
    public void configure() throws Exception {
        from("jms:queue:patient-queue")
            .routeId("patientBillingRoute")
            .log("Received message from patient-queue: ${body}")
            .unmarshal().json(JsonLibrary.Jackson, Map.class)
            .process(exchange -> {
                Map<String, Object> body = exchange.getIn().getBody(Map.class);
                String patientId = (String) body.get("patientPublicId");
                String name = (String) body.get("name");
                String email = (String) body.get("email");
                
                // Call the gRPC client directly
                billing.BillingResponse response = billingServiceGrpcClient.createBillingAccount(patientId, name, email);
                exchange.getIn().setBody(response);
            })
            .log("gRPC response: ${body}");
    }
}
