package com.pm.authservice.user.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pm.authservice.config.authorisation.RedactedEmailUserDTOSerializer;
import com.pm.authservice.dto.OperationDTO;
import com.pm.authservice.dto.RoleDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonSerialize(using = RedactedEmailUserDTOSerializer.class)
public class UserDTO {

    private String publicId;
    private String username;
    private String lastName;
    private String firstName;
    private String email;
    private String status;
    private String statusLabel;
    private Boolean isEnabled;
    private List<RoleDTO> roles;
    private List<OperationDTO> operations;
}
