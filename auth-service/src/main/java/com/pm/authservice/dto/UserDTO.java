package com.pm.authservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private String publicId;
    private String username;
    private String password;
    private String lastName;
    private String firstName;
    private String email;
}
