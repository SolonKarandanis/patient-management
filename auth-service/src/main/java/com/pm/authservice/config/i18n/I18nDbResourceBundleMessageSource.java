package com.pm.authservice.config.i18n;

import com.pm.authservice.service.I18nService;
import com.pm.authservice.util.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;

@Component
@Slf4j
public class I18nDbResourceBundleMessageSource extends ResourceBundleMessageSource {

    /** Set this as cacheMilis in order to prevent internal caching. */
    protected static final long NO_CACHE_MILIS = 1L;

    private final I18nService i18nService;

    /** Prevent setting cacheMillis explicitly. */
    private boolean isCacheMillisInit = false;

    public I18nDbResourceBundleMessageSource(I18nService i18nService) {
        this.i18nService = i18nService;
        setCacheMillis(NO_CACHE_MILIS);
        isCacheMillisInit = true;
    }

    /**
     * Overrides <code>ResourceBundleMessageSource.doGetBundle()</code> so as to extract the properties list from DB.
     */
    @Override
    protected ResourceBundle doGetBundle(final String basename, final Locale locale) throws MissingResourceException {
        ResourceBundle outputAsResBundle = null;

        Map<String, String> propsMap = i18nService.getTranslationsByModuleAndLangIsoCode(basename, locale.getLanguage());
        byte[] contentPropsBytes = CollectionUtil.convertMapToPropertiesBytes(propsMap);

        try {
            outputAsResBundle = new PropertyResourceBundle(new InputStreamReader(new ByteArrayInputStream(contentPropsBytes), String.valueOf(getDefaultCharset())));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return outputAsResBundle;
    }

    private Charset getDefaultCharset() {
        return Optional.ofNullable(this.getDefaultEncoding()).map(Charset::forName).orElse(StandardCharsets.UTF_8);
    }

    @Override
    public void setCacheMillis(long cacheMillis) {
        if (isCacheMillisInit) {
            throw new IllegalStateException("ERROR: Call to setCacheMillis() not allowed after initialization.");
        }
        super.setCacheMillis(cacheMillis);
    }

    public String doResolveCodeWithoutArguments(String code, Locale locale) {
        return super.resolveCodeWithoutArguments(code, locale);
    }

    public MessageFormat doResolveCode(String code, Locale locale) {
        return super.resolveCode(code, locale);
    }
}
