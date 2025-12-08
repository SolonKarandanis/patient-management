package com.pm.analyticsservice.service;

import com.pm.analyticsservice.model.PatientEventModel;
import com.pm.analyticsservice.model.PaymentEventModel;
import com.pm.analyticsservice.model.UserEventModel;
import com.pm.analyticsservice.repository.PatientEventRepository;
import com.pm.analyticsservice.repository.PaymentEventRepository;
import com.pm.analyticsservice.repository.UserEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import patient.events.PatientEvent;
import payment.events.PaymentEvent;
import user.events.UserEvent;

@Service
@Transactional(readOnly = true)
@Slf4j
public class EventServiceBean implements EventService{

    private final PatientEventRepository patientEventRepository;
    private final UserEventRepository userEventRepository;
    private final PaymentEventRepository paymentEventRepository;

    public EventServiceBean(
            PatientEventRepository patientEventRepository,
            UserEventRepository userEventRepository,
            PaymentEventRepository paymentEventRepository) {
        this.patientEventRepository = patientEventRepository;
        this.userEventRepository = userEventRepository;
        this.paymentEventRepository = paymentEventRepository;
    }


    @Transactional(propagation= Propagation.REQUIRED)
    @Override
    public PatientEventModel savePatientEvent(PatientEvent patientEvent) {
        PatientEventModel model = PatientEventModel.createFromEvent(patientEvent);
        return patientEventRepository.save(model);
    }

    @Transactional(propagation= Propagation.REQUIRED)
    @Override
    public UserEventModel saveUserEvent(UserEvent userEvent) {
        UserEventModel model = UserEventModel.createFromEvent(userEvent);
        return userEventRepository.save(model);
    }

    @Transactional(propagation= Propagation.REQUIRED)
    @Override
    public PaymentEventModel savePaymentEvent(PaymentEvent paymentEvent) {
        PaymentEventModel model = PaymentEventModel.createFromEvent(paymentEvent);
        return paymentEventRepository.save(model);
    }
}
