package com.pm.authservice.user.service;

import com.pm.authservice.user.dto.RoleDTO;
import com.pm.authservice.infrastructure.persistence.entity.RoleJpaEntity;

import java.util.List;
import java.util.Set;

public interface RoleService {
    RoleDTO convertToDto(RoleJpaEntity role);

    RoleJpaEntity convertToEntity(RoleDTO roleDTO);

    List<RoleDTO> convertToDtoList(Set<RoleJpaEntity> roles);

    List<RoleJpaEntity> findAll();

    List<RoleJpaEntity> findByIds(List<Integer> ids);

    RoleJpaEntity findByName(String name);
}
