package com.pm.authservice.user.repository;


import com.pm.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoleRepository extends JpaRepository<RoleJpaEntity, Integer> {

    @Query(name = RoleJpaEntity.FIND_BY_IDS)
    List<RoleJpaEntity> findByIds(List<Integer> ids);

    @Query(name = RoleJpaEntity.FIND_BY_NAME)
    RoleJpaEntity findByName(String name);

    Boolean existsByName(String name);
}
