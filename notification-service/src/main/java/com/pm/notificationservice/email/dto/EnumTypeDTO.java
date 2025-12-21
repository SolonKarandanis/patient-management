package com.pm.notificationservice.email.dto;

import lombok.AllArgsConstructor;

import lombok.Getter;

import lombok.NoArgsConstructor;

import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EnumTypeDTO {

    private Integer id;
    private String resourceKey;
    private String resourceLabel;

    public EnumTypeDTO(Integer id, String resourceKey) {
        this.id = id;
        this.resourceKey = resourceKey;
    }
}
