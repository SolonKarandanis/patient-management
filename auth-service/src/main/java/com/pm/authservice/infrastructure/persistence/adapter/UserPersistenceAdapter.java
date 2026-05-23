package com.pm.authservice.infrastructure.persistence.adapter;

import com.pm.authservice.domain.model.AccountStatus;
import com.pm.authservice.domain.model.Role;
import com.pm.authservice.domain.model.User;
import com.pm.authservice.domain.model.UserSearchCriteria;
import com.pm.authservice.domain.port.out.UserPort;
import com.pm.authservice.infrastructure.persistence.entity.QUserJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.persistence.mapper.UserMapper;
import com.pm.authservice.user.repository.RoleRepository;
import com.pm.authservice.user.repository.UserRepository;
import com.pm.authservice.util.AppConstants;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
public class UserPersistenceAdapter implements UserPort {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    public UserPersistenceAdapter(UserRepository userRepository,
                                  RoleRepository roleRepository,
                                  UserMapper userMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public User save(User user) {
        UserJpaEntity entity = userRepository.findByDomainId(user.getDomainId())
                .orElseGet(UserJpaEntity::new);
        if (entity.getDomainId() == null) {
            entity.setDomainId(user.getDomainId());
        }
        userMapper.updateEntity(entity, user);
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            List<Integer> roleIds = user.getRoles().stream()
                    .map(Role::getId)
                    .collect(Collectors.toList());
            Set<RoleJpaEntity> roleEntities = new HashSet<>(roleRepository.findAllById(roleIds));
            entity.setRoles(roleEntities);
        }
        UserJpaEntity saved = userRepository.save(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void delete(UUID domainId) {
        userRepository.findByDomainId(domainId).ifPresent(userRepository::delete);
    }

    @Override
    public Optional<User> findByDomainId(UUID domainId) {
        return userRepository.findByDomainId(domainId)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDomain);
    }

    @Override
    public boolean hasRole(UUID domainId, String roleName) {
        return userRepository.existsByDomainIdAndRoleName(domainId, roleName);
    }

    @Override
    public boolean hasPermission(UUID domainId, String operationName) {
        return userRepository.existsByDomainIdAndOperationName(domainId, operationName);
    }

    @Override
    public List<String> findPermissions(UUID domainId) {
        return userRepository.findPermissionsByDomainId(domainId);
    }

    @Override
    public List<User> search(UserSearchCriteria criteria, boolean includeDeleted) {
        Predicate predicate = buildSearchPredicate(criteria, includeDeleted);
        PageRequest pageRequest = buildPageRequest(criteria);
        return userRepository.findAll(predicate, pageRequest)
                .getContent()
                .stream()
                .map(userMapper::toDomain)
                .toList();
    }

    @Override
    public long count(UserSearchCriteria criteria, boolean includeDeleted) {
        return userRepository.count(buildSearchPredicate(criteria, includeDeleted));
    }

    private Predicate buildSearchPredicate(UserSearchCriteria criteria, boolean includeDeleted) {
        QUserJpaEntity user = QUserJpaEntity.userJpaEntity;
        BooleanBuilder builder = new BooleanBuilder();
        if (!includeDeleted) {
            builder.and(user.status.ne(AccountStatus.DELETED));
        }
        boolean isOrSearch = AppConstants.SEARCH_TYPE_OR.equals(criteria.searchMethod());
        if (StringUtils.hasLength(criteria.email())) {
            if (isOrSearch) builder.or(user.email.eq(criteria.email()));
            else builder.and(user.email.eq(criteria.email()));
        }
        if (StringUtils.hasLength(criteria.username())) {
            if (isOrSearch) builder.or(user.username.eq(criteria.username()));
            else builder.and(user.username.eq(criteria.username()));
        }
        if (StringUtils.hasLength(criteria.name())) {
            var nameExpr = user.firstName.eq(criteria.name()).or(user.lastName.eq(criteria.name()));
            if (isOrSearch) builder.or(nameExpr);
            else builder.and(nameExpr);
        }
        if (StringUtils.hasLength(criteria.status())) {
            AccountStatus accountStatus = AccountStatus.fromValue(criteria.status());
            if (accountStatus != null) {
                if (isOrSearch) builder.or(user.status.eq(accountStatus));
                else builder.and(user.status.eq(accountStatus));
            }
        }
        if (StringUtils.hasLength(criteria.roleName())) {
            RoleJpaEntity role = roleRepository.findByName(criteria.roleName());
            if (role != null) {
                if (isOrSearch) builder.or(user.roles.contains(role));
                else builder.and(user.roles.contains(role));
            }
        }
        return builder;
    }

    private PageRequest buildPageRequest(UserSearchCriteria criteria) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        if (StringUtils.hasLength(criteria.sortColumn()) && StringUtils.hasLength(criteria.sortDirection())) {
            sort = Sort.by(Sort.Direction.valueOf(criteria.sortDirection()), criteria.sortColumn());
        }
        return PageRequest.of(criteria.page(), criteria.size(), sort);
    }
}
