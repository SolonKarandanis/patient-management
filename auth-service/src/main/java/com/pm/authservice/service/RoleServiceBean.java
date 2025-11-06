package com.pm.authservice.service;

import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.model.RoleEntity;
import com.pm.authservice.repository.RoleRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class RoleServiceBean extends GenericServiceBean implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceBean(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDTO convertToDto(RoleEntity role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setNameLabel(translate(role.getName()));
        return dto;
    }

    @Override
    public RoleEntity convertToEntity(RoleDTO roleDTO) {
        RoleEntity role = new RoleEntity();
        role.setId(roleDTO.getId());
        role.setName(roleDTO.getName());
        return role;
    }

    @Override
    public List<RoleDTO> convertToDtoList(Set<RoleEntity> roles) {
        List<RoleDTO> roleDTOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(roles)){
            for (RoleEntity role : roles) {
                roleDTOS.add(convertToDto(role));
            }
        }
        return roleDTOS;
    }

    @Cacheable("roles")
    @Override
    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public List<RoleEntity> findByIds(List<Integer> ids) {
        return roleRepository.findByIds(ids);
    }

    @Override
    public RoleEntity findByName(String name) {
        return roleRepository.findByName(name);
    }
}
