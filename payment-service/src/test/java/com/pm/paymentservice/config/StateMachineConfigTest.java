package com.pm.paymentservice.config;

import com.pm.paymentservice.model.PaymentEvent;
import com.pm.paymentservice.model.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

@Slf4j
@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    private StateMachineFactory<PaymentState, PaymentEvent> factory;

//    @Test
    void testNewStateMachine() {
        log.info("StateMachineConfigTest.testNewStateMachine");
        StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(UUID.randomUUID());
        sm.start();
        sm.sendEvent(PaymentEvent.PRE_AUTHORIZE);
        sm.sendEvent(PaymentEvent.PRE_AUTH_APPROVED);
    }

}