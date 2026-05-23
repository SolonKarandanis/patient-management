package com.pm.authservice.infrastructure.messaging.broker;

import com.pm.authservice.infrastructure.persistence.entity.UserEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import user.events.UserEvent;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaAnalyticsProducer implements Producer<UserEventEntity> {
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
                    .setId(object.getDomainId().toString())
                    .setUserId(object.getUserPublicId().toString())
                    .setUsername(object.getUsername())
                    .setEmail(object.getEmail())
                    .setEventType(object.getStatus().name())
                    .build();
            log.info("Sending user analytics event to kafka: {}", event);
            CompletableFuture<?> message = kafkaTemplate.send(topicName, event.toByteArray());
        } catch (Exception e) {
            log.error("Error sending user analytics event to kafka", e);
        }
    }
}
