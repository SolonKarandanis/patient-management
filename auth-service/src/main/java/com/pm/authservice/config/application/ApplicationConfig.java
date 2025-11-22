package com.pm.authservice.config.application;

import com.pm.authservice.config.i18n.ChangeableLocaleResolver;
import com.pm.authservice.config.i18n.I18nDbReloadableResourceBundleMessageSource;
import com.pm.authservice.config.i18n.I18nDbResourceBundleMessageSource;
import com.pm.authservice.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Configuration
public class ApplicationConfig {
    private static final Logger log = LoggerFactory.getLogger(ApplicationConfig.class);

    @Value("${i18n.resources.DB.enabled:false}")
    private Boolean i18nDbEnabled;

    @Autowired
    private I18nDbResourceBundleMessageSource i18nDbResourceBundleMessageSource;

    @Autowired
    private I18nDbReloadableResourceBundleMessageSource i18nDbReloadableResourceBundleMessageSource;

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MessageSource messageSource() {
        log.debug("messageSource");
        ResourceBundleMessageSource source = null;
        if (Boolean.TRUE.equals(i18nDbEnabled)) {
            source = i18nDbResourceBundleMessageSource;
        } else {
            source = new ResourceBundleMessageSource();
        }
        source.setBasenames("application_messages", "application_errors");
        source.setUseCodeAsDefaultMessage(true);
        source.setDefaultEncoding(AppConstants.UTF_8);
        source.setDefaultLocale(Locale.ENGLISH);
        return source;
    }

    /**
     * Required for locale-based translation of validation messages
     * originating from javax.validation API.
     *
     * @return
     */
    @Bean
    LocalValidatorFactoryBean localValidatorFactoryBean() {
        /* NOTE: MessageSource Implementation must not change
         * (Some implementations other than ReloadableResourceBundleMessageSource
         * cause message interpolation to fail.
         */
        ReloadableResourceBundleMessageSource msgSource = null;
        if (Boolean.TRUE.equals(i18nDbEnabled)) {
            msgSource = i18nDbReloadableResourceBundleMessageSource;
            msgSource.setBasenames("application_messages", "application_errors", "messages");
        } else {
            msgSource = new ReloadableResourceBundleMessageSource();
            msgSource.setBasenames("classpath:application_messages", "classpath:application_errors", "classpath:messages");
        }
        msgSource.setDefaultEncoding(AppConstants.UTF_8);
        msgSource.setDefaultLocale(Locale.ENGLISH);
        msgSource.setUseCodeAsDefaultMessage(true);

        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(msgSource);
        return bean;
    }

    /**
     * Required for locale-based translation of validation messages
     * originating from javax.validation API.
     *
     * @return
     */
    @Bean
    public LocaleResolver localeResolver() {
        LocaleResolver r1 = null;
        /* Return a valid locale resolver that allows re-setting the locale. */
        r1 = new ChangeableLocaleResolver();
        return r1;
    }
}
