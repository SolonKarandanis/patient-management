package com.pm.authservice.infrastructure.messaging.listener;

import com.pm.authservice.domain.model.event.UserActivated;
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
public class UserActivationEventListener extends BaseEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserActivationEventListener.class);

    private final UserJpaRepository userRepository;

    public UserActivationEventListener(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @EventListener
    public void onUserActivated(UserActivated event) {
        UserJpaEntity user = userRepository.findByDomainId(event.domainId()).orElseThrow(() -> new RuntimeException("User not found: " + event.domainId()));

        UserEventEntity eventEntity = createUserEvent(user, UserStatus.USER_ACTIVATED);
        saveAndPublishEvents(eventEntity);

        String message = "User with username '" + user.getUsername() + "' has been activated successfully";
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .addUserIds(user.getDomainId().toString())
                .setTitle("User Activation Completed")
                .setMessage(message)
                .setEventType(EventConstants.USER_ACTIVATED_NOTIFICATION)
                .build();
        notificationsProducer.sendEvent(notificationEvent);

        log.info("UserActivationEventListener->onUserActivated");
    }
}
