package com.pm.authservice.event;


import com.pm.authservice.broker.KafkaAnalyticsProducer;
import com.pm.authservice.broker.KafkaNotificationsProducer;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.user.model.UserEventEntity;
import com.pm.authservice.user.model.UserStatus;
import com.pm.authservice.user.service.UserEventService;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseEventListener {

    @Autowired
    protected UserEventService userEventService;

    @Autowired
    protected KafkaAnalyticsProducer analyticsProducer;

    @Autowired
    protected KafkaNotificationsProducer notificationsProducer;

    protected UserEventEntity createUserEvent(UserEntity user, UserStatus status){
        return new UserEventEntity(user.getId(),user.getPublicId(), status,user.getUsername(),user.getEmail());
    }

    protected void saveAndPublishEvents(UserEventEntity eventEntity){
        eventEntity=userEventService.saveEvent(eventEntity);
        analyticsProducer.sendEvent(eventEntity);
    }
}
