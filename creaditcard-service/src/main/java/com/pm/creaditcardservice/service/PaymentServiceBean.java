package com.pm.creaditcardservice.service;

import com.pm.creaditcardservice.domain.PaymentEntity;
import com.pm.creaditcardservice.domain.PaymentEvent;
import com.pm.creaditcardservice.domain.PaymentState;
import com.pm.creaditcardservice.repository.PaymentRepository;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceBean implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;

    public PaymentServiceBean(PaymentRepository paymentRepository, StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory) {
        this.paymentRepository = paymentRepository;
        this.stateMachineFactory = stateMachineFactory;
    }

    @Override
    public PaymentEntity newPayment(PaymentEntity payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuthorize(Integer paymentId) {
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Integer paymentId) {
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Integer paymentId) {
        return null;
    }

    private StateMachine<PaymentState, PaymentEvent> build(Integer paymentId) {
        PaymentEntity payment = paymentRepository.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found"));
        StateMachine<PaymentState,PaymentEvent> sm = stateMachineFactory.getStateMachine(Integer.toString(payment.getId()));
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(sma->{
            sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(),null,null,null));
        });
        sm.start();
        return sm;
    }
}
