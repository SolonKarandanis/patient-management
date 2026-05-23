package com.pm.authservice.user.service;

import com.pm.authservice.user.dto.RoleDTO;

import java.util.List;

public interface RoleQueryService {
    List<RoleDTO> findAllRoles();
}
