package com.pm.authservice.event;

import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.model.UserEventEntity;
import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UserActivationEventListener extends BaseEventListener implements ApplicationListener<UserActivationEvent> {
    private static final Logger log = LoggerFactory.getLogger(UserActivationEventListener.class);

    @Override
    public void onApplicationEvent(UserActivationEvent event) {
        // 1. Get the newly registered user
        UserEntity user = event.getUser();
        //5 Save UserEventEntity and send Kafka event for analytics
        UserEventEntity eventEntity= createUserEvent(user);
        saveAndPublishEvents(eventEntity);
        //6 Send Kafka event for notification
        StringBuilder sb =new StringBuilder();
        sb.append("User with username '").append(user.getUsername()).append("' has been activated successfully");
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .addUserIds(user.getPublicId().toString())
                .setTitle("User Activation Completed")
                .setMessage(sb.toString())
                .setEventType(EventConstants.USER_ACTIVATED_NOTIFICATION)
                .build();
        notificationsProducer.sendEvent(notificationEvent);
        //7 Send Kafka event for email
        log.info("UserActivationEventListener -> onApplicationEvent -> ");
    }
}
