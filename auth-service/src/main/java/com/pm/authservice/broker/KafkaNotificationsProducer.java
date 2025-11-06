package com.pm.authservice.broker;

import com.pm.authservice.model.UserEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaNotificationsProducer implements Producer<UserEventEntity>{

    private static final Logger log = LoggerFactory.getLogger(KafkaNotificationsProducer.class);

    @Value("${notification.topic-name}")
    private String topicName;

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaNotificationsProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendEvent(UserEventEntity object) {

    }
}
