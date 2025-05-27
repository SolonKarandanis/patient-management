package com.pm.authservice.service;

import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.model.Role;

import java.util.List;

public interface RoleService {
    public RoleDTO convertToDto(Role role);

    public Role convertToEntity(RoleDTO roleDTO);

    public List<RoleDTO> convertToDtoList(List<Role> roles);

    public List<Role> findAll();

    public List<Role> findByIds(List<Integer> ids);

    public Role findByName(String name);
}
