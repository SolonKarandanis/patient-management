package com.pm.authservice.service;

import com.pm.authservice.dto.RoleDTO;
import com.pm.authservice.model.Role;
import com.pm.authservice.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RoleServiceBean implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceBean(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDTO convertToDto(Role role) {
        return new RoleDTO(role.getId(), role.getName());
    }

    @Override
    public Role convertToEntity(RoleDTO roleDTO) {
        Role role = new Role();
        role.setId(roleDTO.getId());
        role.setName(roleDTO.getName());
        return role;
    }

    @Override
    public List<RoleDTO> convertToDtoList(List<Role> roles) {
        List<RoleDTO> roleDTOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(roles)){
            for (Role role : roles) {
                roleDTOS.add(convertToDto(role));
            }
        }
        return roleDTOS;
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public List<Role> findByIds(List<Integer> ids) {
        return roleRepository.findByIds(ids);
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name);
    }
}
