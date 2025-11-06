package com.pm.notificationservice.broker;

import com.google.protobuf.InvalidProtocolBufferException;
import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private static final String ERROR_DESERIALIZING_EVENT = "Error deserializing event {}";

    @KafkaListener(topics="${notification.topic-name}", groupId = "notifications-service")
    public void consumeEvent(byte[] event){
        try {
            NotificationEvent notification = NotificationEvent.parseFrom(event);
            log.debug("Received Notification Event: [User ids={},Title={},Message={}]",
                    notification.getUserIdsCount(),
                    notification.getTitle(),
                    notification.getMessage());
        }
        catch (InvalidProtocolBufferException e) {
            log.error(ERROR_DESERIALIZING_EVENT, e.getMessage());
        }
    }
}
