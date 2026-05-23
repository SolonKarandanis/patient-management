package com.pm.authservice.domain.port.out;

import com.pm.authservice.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserPort {
    User save(User user);
    Optional<User> findByDomainId(UUID domainId);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    void delete(UUID domainId);
    boolean hasRole(UUID domainId, String roleName);
    boolean hasPermission(UUID domainId, String operationName);
}
