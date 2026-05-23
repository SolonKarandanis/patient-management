package com.pm.authservice.controller;

import com.pm.authservice.domain.model.VerificationToken;
import com.pm.authservice.domain.port.out.VerificationTokenPort;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import testcontainers.postgres.PostgresDBTestConfiguration;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Import(PostgresDBTestConfiguration.class)
public class VerificationTokenIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private VerificationTokenPort verificationTokenPort;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("Token round-trip: save a token and find it by string value")
    @Test
    void saveAndFindByToken() {
        UserJpaEntity user = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No users in test DB"));

        String tokenValue = UUID.randomUUID().toString();
        VerificationToken created = VerificationToken.create(tokenValue, user.getDomainId());

        verificationTokenPort.save(created);

        Optional<VerificationToken> found = verificationTokenPort.findByToken(tokenValue);

        assertTrue(found.isPresent());
        assertEquals(tokenValue, found.get().getToken());
        assertEquals(user.getDomainId(), found.get().getUserDomainId());
        assertNotNull(found.get().getDomainId());
        assertFalse(found.get().isExpired());
    }
}
