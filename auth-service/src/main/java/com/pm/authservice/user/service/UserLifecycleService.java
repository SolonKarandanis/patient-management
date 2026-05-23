package com.pm.authservice.user.service;

import com.pm.authservice.user.dto.ChangePasswordDTO;
import com.pm.authservice.user.dto.UpdateUserDTO;
import com.pm.authservice.user.dto.UserDTO;

public interface UserLifecycleService {
    UserDTO updateUser(String publicId, UpdateUserDTO dto);
    void deleteUser(String publicId);
    UserDTO changePassword(String publicId, ChangePasswordDTO dto);
    UserDTO activateUser(String publicId);
    UserDTO deactivateUser(String publicId);
}
