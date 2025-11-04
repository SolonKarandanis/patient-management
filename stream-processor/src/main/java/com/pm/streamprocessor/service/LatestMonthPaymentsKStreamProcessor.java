package com.pm.streamprocessor.service;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LatestMonthPaymentsKStreamProcessor {

    @Value("${payment.latest-month.processing.topic-name}")
    private String latestMonthPaymentsTopic;

    public void process(KStream<String, Object> stream){

    }
}
