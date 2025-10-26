package com.pm.paymentservice.service;

import com.pm.paymentservice.model.PaymentEntity;
import com.pm.paymentservice.model.PaymentEvent;
import com.pm.paymentservice.model.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    PaymentEntity newPayment(PaymentEntity payment);
    StateMachine<PaymentState, PaymentEvent> preAuthorize(Integer paymentId);
    StateMachine<PaymentState, PaymentEvent> authorizePayment(Integer paymentId);
    StateMachine<PaymentState, PaymentEvent> declineAuth(Integer paymentId);
}
