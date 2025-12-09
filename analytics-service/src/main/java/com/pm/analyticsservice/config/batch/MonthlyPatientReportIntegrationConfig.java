package com.pm.analyticsservice.config.batch;

import com.pm.analyticsservice.config.AppConstants;
import org.springframework.messaging.MessageChannel;
import lombok.RequiredArgsConstructor;
import notification.events.NotificationEvent;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.integration.http.inbound.RequestMapping;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class MonthlyPatientReportIntegrationConfig {

    private final JobLauncher jobLauncher;
    private final Job monthlyReportJob;
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;


    @Bean
    public MessageChannel launchJobChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel notificationChannel() {
        return new DirectChannel();
    }

    @Bean

    public HttpRequestHandlingMessagingGateway httpInboundGateway() {
        HttpRequestHandlingMessagingGateway gateway = new HttpRequestHandlingMessagingGateway(true);
        RequestMapping mapping = new RequestMapping();
        mapping.setPathPatterns("/jobs/start-patient-report");
        mapping.setMethods(HttpMethod.POST);
        gateway.setRequestMapping(mapping);
        gateway.setRequestChannel(launchJobChannel());
        gateway.setRequestPayloadTypeClass(List.class);
        return gateway;
    }

    @ServiceActivator(inputChannel = "launchJobChannel")
    public void launchJob(List<String> userIds) throws Exception {
        jobLauncher.run(monthlyReportJob, new JobParametersBuilder()
                .addLong("startTime", System.currentTimeMillis())
                .addString("userIds", String.join(",", userIds))
                .toJobParameters());
    }

    @Bean
    @ServiceActivator(inputChannel = AppConstants.NOTIFICATION_CHANNEL)
    public KafkaProducerMessageHandler<String, NotificationEvent> kafkaOutboundAdapter() {
        KafkaProducerMessageHandler<String, NotificationEvent> handler =
                new KafkaProducerMessageHandler<>(kafkaTemplate);
        handler.setTopicExpression(new LiteralExpression(AppConstants.NOTIFICATION_EVENTS));
        return handler;
    }
}
