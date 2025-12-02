package com.pm.authservice.user.repository;

import com.pm.authservice.user.model.UserEntity;
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

    default List<String> getUserPermissions(Integer userId) {
        List<String> permissions = new ArrayList<>();

        List<Object> results = getUserRoleIdsByUserIdNativeQuery(userId);
        List<Integer> roleIds = new ArrayList<>();
        for (Object row : results) {
            roleIds.add(((Number) row).intValue());
        }

        List<Object> operationResults = getOperationKeysByRoleIdsAsObjectsNativeQuery(roleIds);
        for (Object row : operationResults) {
            permissions.add((String) row);
        }

        return permissions;
    }

    @Query(nativeQuery = true, name = UserEntity.FIND_USER_ROLE_IDS_BY_USER_ID_NATIVE_QUERY)
    List<Object> getUserRoleIdsByUserIdNativeQuery(Integer userId);

    @Query(nativeQuery = true, name = UserEntity.FIND_OPERATION_KEY_BY_ROLE_IDS_AS_OBJECTS_NATIVE_QUERY)
    List<Object> getOperationKeysByRoleIdsAsObjectsNativeQuery(List<Integer> roleIds);



}
