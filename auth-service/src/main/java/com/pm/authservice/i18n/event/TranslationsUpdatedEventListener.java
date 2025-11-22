package com.pm.authservice.i18n.event;

import com.pm.authservice.event.BaseEventListener;
import com.pm.authservice.event.EventConstants;
import notification.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class TranslationsUpdatedEventListener  extends BaseEventListener implements ApplicationListener<TranslationsUpdatedEvent> {
    private static final Logger log = LoggerFactory.getLogger(TranslationsUpdatedEventListener.class);

    @Override
    public void onApplicationEvent(TranslationsUpdatedEvent event) {
        NotificationEvent notificationEvent = NotificationEvent.newBuilder()
                .setEventType(EventConstants.I18N_RESOURCES_EDITED)
                .build();
        notificationsProducer.sendEvent(notificationEvent);
        log.info("TranslationsUpdatedEventListener -> onApplicationEvent -> ");
    }
}
