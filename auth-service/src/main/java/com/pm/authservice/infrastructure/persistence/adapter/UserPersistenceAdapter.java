package com.pm.authservice.infrastructure.persistence.adapter;

import com.pm.authservice.domain.model.Role;
import com.pm.authservice.domain.model.User;
import com.pm.authservice.domain.port.out.UserPort;
import com.pm.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.persistence.mapper.UserMapper;
import com.pm.authservice.user.repository.RoleRepository;
import com.pm.authservice.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class UserPersistenceAdapter implements UserPort {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public UserPersistenceAdapter(UserRepository userRepository,
                                  RoleRepository roleRepository,
                                  UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity entity = userRepository.findByDomainId(user.getDomainId())
                .orElseGet(UserJpaEntity::new);
        if (entity.getDomainId() == null) {
            entity.setDomainId(user.getDomainId());
        }
        userMapper.updateEntity(entity, user);
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<Integer> roleIds = user.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toList());
            Set<RoleJpaEntity> roleEntities = new HashSet<>(roleRepository.findAllById(roleIds));
            entity.setRoles(roleEntities);
        }
        UserJpaEntity saved = userRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByDomainId(UUID domainId) {
        return userRepository.findByDomainId(domainId)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean hasRole(UUID domainId, String roleName) {
        return userRepository.findByDomainId(domainId)
                .map(u -> u.getRoles().stream().anyMatch(r -> roleName.equals(r.getName())))
                .orElse(false);
    }

    @Override
    public boolean hasPermission(UUID domainId, String operationName) {
        return userRepository.findByDomainId(domainId)
                .map(u -> u.getRoles().stream()
                        .flatMap(r -> r.getRoleOperations().stream())
                        .anyMatch(ro -> operationName.equals(ro.getOperation().getName())))
                .orElse(false);
    }
}
