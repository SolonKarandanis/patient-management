package com.pm.analyticsservice.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.messaging.MessageChannel;

@Configuration
public class EventProcessingIntegrationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventProcessingIntegrationConfig.class);
    private static final String KAFKA_TOPIC = "latest-month-payments-events";
    private static final String EVENT_TYPE_HEADER = "event_type";

    @Bean
    public MessageChannel kafkaInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel paymentProcessedChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel paymentFlaggedChannel() {
        return new DirectChannel();
    }

    @Bean
    public ContainerProperties containerProperties() {
        return new ContainerProperties(KAFKA_TOPIC);
    }

    @Bean
    public KafkaMessageListenerContainer<String, String> messageListenerContainer(
            ConsumerFactory<String, String> consumerFactory,
            ContainerProperties containerProperties) {
        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }

    @Bean
    public IntegrationFlow kafkaInboundFlow(
            KafkaMessageListenerContainer<String, String> messageListenerContainer) {
        return IntegrationFlow.from(Kafka.messageDrivenChannelAdapter(messageListenerContainer))
                .channel(kafkaInputChannel())
                .get();
    }

    @Bean
    public HeaderValueRouter router() {
        HeaderValueRouter router = new HeaderValueRouter(EVENT_TYPE_HEADER);
        router.setChannelMapping("payment-processed", "paymentProcessedChannel");
        router.setChannelMapping("payment-flagged", "paymentFlaggedChannel");
        return router;
    }

    @Bean
    public IntegrationFlow mainFlow() {
        return IntegrationFlow.from(kafkaInputChannel())
                .route(router())
                .get();
    }

    @Bean
    public IntegrationFlow paymentProcessedFlow() {
        return IntegrationFlow.from(paymentProcessedChannel())
                .handle(m -> {
                    LOGGER.info("Handling payment-processed event: {}", m.getPayload());
                    LOGGER.info("Processed a standard payment event");
                })
                .get();
    }

    @Bean
    public IntegrationFlow paymentFlaggedFlow() {
        return IntegrationFlow.from(paymentFlaggedChannel())
                .handle(m -> {
                    LOGGER.info("Handling payment-flagged event: {}", m.getPayload());
                    LOGGER.info("Processed a flagged payment event");
                })
                .get();
    }
}
