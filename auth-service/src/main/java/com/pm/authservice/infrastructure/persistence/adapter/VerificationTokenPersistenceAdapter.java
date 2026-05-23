package com.pm.authservice.infrastructure.persistence.adapter;

import com.pm.authservice.domain.model.VerificationToken;
import com.pm.authservice.domain.port.out.VerificationTokenPort;
import com.pm.authservice.infrastructure.persistence.entity.VerificationTokenJpaEntity;
import com.pm.authservice.user.repository.UserRepository;
import com.pm.authservice.user.repository.VerificationTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class VerificationTokenPersistenceAdapter implements VerificationTokenPort {

    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    public VerificationTokenPersistenceAdapter(VerificationTokenRepository verificationTokenRepository,
                                               UserRepository userRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public VerificationToken save(VerificationToken token) {
        Integer userId = userRepository.findIdByDomainId(token.getUserDomainId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found for domainId: " + token.getUserDomainId()));
        VerificationTokenJpaEntity entity = new VerificationTokenJpaEntity();
        entity.setDomainId(token.getDomainId());
        entity.setToken(token.getToken());
        entity.setUserId(userId);
        entity.setExpirationTime(token.getExpirationTime());
        VerificationTokenJpaEntity saved = verificationTokenRepository.save(entity);
        return toDomain(saved, token.getUserDomainId());
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepository.findByToken(token)
                .map(e -> toDomain(e, e.getUser().getDomainId()));
    }

    private VerificationToken toDomain(VerificationTokenJpaEntity entity, java.util.UUID userDomainId) {
        return VerificationToken.builder()
                .domainId(entity.getDomainId())
                .token(entity.getToken())
                .userDomainId(userDomainId)
                .expirationTime(entity.getExpirationTime())
                .build();
    }
}
