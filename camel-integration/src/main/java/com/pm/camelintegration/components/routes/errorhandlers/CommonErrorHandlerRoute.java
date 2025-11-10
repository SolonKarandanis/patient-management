package com.pm.camelintegration.components.routes.errorhandlers;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.camel.LoggingLevel.WARN;

@Component
public class CommonErrorHandlerRoute extends RouteBuilder {

    public static final AtomicInteger COUNTER = new AtomicInteger(1);

    @Override
    public void configure() throws Exception {
//        from("direct:exceptionHandler")
//                .log(WARN, "In Exception Handler")
//                //                .process(e -> SECONDS.sleep(5))
//                .log(WARN, "${body}");
    }
}
