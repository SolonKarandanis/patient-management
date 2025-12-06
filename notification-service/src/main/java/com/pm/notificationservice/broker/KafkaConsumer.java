package com.pm.notificationservice.broker;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.notificationservice.service.NotificationService;
import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private static final String ERROR_DESERIALIZING_EVENT = "Error deserializing event {}";

    private final NotificationService  notificationService;

    public KafkaConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics="${notification.topic-name}", groupId = "notifications-service")
    public void consumeEvent(byte[] event){
        log.info("Received raw Kafka event (byte length: {}): {}", event.length, new String(event));
        try {
            NotificationEvent notification = NotificationEvent.parseFrom(event);
            notificationService.handleNotificationFromBroker(notification);
        }
        catch (InvalidProtocolBufferException e) {
            log.error(ERROR_DESERIALIZING_EVENT, e.getMessage());
        }
    }
}
