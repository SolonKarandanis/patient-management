package com.pm.authservice.config.i18n;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

@Component
@Slf4j
public class I18nDbReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

    /** Set this as cacheMilis in order to prevent internal caching. */
    protected static final long NO_CACHE_MILIS = 1L;

    /** Prevent setting cacheMillis explicitly. */
    private boolean isCacheMillisInit = false;

    @Autowired
    private I18nDbResourceBundleMessageSource i18nDbResourceBundleMessageSource;

    public I18nDbReloadableResourceBundleMessageSource() {
        setCacheMillis(NO_CACHE_MILIS);
        isCacheMillisInit = true;
    }

    @Override
    @Nullable
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        return i18nDbResourceBundleMessageSource.doResolveCodeWithoutArguments(code, locale);
    }

    @Override
    @Nullable
    protected MessageFormat resolveCode(String code, Locale locale) {
        return i18nDbResourceBundleMessageSource.doResolveCode(code, locale);
    }

    @Override
    public void setBasenames(String... basenames) {
        super.setBasenames(basenames);
        i18nDbResourceBundleMessageSource.setBasenames(basenames);
    }

    @Override
    public void setDefaultEncoding(@Nullable String defaultEncoding) {
        super.setDefaultEncoding(defaultEncoding);
        i18nDbResourceBundleMessageSource.setDefaultEncoding(defaultEncoding);
    }

    @Override
    public void setDefaultLocale(@Nullable Locale defaultLocale) {
        super.setDefaultLocale(defaultLocale);
        i18nDbResourceBundleMessageSource.setDefaultLocale(defaultLocale);
    }

    @Override
    protected List<String> calculateAllFilenames(String basename, Locale locale) {
        throw new IllegalStateException("ERROR: Call to calculateAllFilenames() not allowed.");
    }

    @Override
    protected List<String> calculateFilenamesForLocale(String basename, Locale locale) {
        throw new IllegalStateException("ERROR: Call to calculateFilenamesForLocale() not allowed.");
    }

    @Override
    @Nullable
    protected Resource resolveResource(String filename) {
        throw new IllegalStateException("ERROR: Call to resolveResource() not allowed.");
    }

    @Override
    protected Properties loadProperties(Resource resource, String filename) throws IOException {
        throw new IllegalStateException("ERROR: Call to loadProperties() not allowed.");
    }

    @Override
    public void clearCache() {
        throw new IllegalStateException("ERROR: Call to clearCache() not allowed.");
    }

    @Override
    public void clearCacheIncludingAncestors() {
        throw new IllegalStateException("ERROR: Call to clearCacheIncludingAncestors() not allowed.");
    }

    @Override
    public void setCacheMillis(long cacheMillis) {
        if (isCacheMillisInit) {
            throw new IllegalStateException("ERROR: Call to setCacheMillis() not allowed after initialization.");
        }
        super.setCacheMillis(cacheMillis);
    }
}
