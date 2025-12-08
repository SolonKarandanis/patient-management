package com.pm.analyticsservice.model.dto;

import lombok.Value;

import java.time.LocalDate;

@Value
public class DailyPaymentSummary {
    LocalDate eventDate;
    String state;
    long totalPayments;
    double totalAmount;
}
