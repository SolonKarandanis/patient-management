package com.pm.patientservice.broker;

import com.pm.patientservice.model.Patient;
import com.pm.patientservice.model.PatientEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducer implements Producer<PatientEventEntity> {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(PatientEventEntity patientEntity) {
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patientEntity.getPatientId().toString())
//                .setName(patient.getName())
//                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();

        try {
            CompletableFuture<SendResult<String, byte[]>> message = kafkaTemplate.send("patient", event.toByteArray());
        } catch (Exception e) {
            log.error("Error sending PatientCreated event: {}", event);
        }
    }
}
