package com.pm.authservice.domain.port.out;

import com.pm.authservice.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface RolePort {
    List<Role> findAll();
    List<Role> findByIds(List<Integer> ids);
    Optional<Role> findByName(String name);
}
