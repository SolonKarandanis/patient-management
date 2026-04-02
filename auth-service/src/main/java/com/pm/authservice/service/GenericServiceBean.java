package com.pm.authservice.service;

import com.pm.authservice.dto.UserDocumentDTO;
import com.pm.authservice.user.model.RoleEntity;
import com.pm.authservice.user.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public UserDocumentDTO convertToDocumentDto(UserEntity user) {
        UserDocumentDTO dto = new UserDocumentDTO();
        dto.setId(user.getId());
        dto.setPublicId(user.getPublicId().toString());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus() != null ? user.getStatus().getValue() : null);
        dto.setIsVerified(user.getIsVerified());
        dto.setIsEnabled(user.getIsEnabled());

        if (user.getRoles() != null) {
            List<String> roleNames = user.getRoles().stream()
                    .map(RoleEntity::getName)
                    .collect(Collectors.toList());
            dto.setRolesNames(roleNames);

            List<Integer> roleIds = user.getRoles().stream()
                    .map(RoleEntity::getId)
                    .collect(Collectors.toList());
            dto.setRoleIds(roleIds);
        }
        return dto;
    }

    @Override
    public List<UserDocumentDTO> convertToDocumentDtoList(List<UserEntity> userList) {
        if(CollectionUtils.isEmpty(userList)){
            return Collections.emptyList();
        }
        return userList.stream()
                .map(this::convertToDocumentDto)
                .toList();
    }


}
