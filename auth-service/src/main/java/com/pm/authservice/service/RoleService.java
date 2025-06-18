package com.pm.authservice.service;

import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.model.RoleEntity;

import java.util.List;
import java.util.Set;

public interface RoleService {
    public RoleDTO convertToDto(RoleEntity role);

    public RoleEntity convertToEntity(RoleDTO roleDTO);

    public List<RoleDTO> convertToDtoList(Set<RoleEntity> roles);

    public List<RoleEntity> findAll();

    public List<RoleEntity> findByIds(List<Integer> ids);

    public RoleEntity findByName(String name);
}
