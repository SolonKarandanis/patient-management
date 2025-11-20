/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pm.authservice.user.dto;

import com.pm.authservice.validation.Authority;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 *
 * @author solon
 */
@SuppressWarnings("MissingSummary")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class CreateUserDTO {
    
    @NotBlank(message = "{error.generic.prefix} {prompt.user.name} {error.generic.required}")
    @Size(min = 1, max = 25, message = "{error.generic.prefix} {user.username.size} {error.generic.oversize.MAX}")
    private String username;

	@NotBlank(message = "{error.generic.prefix} {prompt.user.password} {error.generic.required}")
    private String password;

	@NotBlank(message = "{error.generic.prefix} {uprompt.user.confirm.password} {error.generic.required}")
	private String confirmPassword;
    
    @NotBlank(message = "{error.generic.prefix} {prompt.user.firstName} {error.generic.required}")
    @Size(min = 1, max = 50, message = "{error.generic.prefix} {prompt.user.firstName} {error.generic.oversize.MAX}")
    private String firstName;

	@NotBlank(message = "{error.generic.prefix} {prompt.user.lastName} {error.generic.required}")
    @Size(min = 1, max = 50, message = "{error.generic.prefix} {prompt.user.lastName} {error.generic.oversize.MAX}")
    private String lastName;
    
    @Size(min = 1, max = 150, message = "{error.generic.prefix} {prompt.user.email} {error.generic.oversize.MAX}")
	@NotBlank(message = "{error.generic.prefix} {prompt.user.email} {error.generic.required}")
	@Email(message = "{error.generic.prefix} {prompt.user.email} {error.generic.valid}")
    private String email;

	@Authority
	@NotBlank(message = "{error.generic.prefix} {prompt.user.email} {error.generic.required}")
	@Size(min = 1, max = 150, message = "{error.generic.prefix} {prompt.user.email} {error.generic.oversize.MAX}")
	private String role;


}
