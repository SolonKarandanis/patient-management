package com.pm.notificationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Value("${notification.topic-name}")
    private String notificationTopicName;

    @Bean
    public NewTopic createTopic() {
        return new NewTopic(notificationTopicName, 3, (short) 1);
    }
}
