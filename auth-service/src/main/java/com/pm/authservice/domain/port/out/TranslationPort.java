package com.pm.authservice.domain.port.out;

import java.util.Locale;

public interface TranslationPort {
    String getLabel(String key, Locale locale);
}
