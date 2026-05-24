package com.pm.authservice.infrastructure.event;

import com.pm.authservice.infrastructure.messaging.listener.BaseEventListener;
import com.pm.authservice.infrastructure.messaging.listener.EventConstants;
import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TranslationsUpdatedEventListener extends BaseEventListener {

    private static final Logger log = LoggerFactory.getLogger(TranslationsUpdatedEventListener.class);

    @EventListener
    public void onTranslationsUpdated(TranslationsUpdatedEvent event) {
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .setEventType(EventConstants.I18N_RESOURCES_EDITED)
                .build();
        notificationsProducer.sendEvent(notificationEvent);
        log.info("TranslationsUpdatedEventListener->onTranslationsUpdated");
    }
}
