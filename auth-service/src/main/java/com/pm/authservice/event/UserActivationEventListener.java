package com.pm.authservice.event;

import com.pm.authservice.broker.KafkaAnalyticsProducer;
import com.pm.authservice.broker.KafkaNotificationsProducer;
import com.pm.authservice.service.UserEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UserActivationEventListener implements ApplicationListener<UserActivationEvent> {
    private static final Logger log = LoggerFactory.getLogger(UserActivationEventListener.class);

    private final UserEventService userEventService;
    private final KafkaAnalyticsProducer analyticsProducer;
    private final KafkaNotificationsProducer notificationsProducer;

    public UserActivationEventListener(UserEventService userEventService, KafkaAnalyticsProducer analyticsProducer, KafkaNotificationsProducer notificationsProducer) {
        this.userEventService = userEventService;
        this.analyticsProducer = analyticsProducer;
        this.notificationsProducer = notificationsProducer;
    }

    @Override
    public void onApplicationEvent(UserActivationEvent event) {

    }
}
