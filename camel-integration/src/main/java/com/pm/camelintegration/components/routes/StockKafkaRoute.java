package com.pm.camelintegration.components.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Date;


import static org.apache.camel.LoggingLevel.INFO;

@Component
public class StockKafkaRoute extends RouteBuilder {

    final String KAFKA_ENDPOINT = "kafka:%s?brokers=localhost:29092";

    @Override
    public void configure() throws Exception {
//        fromF(KAFKA_ENDPOINT, "stock-live")
//                .log(INFO, "[${header.kafka.OFFSET}] [${body}]")
//                .bean(StockPriceEnricher.class)
//                .toF(KAFKA_ENDPOINT, "stock-audit");
    }

    private static class StockPriceEnricher {
        public String enrichStockPrice(String stockPrice) {
            return stockPrice + "," + new Date();
        }
    }
}
