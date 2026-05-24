package com.pm.authservice.infrastructure.messaging.listener;

import com.pm.authservice.infrastructure.messaging.broker.KafkaAnalyticsProducer;
import com.pm.authservice.infrastructure.messaging.broker.KafkaNotificationsProducer;
import com.pm.authservice.infrastructure.persistence.entity.UserEventEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserStatus;
import com.pm.authservice.infrastructure.application.UserEventService;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseEventListener {

    @Autowired
    protected UserEventService userEventService;

    @Autowired
    protected KafkaAnalyticsProducer analyticsProducer;

    @Autowired
    protected KafkaNotificationsProducer notificationsProducer;

    protected UserEventEntity createUserEvent(UserJpaEntity user, UserStatus status) {
        return new UserEventEntity(user.getId(), user.getDomainId(), status, user.getUsername(), user.getEmail());
    }

    protected void saveAndPublishEvents(UserEventEntity eventEntity) {
        eventEntity = userEventService.saveEvent(eventEntity);
        analyticsProducer.sendEvent(eventEntity);
    }
}
