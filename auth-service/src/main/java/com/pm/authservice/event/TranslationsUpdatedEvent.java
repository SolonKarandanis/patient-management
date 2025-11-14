package com.pm.authservice.event;

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