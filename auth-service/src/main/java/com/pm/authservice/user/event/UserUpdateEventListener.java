package com.pm.authservice.user.event;

import com.pm.authservice.domain.model.event.UserUpdated;
import com.pm.authservice.event.BaseEventListener;
import com.pm.authservice.event.EventConstants;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.user.model.UserEventEntity;
import com.pm.authservice.user.model.UserStatus;
import com.pm.authservice.user.service.UserService;
import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserUpdateEventListener extends BaseEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserUpdateEventListener.class);

    private final UserService userService;

    public UserUpdateEventListener(UserService userService) {
        this.userService = userService;
    }

    @EventListener
    public void onUserUpdated(UserUpdated event) {
        UserJpaEntity user = userService.findByPublicId(event.domainId().toString());

        UserEventEntity eventEntity = createUserEvent(user, UserStatus.USER_UPDATED);
        saveAndPublishEvents(eventEntity);

        StringBuilder sb = new StringBuilder();
        sb.append("User with username '").append(user.getUsername())
          .append("' has been updated successfully");
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .addUserIds(user.getDomainId().toString())
                .setTitle("User Update Completed")
                .setMessage(sb.toString())
                .setEventType(EventConstants.USER_UPDATED_NOTIFICATION)
                .build();
        notificationsProducer.sendEvent(notificationEvent);

        log.info("UserUpdateEventListener->onUserUpdated");
    }
}
