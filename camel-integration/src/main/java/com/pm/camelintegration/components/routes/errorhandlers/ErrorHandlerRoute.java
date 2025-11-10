package com.pm.camelintegration.components.routes.errorhandlers;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.pm.camelintegration.components.routes.errorhandlers.CommonErrorHandlerRoute.COUNTER;

@Component
public class ErrorHandlerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
//        errorHandler(deadLetterChannel("direct:exceptionHandler").maximumRedeliveries(2));
//
//        from("timer:time?period=1000")
//                .process(exchange -> exchange.getIn().setBody(new Date()))
//                .choice()
//                .when(e -> COUNTER.incrementAndGet() % 2 == 0)
//                .bean(HelloBean.class, "callBad")
//                .otherwise()
//                .bean(HelloBean.class, "callGood")
//                .end()
//                .log(LoggingLevel.INFO, ">> ${header.firedTime} >> ${body}")
//                .to("log:reply");
    }
}
