package com.pm.paymentservice.event;

import com.pm.paymentservice.broker.KafkaAnalyticsProducer;
import com.pm.paymentservice.model.PaymentEntity;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentCreationEventListener implements ApplicationListener<PaymentCreationEvent> {

    private final KafkaAnalyticsProducer analyticsProducer;

    public PaymentCreationEventListener(KafkaAnalyticsProducer analyticsProducer) {
        this.analyticsProducer = analyticsProducer;
    }

    @Override
    public void onApplicationEvent(PaymentCreationEvent event) {
        PaymentEntity paymentEntity = event.getPayment();
        analyticsProducer.sendEvent(paymentEntity);
    }
}
