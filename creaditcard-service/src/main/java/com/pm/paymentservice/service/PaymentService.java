package com.pm.paymentservice.service;

import com.pm.paymentservice.domain.PaymentEntity;
import com.pm.paymentservice.domain.PaymentEvent;
import com.pm.paymentservice.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    PaymentEntity newPayment(PaymentEntity payment);
    StateMachine<PaymentState, PaymentEvent> preAuthorize(Integer paymentId);
    StateMachine<PaymentState, PaymentEvent> authorizePayment(Integer paymentId);
    StateMachine<PaymentState, PaymentEvent> declineAuth(Integer paymentId);
}
