package com.pm.authservice.user.event;

import com.pm.authservice.event.BaseEventListener;
import com.pm.authservice.event.EventConstants;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.user.model.UserEventEntity;
import com.pm.authservice.user.model.UserStatus;
import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class UserDeletionEventListener extends BaseEventListener implements ApplicationListener<UserDeletionEvent> {
    private static final Logger log = LoggerFactory.getLogger(UserDeletionEventListener.class);

    @Override
    public void onApplicationEvent(UserDeletionEvent event) {
        // 1. Get the newly registered user
        UserEntity user = event.getUser();
        //5 Save UserEventEntity and send Kafka event for analytics
        UserEventEntity eventEntity= createUserEvent(user, UserStatus.USER_DELETED);
        saveAndPublishEvents(eventEntity);
        //6 Send Kafka event for notification
        StringBuilder sb =new StringBuilder();
        sb.append("User with username '").append(user.getUsername()).append("' has been deleted successfully");
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .addUserIds(user.getPublicId().toString())
                .setTitle("User Deletion Completed")
                .setMessage(sb.toString())
                .setEventType(EventConstants.USER_DELETED_NOTIFICATION)
                .build();
        notificationsProducer.sendEvent(notificationEvent);
        //7 Send Kafka event for email
        log.info("UserDeletionEventListener -> onApplicationEvent -> ");
    }
}
