package com.pm.analyticsservice.service;

import com.pm.analyticsservice.model.dto.DailyEventCount;
import com.pm.analyticsservice.model.dto.DailyPaymentSummary;

import java.util.List;

public interface AnalyticsService {
    List<DailyEventCount> getPatientEventDailySummary();
    List<DailyEventCount> getUserEventDailySummary();
    List<DailyPaymentSummary> getPaymentDailySummary();
}
