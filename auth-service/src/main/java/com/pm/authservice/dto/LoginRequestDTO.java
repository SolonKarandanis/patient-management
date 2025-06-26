package com.pm.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @NotBlank(message = "{error.generic.prefix} {prompt.user.email} {error.generic.required}")
    @Email(message = "{error.generic.prefix} {prompt.user.email} {error.generic.valid}")
    private String email;

    @NotBlank(message = "{error.generic.prefix} {prompt.user.password} {error.generic.required}")
    @Size(min = 8, message = "{error.generic.prefix} {prompt.user.password} {error.generic.oversize.MAX}")
    private String password;

}
