package com.pm.authservice.infrastructure.messaging.listener;

import com.pm.authservice.domain.model.VerificationToken;
import com.pm.authservice.domain.model.event.UserRegistered;
import com.pm.authservice.domain.port.out.VerificationTokenPort;
import com.pm.authservice.infrastructure.persistence.entity.UserEventEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserStatus;

import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.pm.authservice.infrastructure.persistence.repository.UserJpaRepository;

import java.util.UUID;

@Component
public class UserRegistrationEventListener extends BaseEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationEventListener.class);

    private final VerificationTokenPort verificationTokenPort;
    private final UserJpaRepository userRepository;

    public UserRegistrationEventListener(VerificationTokenPort verificationTokenPort,
                                         UserJpaRepository userRepository) {
        this.verificationTokenPort = verificationTokenPort;
        this.userRepository = userRepository;
    }

    @EventListener
    public void onUserRegistered(UserRegistered event) {
        UserJpaEntity user = userRepository.findByDomainId(event.domainId()).orElseThrow(() -> new RuntimeException("User not found: " + event.domainId()));

        String verificationToken = UUID.randomUUID().toString();
        verificationTokenPort.save(VerificationToken.create(verificationToken, user.getDomainId()));

        String url = event.applicationUrl() + "/register/verifyEmail?token=" + verificationToken;

        UserEventEntity eventEntity = createUserEvent(user, UserStatus.USER_CREATED);
        saveAndPublishEvents(eventEntity);

        String message = "User with username '" + user.getUsername() + "' has been registered successfully";
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .addUserIds(user.getDomainId().toString())
                .setTitle("User Registration Completed")
                .setMessage(message)
                .setEventType(EventConstants.USER_CREATED_NOTIFICATION)
                .build();
        notificationsProducer.sendEvent(notificationEvent);

        log.info("UserRegistrationEventListener->onUserRegistered->url: {}", url);
    }
}
