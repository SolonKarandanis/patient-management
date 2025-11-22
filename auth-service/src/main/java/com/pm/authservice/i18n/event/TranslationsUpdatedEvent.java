package com.pm.authservice.i18n.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class TranslationsUpdatedEvent extends ApplicationEvent {

    public TranslationsUpdatedEvent() {
        super(new Object());
    }
}