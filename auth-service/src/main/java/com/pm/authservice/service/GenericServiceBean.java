package com.pm.authservice.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Optional;

@Getter
public class GenericServiceBean {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ApplicationEventPublisher publisher;

    public void setPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

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
