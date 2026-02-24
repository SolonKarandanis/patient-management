package com.pm.authservice.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public interface GenericService {

    ApplicationEventPublisher getPublisher();
    String translate(String key);
    String translate(String key, Locale locale);
    Locale getDefaultLocale();
    PageRequest transformPageSorting(PageRequest pageRequest, Map<String, String> sortingFieldsMap,
                                            Set<String> allowedSortingFields);
    String getDefaultSortingProperty();
}
