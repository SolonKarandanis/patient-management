package com.pm.analyticsservice.model.dto;

import lombok.Value;

import java.time.LocalDate;

@Value
public class DailyEventCount {
    LocalDate eventDate;
    String eventType;
    long totalEvents;
}
