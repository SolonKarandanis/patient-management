package com.pm.authservice.broker;

import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaNotificationsProducer implements Producer<NotificationEvent>{

    private static final Logger log = LoggerFactory.getLogger(KafkaNotificationsProducer.class);

    @Value("${notification.topic-name}")
    private String topicName;

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaNotificationsProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendEvent(NotificationEvent object) {
        try {
            log.info("Sending user notification event to kafka: {}", object);
            CompletableFuture<SendResult<String, byte[]>> message =kafkaTemplate.send(topicName, object.toByteArray());
        }
        catch (Exception e) {
            log.error("Error sending user notification event to kafka", e);
        }
    }
}
