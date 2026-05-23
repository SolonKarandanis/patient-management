package com.pm.authservice.infrastructure.persistence.mapper;

import com.pm.authservice.domain.model.Role;
import com.pm.authservice.domain.model.User;
import com.pm.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        Set<Role> roles = entity.getRoles().stream()
                .map(this::roleToDomain)
                .collect(Collectors.toSet());
        return User.builder()
                .domainId(entity.getDomainId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .status(entity.getStatus())
                .isEnabled(entity.getIsEnabled())
                .isVerified(entity.getIsVerified())
                .createdDate(entity.getCreatedDate())
                .lastModifiedDate(entity.getLastModifiedDate())
                .roles(roles)
                .build();
    }

    public void updateEntity(UserJpaEntity entity, User domain) {
        entity.setUsername(domain.getUsername());
        entity.setEmail(domain.getEmail());
        entity.setPassword(domain.getPassword());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setStatus(domain.getStatus());
        entity.setIsEnabled(domain.getIsEnabled());
        entity.setIsVerified(domain.getIsVerified());
        entity.setCreatedDate(domain.getCreatedDate());
        entity.setLastModifiedDate(domain.getLastModifiedDate());
    }

    public Role roleToDomain(RoleJpaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Role(entity.getId(), entity.getName());
    }
}
