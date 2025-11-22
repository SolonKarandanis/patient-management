package com.pm.authservice.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class ChangePasswordDTO {

    @NotBlank(message = "{error.generic.prefix} {prompt.user.password} {error.generic.required}")
    private String password;

    @NotBlank(message = "{error.generic.prefix} {uprompt.user.confirm.password} {error.generic.required}")
    private String confirmPassword;
}
