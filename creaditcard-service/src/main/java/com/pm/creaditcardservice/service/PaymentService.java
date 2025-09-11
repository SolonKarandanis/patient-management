package com.pm.creaditcardservice.service;

import com.pm.creaditcardservice.domain.PaymentEntity;
import com.pm.creaditcardservice.domain.PaymentEvent;
import com.pm.creaditcardservice.domain.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {

    PaymentEntity newPayment(PaymentEntity payment);
    StateMachine<PaymentState, PaymentEvent> preAuthorize(Integer paymentId);
    StateMachine<PaymentState, PaymentEvent> authorizePayment(Integer paymentId);
    StateMachine<PaymentState, PaymentEvent> declineAuth(Integer paymentId);
}
