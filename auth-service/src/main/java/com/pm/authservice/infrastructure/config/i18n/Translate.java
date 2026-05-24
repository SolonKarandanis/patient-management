package com.pm.authservice.infrastructure.config.i18n;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Repeatable(Translate.Translations.class)
public @interface Translate {
    String path();

    String targetProperty() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Documented
    @interface Translations {
        Translate[] value();
    }
}
