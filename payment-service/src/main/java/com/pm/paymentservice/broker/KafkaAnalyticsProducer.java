package com.pm.paymentservice.broker;

import com.pm.paymentservice.model.PaymentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import payment.events.PaymentEvent;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaAnalyticsProducer  implements Producer<PaymentEntity>{

    private static final Logger log = LoggerFactory.getLogger(KafkaAnalyticsProducer.class);

    @Value("${payment.processing.topic-name}")
    private String topicName;

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaAnalyticsProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendEvent(PaymentEntity object) {
        try {
        PaymentEvent event = PaymentEvent.newBuilder()
                .setId(object.getPublicId().toString())
                .setPatientId(object.getPatientId().toString())
                .setState(object.getState().toString())
                .setAmount(object.getAmount().doubleValue())
                .setCreatedDate(object.getCreatedDate().toString())
                .build();
            log.info("Sending payment analytics event to kafka: {}", event);
            CompletableFuture<SendResult<String, byte[]>> message =kafkaTemplate.send(topicName, event.toByteArray());
        } catch (Exception e) {
            log.error("Error sending payment analytics event to kafka", e);
        }
    }
}
