package com.pm.patientservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import jakarta.jms.Queue;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.util.ErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@EnableJms
@Configuration
@Slf4j
public class JmsConfig {

    private static final String AMQ_URL_SCHEME = "tcp://";

    @Value("${patient.processing.queue-name}")
    private String patientQueueName;

    @Value("${artemis.broker.host}")
    private String brokerHost;

    @Value("${artemis.broker.port}")
    private String brokerPort;

    @Value("${artemis.broker.user}")
    private String brokerUsername;

    @Value("${artemis.broker.password}")
    private String brokerPassword;

    @Value("${artemis.broker.message.type:pojo}")
    private String eventBrokerMessageType;

    public static final String EVENT_BROKER_MESSAGE_TYPE_JSON = "json";
    public static final String EVENT_BROKER_MESSAGE_TYPE_POJO = "pojo";

    @PostConstruct
    public void postConstruct() {
        log.debug(" JmsConfig.postConstruct [HOST={}, PORT={}, USER={}] ", brokerHost, brokerPort, brokerUsername);
    }

    public String getBrokerUrl() {
        return AMQ_URL_SCHEME + brokerHost + ":" + brokerPort;
    }

    public ConnectionFactory getConnectionFactory() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(getBrokerUrl());
        connectionFactory.setUser(brokerUsername);
        connectionFactory.setPassword(brokerPassword);
        return connectionFactory;
    }

    @Bean("artemisPatientServiceConnectionFactory")
    ConnectionFactory connectionFactory() throws JMSException {
        log.info("JmsConfig.connectionFactory: Initializing ActiveMQConnectionFactory");
        return getConnectionFactory();
    }


    @Bean
    public JmsListenerContainerFactory<DefaultMessageListenerContainer> queueJmsListenerContainerFactory(
            @Qualifier("artemisPatientServiceConnectionFactory") ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setPubSubDomain(false);
        factory.setSessionTransacted(true);
        factory.setErrorHandler(customErrorHandler());
        factory.setMessageConverter(simpleMessageConverter());
        return factory;
    }

    @Bean
    JmsListenerContainerFactory<DefaultMessageListenerContainer> topicJmsListenerContainerFactory(
            @Qualifier("artemisPatientServiceConnectionFactory") ConnectionFactory connectionFactory,
            DefaultJmsListenerContainerFactoryConfigurer configurer) {
        log.info("[CY] in topicJmsListenerContainerFactory ");
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setSubscriptionDurable(true);
        factory.setPubSubDomain(true);
        factory.setSessionTransacted(true);
        factory.setSubscriptionShared(true);
        factory.setBackOff(new FixedBackOff(5000, 6));
        factory.setMessageConverter(simpleMessageConverter());
        factory.setErrorHandler(customErrorHandler());
        return factory;
    }

    @Bean
    public ErrorHandler customErrorHandler() {
        return t -> {
            // Custom error handling logic
            log.error("Error occurred while processing JMS message: " + t.getMessage());
        };
    }

    @Bean
    public MessageConverter jacksonJmsMessageConverter() {
        // Use MappingJackson2MessageConverter to convert JSON
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    // This converter is used for POJO serialization/deserialization event messages
    @Bean(name = { "simpleMessageConverter" })
    public SimpleMessageConverter simpleMessageConverter() {
        return new SimpleMessageConverter();
    }

    @Bean
    public JmsTemplate jmsTemplate(@Qualifier("artemisPatientServiceConnectionFactory") ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory);
        template.setPubSubDomain(true);
        template.setDeliveryPersistent(true);
        if (EVENT_BROKER_MESSAGE_TYPE_JSON.equals(eventBrokerMessageType)) {
            template.setMessageConverter(jacksonJmsMessageConverter());
        } else if (EVENT_BROKER_MESSAGE_TYPE_POJO.equals(eventBrokerMessageType)) {
            template.setMessageConverter(simpleMessageConverter());
        }
        return template;
    }

    @Bean("patientQueue")
    public Queue patientQueue() {
        return new ActiveMQQueue(patientQueueName);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

