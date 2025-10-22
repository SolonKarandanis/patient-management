package com.pm.patientservice.broker;

import com.pm.patientservice.model.Patient;
import com.pm.patientservice.model.PatientEventEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class KafkaProducerIT {

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
    }

    @Autowired
    private KafkaProducer publisher;

    @Test
    public void testSendEventsToTopic() {
        PatientEventEntity patientEvent = new PatientEventEntity();
        patientEvent.setId(1);
        patientEvent.setName("Patient 1");
        patientEvent.setEmail("skarandanis@gmail.com");
        publisher.sendEvent(patientEvent);
        await().pollInterval(Duration.ofSeconds(3))
                .atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
                    // assert statement
                });
    }
}