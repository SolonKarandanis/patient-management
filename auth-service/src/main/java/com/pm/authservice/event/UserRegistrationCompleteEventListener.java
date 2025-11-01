package com.pm.authservice.event;

import com.pm.authservice.broker.KafkaAnalyticsProducer;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.model.UserEventEntity;
import com.pm.authservice.model.UserStatus;
import com.pm.authservice.service.UserEventService;
import com.pm.authservice.service.VerificationTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UserRegistrationCompleteEventListener implements ApplicationListener<UserRegistrationCompleteEvent> {
    private static final Logger log = LoggerFactory.getLogger(UserRegistrationCompleteEventListener.class);

    private final VerificationTokenService tokenService;
    private final UserEventService userEventService;
    private final KafkaAnalyticsProducer analyticsProducer;
    private UserEntity user;

    public UserRegistrationCompleteEventListener(
            VerificationTokenService tokenService,
            UserEventService userEventService,
            KafkaAnalyticsProducer analyticsProducer){
        this.tokenService = tokenService;
        this.userEventService = userEventService;
        this.analyticsProducer = analyticsProducer;
    }

    @Override
    public void onApplicationEvent(UserRegistrationCompleteEvent event) {
        // 1. Get the newly registered user
        user = event.getUser();
        //2. Create a verification token for the user
        String verificationToken = UUID.randomUUID().toString();
        //3. Save the verification token for the user
        tokenService.saveUserVerificationToken(user, verificationToken);
        //4 Build the verification url to be sent to the user
        String url = event.getApplicationUrl()+"/register/verifyEmail?token="+verificationToken;
        //5 Save UserEventEntity and send Kafka event for analytics
        UserEventEntity eventEntity= createUserEvent(user);
        saveAndPublishEvents(eventEntity);
        //6 Send Kafka event for email
        log.info("UserRegistrationCompleteEventListener -> onApplicationEvent ->  url:  {}", url);

    }

    private UserEventEntity createUserEvent(UserEntity user){
        return new UserEventEntity(user.getId(),user.getPublicId(), UserStatus.USER_CREATED,user.getUsername(),user.getEmail());
    }

    private void saveAndPublishEvents(UserEventEntity eventEntity){
        eventEntity=userEventService.saveEvent(eventEntity);
        analyticsProducer.sendEvent(eventEntity);
    }
}
