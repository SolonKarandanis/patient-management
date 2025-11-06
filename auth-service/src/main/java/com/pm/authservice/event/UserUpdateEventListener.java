package com.pm.authservice.event;

import com.pm.authservice.model.UserEntity;
import com.pm.authservice.model.UserEventEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UserUpdateEventListener extends BaseEventListener implements ApplicationListener<UserUpdateEvent> {
    private static final Logger log = LoggerFactory.getLogger(UserUpdateEventListener.class);

    @Override
    public void onApplicationEvent(UserUpdateEvent event) {
        // 1. Get the newly registered user
        UserEntity user = event.getUser();
        //5 Save UserEventEntity and send Kafka event for analytics
        UserEventEntity eventEntity= createUserEvent(user);
        saveAndPublishEvents(eventEntity);
        //6 Send Kafka event for email
        log.info("UserUpdateEventListener -> onApplicationEvent -> ");
    }
}
