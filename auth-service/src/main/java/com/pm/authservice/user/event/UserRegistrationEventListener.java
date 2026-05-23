package com.pm.authservice.user.event;

import com.pm.authservice.domain.model.event.UserRegistered;
import com.pm.authservice.event.BaseEventListener;
import com.pm.authservice.event.EventConstants;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.user.model.UserEventEntity;
import com.pm.authservice.user.model.UserStatus;
import com.pm.authservice.user.service.UserService;
import com.pm.authservice.service.VerificationTokenService;
import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserRegistrationEventListener extends BaseEventListener {

    private static final Logger log = LoggerFactory.getLogger(UserRegistrationEventListener.class);

    private final VerificationTokenService tokenService;
    private final UserService userService;

    public UserRegistrationEventListener(VerificationTokenService tokenService,
                                         UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @EventListener
    public void onUserRegistered(UserRegistered event) {
        UserJpaEntity user = userService.findByPublicId(event.domainId().toString());

        String verificationToken = UUID.randomUUID().toString();
        tokenService.saveUserVerificationToken(user, verificationToken);

        String url = event.applicationUrl() + "/register/verifyEmail?token=" + verificationToken;

        UserEventEntity eventEntity = createUserEvent(user, UserStatus.USER_CREATED);
        saveAndPublishEvents(eventEntity);

        StringBuilder sb = new StringBuilder();
        sb.append("User with username '").append(user.getUsername())
          .append("' has been registered successfully");
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .addUserIds(user.getDomainId().toString())
                .setTitle("User Registration Completed")
                .setMessage(sb.toString())
                .setEventType(EventConstants.USER_CREATED_NOTIFICATION)
                .build();
        notificationsProducer.sendEvent(notificationEvent);

        log.info("UserRegistrationEventListener->onUserRegistered->url: {}", url);
    }
}
