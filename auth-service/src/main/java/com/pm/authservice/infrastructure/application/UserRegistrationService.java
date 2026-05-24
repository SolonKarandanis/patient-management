package com.pm.authservice.infrastructure.application;

import com.pm.authservice.infrastructure.web.dto.CreateUserDTO;
import com.pm.authservice.infrastructure.web.dto.UserDTO;

public interface UserRegistrationService {
    UserDTO register(CreateUserDTO dto, String applicationUrl);
}
