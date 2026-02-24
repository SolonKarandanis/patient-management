package com.pm.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GenericServiceBean  implements GenericService{

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ApplicationEventPublisher publisher;

    public void setPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public ApplicationEventPublisher getPublisher(){
       return this.publisher;
    }

    public String translate(String key) {
        return Optional.of(messageSource.getMessage(key, null, getDefaultLocale())).orElse(key);
    }


    public String translate(String key, Locale locale) {
        return Optional.of(messageSource.getMessage(key, null, locale)).orElse(key);
    }

    public Locale getDefaultLocale() {
        return Locale.ENGLISH;
    }



    public PageRequest transformPageSorting(PageRequest pageRequest, Map<String, String> sortingFieldsMap,
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

    public String getDefaultSortingProperty() {
        return "id";
    }


}
