package com.pm.authservice.infrastructure.messaging.listener;

import com.pm.authservice.domain.model.event.UserDeleted;
import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserDeletionEventListener extends BaseEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserDeletionEventListener.class);

    @EventListener
    public void onUserDeleted(UserDeleted event) {
        String message = "User with username '" + event.username() + "' has been deleted successfully";
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .addUserIds(event.domainId().toString())
                .setTitle("User Deletion Completed")
                .setMessage(message)
                .setEventType(EventConstants.USER_DELETED_NOTIFICATION)
                .build();
        notificationsProducer.sendEvent(notificationEvent);

        log.info("UserDeletionEventListener->onUserDeleted");
    }
}
