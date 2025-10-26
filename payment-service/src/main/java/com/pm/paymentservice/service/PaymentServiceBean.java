package com.pm.paymentservice.service;

import com.pm.paymentservice.domain.PaymentEntity;
import com.pm.paymentservice.domain.PaymentEvent;
import com.pm.paymentservice.domain.PaymentState;
import com.pm.paymentservice.repository.PaymentRepository;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PaymentServiceBean implements PaymentService {

    public static final String PAYMENT_ID_HEADER="payment_id";

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    private final PaymentStateChangeInterceptor interceptor;

    public PaymentServiceBean(
            PaymentRepository paymentRepository,
            StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory,
            PaymentStateChangeInterceptor interceptor
    ) {
        this.paymentRepository = paymentRepository;
        this.stateMachineFactory = stateMachineFactory;
        this.interceptor = interceptor;
    }


    @Override
    public PaymentEntity newPayment(PaymentEntity payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.create(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuthorize(Integer paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.PRE_AUTHORIZE);
        return sm;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Integer paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.AUTH_APPROVED);
        return sm;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Integer paymentId) {
        StateMachine<PaymentState, PaymentEvent> sm = build(paymentId);
        sendEvent(paymentId,sm,PaymentEvent.AUTH_DECLINED);
        return sm;
    }


    private void sendEvent(Integer paymentId, StateMachine<PaymentState, PaymentEvent> sm, PaymentEvent paymentEvent) {
        Message<PaymentEvent> msg = MessageBuilder.withPayload(paymentEvent)
                .setHeader(PAYMENT_ID_HEADER, paymentId).build();
        sm.sendEvent(msg);
    }

    private StateMachine<PaymentState, PaymentEvent> build(Integer paymentId) {
        PaymentEntity payment = paymentRepository.findByIdOpt(paymentId).orElseThrow(() -> new RuntimeException("Payment not found"));
        StateMachine<PaymentState,PaymentEvent> sm = stateMachineFactory.getStateMachine(Integer.toString(payment.getId()));
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(sma->{
            sma.addStateMachineInterceptor(interceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(payment.getState(),null,null,null));
        });
        sm.start();
        return sm;
    }
}
