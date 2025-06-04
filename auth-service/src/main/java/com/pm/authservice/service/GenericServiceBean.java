package com.pm.authservice.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
@Getter
public class GenericServiceBean {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ApplicationEventPublisher publisher;

    protected String translate(String key) {
        return Optional.of(messageSource.getMessage(key, null, getDefaultLocale())).orElse(key);
    }


    protected String translate(String key, Locale locale) {
        return Optional.of(messageSource.getMessage(key, null, locale)).orElse(key);
    }

    protected Locale getDefaultLocale() {
        return Locale.ENGLISH;
    }
}
