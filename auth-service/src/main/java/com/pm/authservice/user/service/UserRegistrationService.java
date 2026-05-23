package com.pm.authservice.user.service;

import com.pm.authservice.user.dto.CreateUserDTO;
import com.pm.authservice.user.dto.UserDTO;

public interface UserRegistrationService {
    UserDTO register(CreateUserDTO dto, String applicationUrl);
}
