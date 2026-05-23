package com.pm.authservice.user.service;

import com.pm.authservice.service.GenericService;
import com.pm.authservice.user.dto.RoleDTO;
import com.pm.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import com.pm.authservice.user.repository.RoleRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class RoleServiceBean  implements RoleService {

    private final RoleRepository roleRepository;
    private final GenericService genericService;

    public RoleServiceBean(RoleRepository roleRepository, GenericService genericService) {
        this.roleRepository = roleRepository;
        this.genericService = genericService;
    }

    @Override
    public RoleDTO convertToDto(RoleJpaEntity role) {
        RoleDTO dto = new RoleDTO();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setNameLabel(genericService.translate(role.getName()));
        return dto;
    }

    @Override
    public RoleJpaEntity convertToEntity(RoleDTO roleDTO) {
        RoleJpaEntity role = new RoleJpaEntity();
        role.setId(roleDTO.getId());
        role.setName(roleDTO.getName());
        return role;
    }

    @Override
    public List<RoleDTO> convertToDtoList(Set<RoleJpaEntity> roles) {
        List<RoleDTO> roleDTOS = new ArrayList<>();
        if(!CollectionUtils.isEmpty(roles)){
            for (RoleJpaEntity role : roles) {
                roleDTOS.add(convertToDto(role));
            }
        }
        return roleDTOS;
    }

    @Cacheable("roles")
    @Override
    public List<RoleJpaEntity> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public List<RoleJpaEntity> findByIds(List<Integer> ids) {
        return roleRepository.findByIds(ids);
    }

    @Override
    public RoleJpaEntity findByName(String name) {
        return roleRepository.findByName(name);
    }
}
