package com.pm.authservice.infrastructure.messaging.listener;

import com.pm.authservice.domain.model.event.UserDeactivated;
import com.pm.authservice.infrastructure.persistence.entity.UserEventEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserStatus;
import com.pm.authservice.user.service.UserService;
import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserDeactivationEventListener extends BaseEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserDeactivationEventListener.class);

    private final UserService userService;

    public UserDeactivationEventListener(UserService userService) {
        this.userService = userService;
    }

    @EventListener
    public void onUserDeactivated(UserDeactivated event) {
        UserJpaEntity user = userService.findByPublicId(event.domainId().toString());

        UserEventEntity eventEntity = createUserEvent(user, UserStatus.USER_DEACTIVATED);
        saveAndPublishEvents(eventEntity);

        String message = "User with username '" + user.getUsername() + "' has been deactivated successfully";
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .addUserIds(user.getDomainId().toString())
                .setTitle("User Deactivation Completed")
                .setMessage(message)
                .setEventType(EventConstants.USER_DEACTIVATED_NOTIFICATION)
                .build();
        notificationsProducer.sendEvent(notificationEvent);

        log.info("UserDeactivationEventListener->onUserDeactivated");
    }
}
