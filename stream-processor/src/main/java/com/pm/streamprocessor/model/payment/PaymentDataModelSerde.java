package com.pm.streamprocessor.model.payment;

import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class PaymentDataModelSerde extends Serdes.WrapperSerde<PaymentDataModel>{
    public PaymentDataModelSerde() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(PaymentDataModel.class));
    }
}
