package com.pm.authservice.infrastructure.persistence.adapter;

import com.pm.authservice.domain.model.Role;
import com.pm.authservice.domain.port.out.RolePort;
import com.pm.authservice.user.repository.RoleRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class RolePersistenceAdapter implements RolePort {

    private final RoleRepository roleRepository;

    public RolePersistenceAdapter(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Cacheable("roles")
    @Override
    public List<Role> findAll() {
        return roleRepository.findAll().stream()
                .map(e -> new Role(e.getId(), e.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> findByIds(List<Integer> ids) {
        return roleRepository.findByIds(ids).stream()
                .map(e -> new Role(e.getId(), e.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Role> findByName(String name) {
        return Optional.ofNullable(roleRepository.findByName(name))
                .map(e -> new Role(e.getId(), e.getName()));
    }
}
