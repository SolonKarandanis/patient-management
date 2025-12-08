package com.pm.analyticsservice.model;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import payment.events.PaymentEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Table("payment_events")
public class PaymentEventModel {
    @Id
    private UUID id;
    private String patientId;
    private String state;
    private Double amount;
    private LocalDateTime createdDate;
    private LocalDateTime event_timestamp;

    public static PaymentEventModel  createFromEvent(PaymentEvent paymentEvent) {
        PaymentEventModel paymentEventModel = new PaymentEventModel();
        paymentEventModel.setId(UUID.randomUUID());
        paymentEventModel.setPatientId(paymentEvent.getPatientId());
        paymentEventModel.setState(paymentEvent.getState());
        paymentEventModel.setAmount(paymentEvent.getAmount());
        paymentEventModel.setCreatedDate(LocalDateTime.parse(paymentEvent.getCreatedDate()));
        paymentEventModel.setEvent_timestamp(LocalDateTime.now());
        return paymentEventModel;
    }

    private void setId(UUID id) {
        this.id = id;
    }

    private void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    private void setState(String state) {
        this.state = state;
    }

    private void setAmount(Double amount) {
        this.amount = amount;
    }

    private void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    private void setEvent_timestamp(LocalDateTime event_timestamp) {
        this.event_timestamp = event_timestamp;
    }
}
