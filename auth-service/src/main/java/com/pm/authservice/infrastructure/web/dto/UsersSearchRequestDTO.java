package com.pm.authservice.infrastructure.web.dto;

import com.pm.authservice.infrastructure.web.dto.SearchRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UsersSearchRequestDTO extends SearchRequestDTO {
    private String username;
    private String name;
    private String email;
    private String status;
    private String roleName;
}