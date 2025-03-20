package com.pm.authservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersSearchRequestDTO extends SearchRequestDTO{
	
	private String username;
	private String firstName;
	private String email;
	private String status;
	private String roleName;
}
