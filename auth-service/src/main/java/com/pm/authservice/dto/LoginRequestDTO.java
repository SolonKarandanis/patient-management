package com.pm.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequestDTO {
    @NotBlank(message = "{error.generic.prefix} {prompt.user.email} {error.generic.required}")
    @Email(message = "{error.generic.prefix} {prompt.user.email} {error.generic.valid}")
    private String email;

    @NotBlank(message = "{error.generic.prefix} {prompt.user.password} {error.generic.required}")
    @Size(min = 8, message = "{error.generic.prefix} {prompt.user.password} {error.generic.oversize.MAX}")
    private String password;

    public @NotBlank(message = "{error.generic.prefix} {prompt.user.email} {error.generic.required}") @Email(message = "{error.generic.prefix} {prompt.user.email} {error.generic.valid}") String getEmail() {
        return email;
    }

    public void setEmail(
            @NotBlank(message = "{error.generic.prefix} {prompt.user.email} {error.generic.required}") @Email(message = "{error.generic.prefix} {prompt.user.email} {error.generic.valid}") String email) {
        this.email = email;
    }

    public @NotBlank(message = "{error.generic.prefix} {prompt.user.password} {error.generic.required}") @Size(min = 8, message = "{error.generic.prefix} {prompt.user.password} {error.generic.oversize.MAX}") String getPassword() {
        return password;
    }

    public void setPassword(
            @NotBlank(message = "{error.generic.prefix} {prompt.user.password} {error.generic.required}") @Size(min = 8, message = "{error.generic.prefix} {prompt.user.password} {error.generic.oversize.MAX}") String password) {
        this.password = password;
    }
}
