package com.pm.authservice.infrastructure.i18n;

import com.pm.authservice.domain.port.out.TranslationPort;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageSourceTranslationAdapter implements TranslationPort {

    private final MessageSource messageSource;

    public MessageSourceTranslationAdapter(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String getLabel(String key, Locale locale) {
        return messageSource.getMessage(key, null, key, locale);
    }
}
