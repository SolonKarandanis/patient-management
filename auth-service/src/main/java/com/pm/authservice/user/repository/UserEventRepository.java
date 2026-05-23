package com.pm.authservice.user.repository;

import com.pm.authservice.infrastructure.persistence.entity.UserEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserEventRepository extends JpaRepository<UserEventEntity,Integer> {

    @Query(name = UserEventEntity.FIND_BY_DOMAIN_ID)
    Optional<UserEventEntity> findByDomainId(@Param("domainId") UUID domainId);

    @Query(name = UserEventEntity.FIND_BY_USER_ID)
    List<UserEventEntity> findByUserId(@Param("userId") Integer userId);

    @Query(name = UserEventEntity.FIND_BY_USER_DOMAIN_ID)
    List<UserEventEntity> findByUserDomainId(@Param("userPublicId") UUID userPublicId);

    @Query(name = UserEventEntity.FIND_BY_USERNAME)
    List<UserEventEntity> findByUserName(@Param("username") String username);

    @Query(name = UserEventEntity.FIND_BY_EMAIL)
    List<UserEventEntity> findByUserEmail(@Param("email") String email);
}
