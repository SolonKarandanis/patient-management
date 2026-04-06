package com.pm.analyticsservice.broker;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.analyticsservice.service.EventService;
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

    private final EventService eventService;

    public KafkaConsumer(EventService eventService) {
        this.eventService = eventService;
    }


    @KafkaListener(topics="patient-events", groupId = "analytics-service")
    public void consumeEvent(byte[] event) throws InvalidProtocolBufferException {
        PatientEvent patientEvent = PatientEvent.parseFrom(event);
        // ... perform any business related to analytics here

        log.info("Received Patient Event: [PatientId={},PatientName={},PatientEmail={}]",
                patientEvent.getPatientId(),
                patientEvent.getName(),
                patientEvent.getEmail());
        eventService.savePatientEvent(patientEvent);
    }

    @KafkaListener(topics="user-events", groupId = "analytics-service")
    public void consumeUserEvent(byte[] event) throws InvalidProtocolBufferException {
        UserEvent userEvent = UserEvent.parseFrom(event);
        log.info("Received User Event: [User Id={},Username={},UserEmail={}]",
                userEvent.getUserId(),
                userEvent.getUsername(),
                userEvent.getEmail());
        eventService.saveUserEvent(userEvent);
    }

    @KafkaListener(topics="payment-events", groupId = "analytics-service")
    public void consumePaymentEvent(byte[] event) throws InvalidProtocolBufferException {
        PaymentEvent paymentEvent = PaymentEvent.parseFrom(event);
        log.info("Received Payment Event: [Payment Id={}, Patient Id={}, Amount={}]",
                paymentEvent.getId(),
                paymentEvent.getPatientId(),
                paymentEvent.getAmount());
        eventService.savePaymentEvent(paymentEvent);
    }
}
