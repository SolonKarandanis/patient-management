package com.pm.analyticsservice.broker;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;
import payment.events.PaymentEvent;
import user.events.UserEvent;

@Service
public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    private static final String ERROR_DESERIALIZING_EVENT = "Error deserializing event {}";


    @KafkaListener(topics="patient-events", groupId = "analytics-service")
    public void consumeEvent(byte[] event) {
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            // ... perform any business related to analytics here

            log.info("Received Patient Event: [PatientId={},PatientName={},PatientEmail={}]",
                    patientEvent.getPatientId(),
                    patientEvent.getName(),
                    patientEvent.getEmail());
        } catch (InvalidProtocolBufferException e) {
            log.error(ERROR_DESERIALIZING_EVENT, e.getMessage());
        }
    }

    @KafkaListener(topics="user-events", groupId = "analytics-service")
    public void consumeUserEvent(byte[] event){
        try {
            UserEvent userEvent = UserEvent.parseFrom(event);
            log.info("Received User Event: [User Id={},Username={},UserEmail={}]",
                    userEvent.getUserId(),
                    userEvent.getUsername(),
                    userEvent.getEmail());
        } catch (InvalidProtocolBufferException e) {
            log.error(ERROR_DESERIALIZING_EVENT, e.getMessage());
        }
    }

    @KafkaListener(topics="payment-events", groupId = "analytics-service")
    public void consumePaymentEvent(byte[] event){
        try {
            PaymentEvent paymentEvent = PaymentEvent.parseFrom(event);
            log.info("Received Payment Event: [Payment Id={}, Patient Id={}, Amount={}]",
                    paymentEvent.getId(),
                    paymentEvent.getPatientId(),
                    paymentEvent.getAmount());
        } catch (InvalidProtocolBufferException e) {
            log.error(ERROR_DESERIALIZING_EVENT, e.getMessage());
        }
    }
}
