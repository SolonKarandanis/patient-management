package com.pm.authservice.repository;

import com.pm.authservice.model.UserEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Integer> ,
        JpaSpecificationExecutor<UserEntity>, QuerydslPredicateExecutor<UserEntity> {

    @Query(name = UserEntity.FIND_BY_EMAIL)
    Optional<UserEntity> findByEmail(String email);

    @Query(name = UserEntity.FIND_BY_PUBLIC_ID)
    Optional<UserEntity> findByPublicId(@Param("publicId") UUID publicId);

    @Query(name = UserEntity.FIND_ID_BY_PUBLIC_ID)
    Optional<Integer> findIdByPublicId(@Param("publicId") UUID publicId);

    @Query(name = UserEntity.FIND_BY_USERNAME)
    Optional<UserEntity> findByUsername(String username);

    @EntityGraph(value =  UserEntity.GRAPH_USERS_ROLES,type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT u FROM UserEntity u ")
    List<UserEntity> findUsersWithRoles(Specification<UserEntity> spec);

}
