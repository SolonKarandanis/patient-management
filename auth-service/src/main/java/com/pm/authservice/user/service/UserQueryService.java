package com.pm.authservice.user.service;

import com.pm.authservice.user.dto.UserDTO;

import java.util.List;

public interface UserQueryService {
    UserDTO viewUser(String publicId);
    List<String> getUserPermissions(String publicId);
}
