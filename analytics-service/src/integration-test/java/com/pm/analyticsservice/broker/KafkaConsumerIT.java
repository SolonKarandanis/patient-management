package com.pm.analyticsservice.broker;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import patient.events.PatientEvent;

import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Slf4j
public class KafkaConsumerIT {

    @Container
    static final KafkaContainer kafka =
            new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.9.0"));

    @DynamicPropertySource
    static void overridePropertiesInternal(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @Test
    public void testConsumeEvents(){
        log.info("testConsumeEvents method execution started...");
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId("1")
                .setName("solon")
                .setEmail("skarandanis@gmail.com")
                .setEventType("PATIENT_CREATED")
                .build();
        kafkaTemplate.send("patient", event.toByteArray());
        log.info("testConsumeEvents method execution ended...");
        await().pollInterval(Duration.ofSeconds(3)).atMost(10, SECONDS).untilAsserted(() -> {

        });
    }
}
