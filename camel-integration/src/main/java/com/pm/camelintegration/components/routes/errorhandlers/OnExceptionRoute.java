package com.pm.camelintegration.components.routes.errorhandlers;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OnExceptionRoute extends RouteBuilder {

    static final AtomicInteger counter = new AtomicInteger(1);

    @Override
    public void configure() throws Exception {
//        onException(Exception.class)
//                .log(LoggingLevel.ERROR, "JSS: ${exception}")
//                .handled(true)
//                .to("direct:exceptionHandler");
//
//        from("timer:time?period=1000")
//                .process(exchange -> exchange.getIn().setBody(new Date()))
//                .choice()
//                .when(e -> counter.incrementAndGet() % 2 == 0)
//                .bean(HelloBean.class, "callBad")
//                .otherwise()
//                .bean(HelloBean.class, "callGood")
//                .end()
//                .log(LoggingLevel.INFO, ">> ${header.firedTime} >> ${body}")
//                .to("log:reply");
    }
}
