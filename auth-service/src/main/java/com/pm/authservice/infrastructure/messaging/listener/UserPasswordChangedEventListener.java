package com.pm.authservice.infrastructure.messaging.listener;

import com.pm.authservice.domain.model.event.UserPasswordChanged;
import com.pm.authservice.infrastructure.persistence.entity.UserEventEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserStatus;

import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.pm.authservice.infrastructure.persistence.repository.UserJpaRepository;

@Component
public class UserPasswordChangedEventListener extends BaseEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserPasswordChangedEventListener.class);

    private final UserJpaRepository userRepository;

    public UserPasswordChangedEventListener(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    public void onUserPasswordChanged(UserPasswordChanged event) {
        UserJpaEntity user = userRepository.findByDomainId(event.domainId()).orElseThrow(() -> new RuntimeException("User not found: " + event.domainId()));

        UserEventEntity eventEntity = createUserEvent(user, UserStatus.USER_UPDATED);
        saveAndPublishEvents(eventEntity);

        String message = "Password for user '" + user.getUsername() + "' has been changed successfully";
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .addUserIds(user.getDomainId().toString())
                .setTitle("Password Change Completed")
                .setMessage(message)
                .setEventType(EventConstants.USER_PASSWORD_CHANGED_NOTIFICATION)
                .build();
        notificationsProducer.sendEvent(notificationEvent);

        log.info("UserPasswordChangedEventListener->onUserPasswordChanged");
    }
}
