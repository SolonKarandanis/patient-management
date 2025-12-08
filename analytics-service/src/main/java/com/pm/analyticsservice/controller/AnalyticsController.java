package com.pm.analyticsservice.controller;

import com.pm.analyticsservice.model.dto.DailyEventCount;
import com.pm.analyticsservice.model.dto.DailyPaymentSummary;
import com.pm.analyticsservice.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/patients/daily-summary")
    public List<DailyEventCount> getPatientDailySummary() {
        return analyticsService.getPatientEventDailySummary();
    }

    @GetMapping("/users/daily-summary")
    public List<DailyEventCount> getUserDailySummary() {
        return analyticsService.getUserEventDailySummary();
    }

    @GetMapping("/payments/daily-summary")
    public List<DailyPaymentSummary> getPaymentDailySummary() {
        return analyticsService.getPaymentDailySummary();
    }
}
