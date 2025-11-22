package com.pm.authservice.user.service;

import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.user.model.RoleEntity;

import java.util.List;
import java.util.Set;

public interface RoleService {
    RoleDTO convertToDto(RoleEntity role);

    RoleEntity convertToEntity(RoleDTO roleDTO);

    List<RoleDTO> convertToDtoList(Set<RoleEntity> roles);

    List<RoleEntity> findAll();

    List<RoleEntity> findByIds(List<Integer> ids);

    RoleEntity findByName(String name);
}
