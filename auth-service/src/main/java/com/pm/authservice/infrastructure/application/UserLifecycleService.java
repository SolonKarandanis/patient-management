package com.pm.authservice.infrastructure.application;

import com.pm.authservice.infrastructure.web.dto.ChangePasswordDTO;
import com.pm.authservice.infrastructure.web.dto.UpdateUserDTO;
import com.pm.authservice.infrastructure.web.dto.UserDTO;

public interface UserLifecycleService {
    UserDTO updateUser(String publicId, UpdateUserDTO dto);
    void deleteUser(String publicId);
    UserDTO changePassword(String publicId, ChangePasswordDTO dto);
    UserDTO activateUser(String publicId);
    UserDTO deactivateUser(String publicId);
}
