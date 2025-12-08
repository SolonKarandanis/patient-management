package com.pm.analyticsservice.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Table("payment_events")
public class PaymentEvent {
    @Id
    private UUID id;
    private String patientId;
    private String state;
    private Double amount;
    private String createdDate;
    private LocalDateTime event_timestamp;
}
