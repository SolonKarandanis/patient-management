package com.pm.authservice.event;

import com.pm.authservice.broker.KafkaAnalyticsProducer;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.model.UserEventEntity;
import com.pm.authservice.model.UserStatus;
import com.pm.authservice.service.UserEventService;
import com.pm.authservice.service.VerificationTokenService;
import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserRegistrationEventListener extends BaseEventListener implements ApplicationListener<UserRegistrationEvent> {
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationEventListener.class);

    private final VerificationTokenService tokenService;

    public UserRegistrationEventListener(VerificationTokenService tokenService){
        this.tokenService = tokenService;
    }

    @Override
    public void onApplicationEvent(UserRegistrationEvent event) {
        // 1. Get the newly registered user
        UserEntity user = event.getUser();
        //2. Create a verification token for the user
        String verificationToken = UUID.randomUUID().toString();
        //3. Save the verification token for the user
        tokenService.saveUserVerificationToken(user, verificationToken);
        //4 Build the verification url to be sent to the user
        String url = event.getApplicationUrl()+"/register/verifyEmail?token="+verificationToken;
        //5 Save UserEventEntity and send Kafka event for analytics
        UserEventEntity eventEntity= createUserEvent(user);
        saveAndPublishEvents(eventEntity);
        //6 Send Kafka event for notification
        StringBuilder sb =new StringBuilder();
        sb.append("User with username '").append(user.getUsername()).append("' has been registered successfully");
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .addUserIds(user.getPublicId().toString())
                .setTitle("User Registration Completed")
                .setMessage(sb.toString())
                .setEventType(EventConstants.USER_CREATED_NOTIFICATION)
                .build();
        notificationsProducer.sendEvent(notificationEvent);
        //7 Send Kafka event for email
        log.info("UserRegistrationCompleteEventListener -> onApplicationEvent ->  url:  {}", url);

    }
}
