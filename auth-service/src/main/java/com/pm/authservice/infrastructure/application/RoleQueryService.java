package com.pm.authservice.infrastructure.application;

import com.pm.authservice.infrastructure.web.dto.RoleDTO;

import java.util.List;

public interface RoleQueryService {
    List<RoleDTO> findAllRoles();
}
