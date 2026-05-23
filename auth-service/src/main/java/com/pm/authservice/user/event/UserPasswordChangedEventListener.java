package com.pm.authservice.user.event;

import com.pm.authservice.domain.model.event.UserPasswordChanged;
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
public class UserPasswordChangedEventListener extends BaseEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserPasswordChangedEventListener.class);

    private final UserService userService;

    public UserPasswordChangedEventListener(UserService userService) {
        this.userService = userService;
    }

    @EventListener
    public void onUserPasswordChanged(UserPasswordChanged event) {
        UserJpaEntity user = userService.findByPublicId(event.domainId().toString());

        UserEventEntity eventEntity = createUserEvent(user, UserStatus.USER_UPDATED);
        saveAndPublishEvents(eventEntity);

        StringBuilder sb = new StringBuilder();
        sb.append("Password for user '").append(user.getUsername())
          .append("' has been changed successfully");
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .addUserIds(user.getDomainId().toString())
                .setTitle("Password Change Completed")
                .setMessage(sb.toString())
                .setEventType(EventConstants.USER_PASSWORD_CHANGED_NOTIFICATION)
                .build();
        notificationsProducer.sendEvent(notificationEvent);

        log.info("UserPasswordChangedEventListener->onUserPasswordChanged");
    }
}
