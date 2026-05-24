package com.pm.authservice.infrastructure.administration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pm.authservice.infrastructure.messaging.outbox.OutboxService;
import com.pm.authservice.infrastructure.persistence.entity.RoleJpaEntity;
import com.pm.authservice.infrastructure.persistence.entity.UserJpaEntity;
import com.pm.authservice.infrastructure.persistence.repository.UserJpaRepository;
import com.pm.authservice.infrastructure.persistence.repository.projections.MinMaxUserId;
import com.pm.authservice.infrastructure.search.UserFullTextSearchService;
import com.pm.authservice.infrastructure.search.dto.UserDocumentDTO;
import com.pm.authservice.infrastructure.util.AppConstants;
import com.pm.authservice.infrastructure.web.dto.MinMaxUserIdDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResourceAccessException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdministrationServiceBean implements AdministrationService {
    private static final int USER_UPDATE_BATCH = 50;
    private static final Logger log = LoggerFactory.getLogger(AdministrationServiceBean.class);

    private final UserFullTextSearchService userFullTextSearchService;
    private final UserJpaRepository userRepository;
    private final OutboxService outboxService;

    @Value("${search.elasticSearch.indexing.method:elastic.search.indexing.outbox}")
    private String elasticSearchIndexingMethod;

    public AdministrationServiceBean(
            UserFullTextSearchService userFullTextSearchService,
            UserJpaRepository userRepository,
            OutboxService outboxService) {
        this.userFullTextSearchService = userFullTextSearchService;
        this.userRepository = userRepository;
        this.outboxService = outboxService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Boolean triggerAdHocIndexing() {
        log.info("AdministrationServiceBean --> triggerAdHocIndexing");
        MinMaxUserId result = userRepository.findMinAndMaxUserId();
        MinMaxUserIdDTO dto = new MinMaxUserIdDTO(result.getMinId(), result.getMaxId());
        Integer minUserId = dto.minId();
        Integer maxUserId = dto.maxId();
        log.info("AdministrationServiceBean --> triggerAdHocIndexing --> IdRange=[{}-{}]", minUserId, maxUserId);
        if (minUserId != null && maxUserId != null) {
            for (int i = minUserId; i <= maxUserId; i += USER_UPDATE_BATCH) {
                int j = Math.min(i + USER_UPDATE_BATCH - 1, maxUserId);
                log.info(" CASE-Processing: minId={}, maxId={} ", i, j);
                List<UserJpaEntity> users = userRepository.findUsersByIdRange(i, j);
                if (!CollectionUtils.isEmpty(users)) {
                    try {
                        handleReindexing(users);
                    } catch (Exception ex) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected void handleReindexing(List<UserJpaEntity> users) throws ResourceAccessException, JsonProcessingException {
        List<UserDocumentDTO> documentDto = toDocumentDtoList(users);
        if (AppConstants.ELASTIC_SEARCH_INDEXING_METHOD_HTTP.equals(elasticSearchIndexingMethod)) {
            userFullTextSearchService.indexUsers(documentDto);
        }
        if (AppConstants.ELASTIC_SEARCH_INDEXING_METHOD_OUTBOX.equals(elasticSearchIndexingMethod)) {
            outboxService.indexUsersByCreatingUserEvents(documentDto);
        }
    }

    @Override
    public Boolean deleteUserIndex() {
        log.info("AdministrationServiceBean --> deleteUserIndex");
        return userFullTextSearchService.deleteUserIndex();
    }

    private List<UserDocumentDTO> toDocumentDtoList(List<UserJpaEntity> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return Collections.emptyList();
        }
        return userList.stream().map(this::toDocumentDto).toList();
    }

    private UserDocumentDTO toDocumentDto(UserJpaEntity user) {
        UserDocumentDTO dto = new UserDocumentDTO();
        dto.setId(user.getId());
        dto.setPublicId(user.getDomainId().toString());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus() != null ? user.getStatus().getValue() : null);
        dto.setIsVerified(user.getIsVerified());
        dto.setIsEnabled(user.getIsEnabled());
        if (user.getRoles() != null) {
            List<String> roleNames = user.getRoles().stream()
                    .map(RoleJpaEntity::getName)
                    .collect(Collectors.toList());
            dto.setRolesNames(roleNames);
            List<Integer> roleIds = user.getRoles().stream()
                    .map(RoleJpaEntity::getId)
                    .collect(Collectors.toList());
            dto.setRoleIds(roleIds);
        }
        return dto;
    }
}
