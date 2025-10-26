package com.pm.paymentservice.service;

import com.pm.paymentservice.domain.PaymentEntity;
import com.pm.paymentservice.domain.PaymentEvent;
import com.pm.paymentservice.domain.PaymentState;
import com.pm.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository paymentRepository;

    @Override
    public void preStateChange(
            State<PaymentState, PaymentEvent> state,
            Message<PaymentEvent> message,
            Transition<PaymentState, PaymentEvent> transition,
            StateMachine<PaymentState, PaymentEvent> stateMachine,
            StateMachine<PaymentState, PaymentEvent> rootStateMachine
    ) {
        Optional.ofNullable(message).flatMap(msg ->
                Optional.ofNullable((Integer) msg.getHeaders().getOrDefault(PaymentServiceBean.PAYMENT_ID_HEADER, -1)))
                .ifPresent(paymentId -> {
                    PaymentEntity payment = paymentRepository.findByIdOpt(paymentId).orElseThrow(() -> new RuntimeException("Payment not found"));
                    payment.setState(state.getId());
                    paymentRepository.create(payment);
                });
    }
}
