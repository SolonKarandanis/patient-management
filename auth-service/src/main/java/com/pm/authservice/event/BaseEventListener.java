package com.pm.authservice.event;


import com.pm.authservice.broker.KafkaAnalyticsProducer;
import com.pm.authservice.broker.KafkaNotificationsProducer;
import com.pm.authservice.model.UserEntity;
import com.pm.authservice.model.UserEventEntity;
import com.pm.authservice.model.UserStatus;
import com.pm.authservice.service.UserEventService;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseEventListener {

    @Autowired
    protected UserEventService userEventService;

    @Autowired
    protected KafkaAnalyticsProducer analyticsProducer;

    @Autowired
    protected KafkaNotificationsProducer notificationsProducer;

    protected UserEventEntity createUserEvent(UserEntity user){
        return new UserEventEntity(user.getId(),user.getPublicId(), UserStatus.USER_CREATED,user.getUsername(),user.getEmail());
    }

    protected void saveAndPublishEvents(UserEventEntity eventEntity){
        eventEntity=userEventService.saveEvent(eventEntity);
        analyticsProducer.sendEvent(eventEntity);
    }
}
