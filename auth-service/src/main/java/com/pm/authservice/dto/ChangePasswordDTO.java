package com.pm.authservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class ChangePasswordDTO {

    @NotNull(message = "{user.password.notNull}")
    private String password;

    @NotNull(message = "{user.confirm.password.notNull}")
    private String confirmPassword;
}
