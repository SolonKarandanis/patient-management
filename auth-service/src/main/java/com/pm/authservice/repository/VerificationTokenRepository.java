package com.pm.authservice.repository;


import com.pm.authservice.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {

    @Query(name = VerificationToken.FIND_BY_TOKEN)
    public VerificationToken findByToken(String token);
}
