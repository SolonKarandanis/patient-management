package com.pm.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketConfig {

    @Bean
    public RouteLocator webSocketRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("notification-service-ws-route", r -> r.path("/ws/**")
                        .uri("ws://localhost:4010"))
                .build();
    }
}
