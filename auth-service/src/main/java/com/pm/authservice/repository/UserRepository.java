package com.pm.authservice.repository;

import com.pm.authservice.model.User;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> ,
        JpaSpecificationExecutor<User> {

    @Query(name = User.FIND_BY_EMAIL)
    Optional<User> findByEmail(String email);

    @Query(name = User.FIND_BY_PUBLIC_ID)
    Optional<User> findByPublicId(@Param("publicId") UUID publicId);

    @Query(name = User.FIND_ID_BY_PUBLIC_ID)
    Optional<Integer> findIdByPublicId(@Param("publicId") UUID publicId);

    @EntityGraph(value =  User.GRAPH_USERS_ROLES,type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT u FROM User u ")
    List<User> findUsersWithRoles(Specification<User> spec);
}
