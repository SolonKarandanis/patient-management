package com.pm.authservice.broker;

import com.pm.authservice.user.model.UserEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import user.events.UserEvent;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaAnalyticsProducer implements Producer<UserEventEntity>{
    private static final Logger log = LoggerFactory.getLogger(KafkaAnalyticsProducer.class);

    @Value("${user.processing.topic-name}")
    private String topicName;

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaAnalyticsProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendEvent(UserEventEntity object) {
        try {
            UserEvent event = UserEvent.newBuilder()
                    .setId(object.getPublicId().toString())
                    .setUserId(object.getUserPublicId().toString())
                    .setUsername(object.getUsername())
                    .setEmail(object.getEmail())
                    .setEventType(object.getStatus().name())
                    .build();
            log.info("Sending user analytics event to kafka: {}", event);
            CompletableFuture<SendResult<String, byte[]>> message =kafkaTemplate.send(topicName, event.toByteArray());
        } catch (Exception e) {
            log.error("Error sending user analytics event to kafka", e);
        }
    }
}

