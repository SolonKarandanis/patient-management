package com.pm.camelintegration.config;

import jakarta.jms.ConnectionFactory;
import org.apache.camel.component.jms.JmsComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JmsConfig {

    @Bean
    public JmsComponent jms(ConnectionFactory connectionFactory) {
        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(connectionFactory);
        return jmsComponent;
    }
}
