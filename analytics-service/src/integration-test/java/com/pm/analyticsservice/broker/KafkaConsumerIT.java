package com.pm.analyticsservice.broker;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import patient.events.PatientEvent;

import java.time.Duration;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Slf4j
public class KafkaConsumerIT {

    @Container
    static final GenericContainer<?> kafka =
            new GenericContainer<>(DockerImageName.parse("confluentinc/cp-kafka:7.9.0"))
                    .withExposedPorts(9092)
                    .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                            cmd.getHostConfig().withPortBindings(
                                    new com.github.dockerjava.api.model.PortBinding(
                                            com.github.dockerjava.api.model.Ports.Binding.bindPort(9092),
                                            new com.github.dockerjava.api.model.ExposedPort(9092)
                                    )
                            )
                    ))
                    .withEnv("KAFKA_NODE_ID", "1")
                    .withEnv("KAFKA_PROCESS_ROLES", "broker,controller")
                    .withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT")
                    .withEnv("KAFKA_ADVERTISED_LISTENERS", "PLAINTEXT://localhost:9092")
                    .withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "PLAINTEXT")
                    .withEnv("KAFKA_LISTENERS", "PLAINTEXT://0.0.0.0:9092,CONTROLLER://localhost:9093")
                    .withEnv("KAFKA_CONTROLLER_QUORUM_VOTERS", "1@localhost:9093")
                    .withEnv("KAFKA_CONTROLLER_LISTENER_NAMES", "CONTROLLER")
                    .withEnv("CLUSTER_ID", UUID.randomUUID().toString())
                    .withEnv("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
                    .waitingFor(Wait.forLogMessage(".*Transitioning from RECOVERY to RUNNING.*", 1));

    @DynamicPropertySource
    static void overridePropertiesInternal(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers",
                () -> String.format("%s:%d", kafka.getHost(), kafka.getMappedPort(9092)));
        registry.add("spring.kafka.producer.value-serializer", () -> "org.apache.kafka.common.serialization.ByteArraySerializer");
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
