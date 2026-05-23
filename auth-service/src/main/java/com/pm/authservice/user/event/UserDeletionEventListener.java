package com.pm.authservice.user.event;

import com.pm.authservice.domain.model.event.UserDeleted;
import com.pm.authservice.event.BaseEventListener;
import com.pm.authservice.event.EventConstants;
import com.pm.authservice.user.model.UserEventEntity;
import com.pm.authservice.user.model.UserStatus;
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
        StringBuilder sb = new StringBuilder();
        sb.append("User with username '").append(event.username())
          .append("' has been deleted successfully");
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .addUserIds(event.domainId().toString())
                .setTitle("User Deletion Completed")
                .setMessage(sb.toString())
                .setEventType(EventConstants.USER_DELETED_NOTIFICATION)
                .build();
        notificationsProducer.sendEvent(notificationEvent);

        log.info("UserDeletionEventListener->onUserDeleted");
    }
}
