package com.pm.authservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class I18nResourceManagementResponseDTO {
    private Integer id;
    private String key;
    private String mod;
    Map<Integer, String> translations;
}
