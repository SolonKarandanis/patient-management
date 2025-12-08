package com.pm.analyticsservice.service;

import com.pm.analyticsservice.model.dto.DailyEventCount;
import com.pm.analyticsservice.model.dto.DailyPaymentSummary;
import com.pm.analyticsservice.repository.PatientEventRepository;
import com.pm.analyticsservice.repository.PaymentEventRepository;
import com.pm.analyticsservice.repository.UserEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceBean implements AnalyticsService {
    private final PatientEventRepository patientEventRepository;
    private final UserEventRepository userEventRepository;
    private final PaymentEventRepository paymentEventRepository;

    @Override
    public List<DailyEventCount> getPatientEventDailySummary() {
        return patientEventRepository.getDailyPatientSummary();
    }

    @Override
    public List<DailyEventCount> getUserEventDailySummary() {
        return userEventRepository.getDailyUserSummary();
    }

    @Override
    public List<DailyPaymentSummary> getPaymentDailySummary() {
        return paymentEventRepository.getDailyPaymentSummary();
    }
}
