package com.pm.analyticsservice.service;

import com.pm.analyticsservice.model.PatientEventModel;
import com.pm.analyticsservice.model.PaymentEventModel;
import com.pm.analyticsservice.model.UserEventModel;
import patient.events.PatientEvent;
import payment.events.PaymentEvent;
import user.events.UserEvent;

public interface EventService {

    PatientEventModel savePatientEvent(PatientEvent patientEvent);

    UserEventModel saveUserEvent(UserEvent userEvent);

    PaymentEventModel savePaymentEvent(PaymentEvent paymentEvent);
}
