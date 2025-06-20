package com.pm.authservice.repository;


import com.pm.authservice.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,Integer>{

    @Query(name = RoleEntity.FIND_BY_IDS)
    List<RoleEntity> findByIds(List<Integer> ids);

    @Query(name = RoleEntity.FIND_BY_NAME)
    RoleEntity findByName(String name);

    Boolean existsByName(String name);
}
