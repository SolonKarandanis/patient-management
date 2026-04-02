package com.pm.authservice.service;

import com.pm.authservice.dto.UserDocumentDTO;
import com.pm.authservice.user.model.UserEntity;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;

import java.util.List;
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

    UserDocumentDTO convertToDocumentDto(UserEntity user);
    List<UserDocumentDTO>convertToDocumentDtoList(List<UserEntity> userList);
}
