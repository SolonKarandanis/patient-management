/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pm.authservice.dto;

import com.pm.authservice.validation.Authority;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author solon
 */
@SuppressWarnings("MissingSummary")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CreateUserDTO {
    
    @NotNull(message = "{user.username.notNull}")
    @Size(min = 1, max = 25, message = "{user.username.size}")
    private String username;
    
    private String password;
    
    @NotNull(message = "{user.firstName.notNull}")
    @Size(min = 1, max = 50, message = "{user.firstName.size}")
    private String firstName;
    
    @NotNull(message = "{user.lastName.notNull}")
    @Size(min = 1, max = 50, message = "{user.lastName.size}")
    private String lastName;
    
    @NotNull(message = "{user.email.notNull}")
    @Size(min = 1, max = 150, message = "{user.email.size}")
    private String email;

	@Authority
	@NotNull(message = "{user.role.notNull}")
	@Size(min = 1, max = 150, message = "{user.role.size}")
	private String role;


	public @NotNull(message = "{user.username.notNull}") @Size(min = 1, max = 25, message = "{user.username.size}") String getUsername() {
		return username;
	}

	public void setUsername(@NotNull(message = "{user.username.notNull}") @Size(min = 1, max = 25, message = "{user.username.size}") String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public @NotNull(message = "{user.firstName.notNull}") @Size(min = 1, max = 50, message = "{user.firstName.size}") String getFirstName() {
		return firstName;
	}

	public void setFirstName(@NotNull(message = "{user.firstName.notNull}") @Size(min = 1, max = 50, message = "{user.firstName.size}") String firstName) {
		this.firstName = firstName;
	}

	public @NotNull(message = "{user.lastName.notNull}") @Size(min = 1, max = 50, message = "{user.lastName.size}") String getLastName() {
		return lastName;
	}

	public void setLastName(@NotNull(message = "{user.lastName.notNull}") @Size(min = 1, max = 50, message = "{user.lastName.size}") String lastName) {
		this.lastName = lastName;
	}

	public @NotNull(message = "{user.email.notNull}") @Size(min = 1, max = 150, message = "{user.email.size}") String getEmail() {
		return email;
	}

	public void setEmail(@NotNull(message = "{user.email.notNull}") @Size(min = 1, max = 150, message = "{user.email.size}") String email) {
		this.email = email;
	}

	public @NotNull(message = "{user.role.notNull}") @Size(min = 1, max = 150, message = "{user.role.size}") String getRole() {
		return role;
	}

	public void setRole(@NotNull(message = "{user.role.notNull}") @Size(min = 1, max = 150, message = "{user.role.size}") String role) {
		this.role = role;
	}
}
