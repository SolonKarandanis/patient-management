package com.pm.authservice.infrastructure.application;

import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.web.dto.UserDTO;

import java.util.List;

public interface UserQueryService {
    UserDTO viewUser(String publicId);
    List<String> getUserPermissions(String publicId);
}
