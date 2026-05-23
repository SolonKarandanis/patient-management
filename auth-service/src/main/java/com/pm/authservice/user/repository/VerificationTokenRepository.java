package com.pm.authservice.user.repository;

import com.pm.authservice.infrastructure.persistence.entity.VerificationTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationTokenJpaEntity, Integer> {

    @Query("SELECT vt FROM VerificationTokenJpaEntity vt LEFT JOIN FETCH vt.user u WHERE vt.token = :token")
    Optional<VerificationTokenJpaEntity> findByToken(String token);
}
