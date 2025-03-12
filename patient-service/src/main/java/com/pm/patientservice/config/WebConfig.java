package com.pm.patientservice.config;

import com.pm.patientservice.config.i18n.ChangeableLocaleResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Configuration
public class WebConfig {
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    @Bean
    public MessageSource messageSource() {
        log.debug("messageSource");
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("application_messages", "application_errors");
        source.setUseCodeAsDefaultMessage(true);
        source.setDefaultEncoding("UTF-8");
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
        ReloadableResourceBundleMessageSource msgSource = new ReloadableResourceBundleMessageSource();
        msgSource.setBasenames("classpath:application_messages", "classpath:application_errors");
        msgSource.setDefaultEncoding("UTF-8");
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
