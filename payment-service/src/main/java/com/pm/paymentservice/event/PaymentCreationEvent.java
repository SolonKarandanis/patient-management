package com.pm.paymentservice.event;

import com.pm.paymentservice.model.PaymentEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class PaymentCreationEvent extends ApplicationEvent {
    private PaymentEntity payment;

    public PaymentCreationEvent(PaymentEntity payment) {
        super(payment);
        this.payment=payment;
    }
}
