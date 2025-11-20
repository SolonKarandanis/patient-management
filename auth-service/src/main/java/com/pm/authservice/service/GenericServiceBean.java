package com.pm.authservice.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.*;

@Getter
public class GenericServiceBean {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ApplicationEventPublisher publisher;

    public void setPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    protected String translate(String key) {
        return Optional.of(messageSource.getMessage(key, null, getDefaultLocale())).orElse(key);
    }


    protected String translate(String key, Locale locale) {
        return Optional.of(messageSource.getMessage(key, null, locale)).orElse(key);
    }

    protected Locale getDefaultLocale() {
        return Locale.ENGLISH;
    }



    protected PageRequest transformPageSorting(PageRequest pageRequest, Map<String, String> sortingFieldsMap,
                                               Set<String> allowedSortingFields) {
        if (pageRequest == null) {
            return null;
        }
        Set<String> sortingFieldsMapKeys = sortingFieldsMap.keySet();
        List<Sort.Order> mappedOrders = pageRequest.getSort().get().map(order -> {
            String propName = order.getProperty();
            if (sortingFieldsMapKeys.contains(propName)) {
                return order.withProperty(sortingFieldsMap.get(propName));
            } else if (!allowedSortingFields.contains(propName)) {
                return order.withProperty(getDefaultSortingProperty());
            }
            return order;
        }).toList();

        return pageRequest.withSort(Sort.by(mappedOrders));
    }

    protected String getDefaultSortingProperty() {
        return "id";
    }


}
