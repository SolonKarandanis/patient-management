package com.pm.authservice.event;

import com.pm.authservice.model.UserEntity;
import com.pm.authservice.model.UserEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UserDeactivationEventListener extends BaseEventListener implements ApplicationListener<UserDeactivationEvent> {
    private static final Logger log = LoggerFactory.getLogger(UserDeactivationEventListener.class);

    @Override
    public void onApplicationEvent(UserDeactivationEvent event) {
        // 1. Get the newly registered user
        UserEntity user = event.getUser();
        //5 Save UserEventEntity and send Kafka event for analytics
        UserEventEntity eventEntity= createUserEvent(user);
        saveAndPublishEvents(eventEntity);
        //6 Send Kafka event for email
        log.info("UserDeactivationEventListener -> onApplicationEvent -> ");
    }
}
