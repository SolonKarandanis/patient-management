package com.pm.streamprocessor.service;

import com.pm.streamprocessor.model.payment.PaymentDataModel;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LatestMonthPaymentsKStreamProcessor {

    @Value("${payment.latest-month.processing.topic-name}")
    private String latestMonthPaymentsTopic;

    public void process(KStream<String, PaymentDataModel> stream){
        //KSTREAM FILTER: Filter the Stream to get the latest monthly payments
        stream.filter(new Predicate<String, PaymentDataModel>(){
            @Override
            public boolean test(String key, PaymentDataModel object) {
                LocalDate currentDate = LocalDate.now();
                LocalDate currentDateMinus1Month = currentDate.minusMonths(1);
                LocalDate paymentCreatingDate = object.getCreatedDate();
                return paymentCreatingDate.isAfter(currentDateMinus1Month);
            }
        }).to(latestMonthPaymentsTopic);
    }
}
