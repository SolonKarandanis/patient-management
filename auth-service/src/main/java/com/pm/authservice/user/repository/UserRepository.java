package com.pm.authservice.user.repository;

import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.user.repository.projections.MinMaxUserId;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserJpaEntity, Integer>,
        JpaSpecificationExecutor<UserJpaEntity>, QuerydslPredicateExecutor<UserJpaEntity> {

    @Query(name = UserJpaEntity.FIND_BY_EMAIL)
    Optional<UserJpaEntity> findByEmail(String email);

    @Query(name = UserJpaEntity.FIND_BY_DOMAIN_ID)
    Optional<UserJpaEntity> findByDomainId(@Param("domainId") UUID domainId);

    @Query(name = UserJpaEntity.FIND_ID_BY_DOMAIN_ID)
    Optional<Integer> findIdByDomainId(@Param("domainId") UUID domainId);

    @Query(name = UserJpaEntity.FIND_BY_USERNAME)
    Optional<UserJpaEntity> findByUsername(String username);

    @EntityGraph(value = UserJpaEntity.GRAPH_USERS_ROLES, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT u FROM UserJpaEntity u ")
    List<UserJpaEntity> findUsersWithRoles(Specification<UserJpaEntity> spec);

    default List<String> findUserPermissions(Integer userId) {
        List<String> permissions = new ArrayList<>();

        List<Object> results = findUserRoleIdsByUserIdNativeQuery(userId);
        List<Integer> roleIds = new ArrayList<>();
        for (Object row : results) {
            roleIds.add(((Number) row).intValue());
        }

        List<Object> operationResults = findOperationKeysByRoleIdsAsObjectsNativeQuery(roleIds);
        for (Object row : operationResults) {
            permissions.add((String) row);
        }

        return permissions;
    }

    @Query(nativeQuery = true, name = UserJpaEntity.FIND_USER_ROLE_IDS_BY_USER_ID_NATIVE_QUERY)
    List<Object> findUserRoleIdsByUserIdNativeQuery(Integer userId);

    @Query(nativeQuery = true, name = UserJpaEntity.FIND_OPERATION_KEY_BY_ROLE_IDS_AS_OBJECTS_NATIVE_QUERY)
    List<Object> findOperationKeysByRoleIdsAsObjectsNativeQuery(List<Integer> roleIds);

    @Query("SELECT MIN(u.id) AS minId, MAX(u.id) AS maxId FROM UserJpaEntity u")
    MinMaxUserId findMinAndMaxUserId();

    @Query("SELECT user FROM UserJpaEntity user " +
            "LEFT JOIN FETCH user.roles r " +
            "WHERE user.id >= :minId " +
            "AND user.id <= :maxId " +
            "ORDER BY user.id ASC")
    List<UserJpaEntity> findUsersByIdRange(@Param("minId") Integer minId, @Param("maxId") Integer maxId);

}
