package com.pm.authservice.infrastructure.i18n.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.i18n.AbstractLocaleResolver;

import java.util.Locale;

public class ChangeableLocaleResolver extends AbstractLocaleResolver {

    protected static final Logger LOG = LoggerFactory.getLogger(ChangeableLocaleResolver.class);

    private Locale changeableLocale = null;

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        throwIllegalStateExceptionIfRequestIsNull(request);
        return changeableLocale != null ? changeableLocale : Locale.ENGLISH;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        throwIllegalStateExceptionIfRequestIsNull(request);
        throwIllegalStateExceptionIfResponseIsNull(response);
        changeableLocale = locale;
    }

    private void throwIllegalStateExceptionIfRequestIsNull(final HttpServletRequest request) {
        if (request == null) {
            throw new IllegalStateException("ERROR: Request is NULL.");
        }
    }

    private void throwIllegalStateExceptionIfResponseIsNull(final HttpServletResponse response) {
        if (response == null) {
            throw new IllegalStateException("ERROR: Response is NULL.");
        }
    }
}
