package com.pm.authservice.repository;


import com.pm.authservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoleRepository extends JpaRepository<Role,Integer>{

    @Query(name = Role.FIND_BY_IDS)
    List<Role> findByIds(List<Integer> ids);

    @Query(name = Role.FIND_BY_NAME)
    Role findByName(String name);
}
