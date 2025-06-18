package com.pm.authservice.repository;


import com.pm.authservice.model.VerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationTokenEntity, Integer> {

    @Query(name = VerificationTokenEntity.FIND_BY_TOKEN)
    public VerificationTokenEntity findByToken(String token);
}
