package com.pm.authservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDTO {

    private String publicId;
    private String username;
    private String password;
    private String lastName;
    private String firstName;
    private String email;
    private String status;
    private String statusLabel;
    private Boolean isEnabled;
    private List<RoleDTO> roles;
    private List<OperationDTO> operations;
}
