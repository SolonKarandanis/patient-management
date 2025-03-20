package com.pm.authservice.config.i18n;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Repeatable(Translate.Translations.class)
public @interface Translate {
    String path();

    String targetProperty() default "";

    //Do it as inner class since we will never use this in practice
    //Repeatable Annotations require to have a container class
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Documented
    public @interface Translations {
        Translate[] value();
    }
}

