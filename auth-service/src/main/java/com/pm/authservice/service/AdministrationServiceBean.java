package com.pm.authservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pm.authservice.dto.UserDocumentDTO;
import com.pm.authservice.outbox.service.OutboxService;
import com.pm.authservice.service.fts.UserFullTextSearchService;
import com.pm.authservice.user.dto.MinMaxUserIdDTO;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.user.service.UserService;
import com.pm.authservice.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdministrationServiceBean implements AdministrationService{
    private static final int USER_UPDATE_BATCH = 50;
    private static final Logger log = LoggerFactory.getLogger(AdministrationServiceBean.class);

    private final UserFullTextSearchService userFullTextSearchService;
    private final UserService userService;
    private final GenericService genericService;
    private final OutboxService outboxService;

    @Value("${search.elasticSearch.indexing.method:elastic.search.indexing.outbox}")
    private String elasticSearchIndexingMethod;

    public AdministrationServiceBean(
            UserFullTextSearchService userFullTextSearchService,
            UserService userService,
            GenericService genericService,
            OutboxService outboxService) {
        this.userFullTextSearchService = userFullTextSearchService;
        this.userService = userService;
        this.genericService = genericService;
        this.outboxService = outboxService;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Boolean triggerAdHocIndexing() {
        log.info("AdministrationServiceBean --> triggerAdHocIndexing");
        MinMaxUserIdDTO dto = userService.getMinAndMaxUserId();
        Integer minUserId = dto.minId();
        Integer maxUserId = dto.maxId();
        log.info("AdministrationServiceBean --> triggerAdHocIndexing --> IdRange=[{}-{}]",minUserId,maxUserId);
        if(minUserId != null && maxUserId != null){
            for (int i = minUserId; i <= maxUserId; i += USER_UPDATE_BATCH){
                int j = Math.min(i + USER_UPDATE_BATCH - 1, maxUserId);
                log.info(" CASE-Processing: minId={}, maxId={} ", i, j);
                List<UserEntity> users = userService.findUsersToBeIndexedByIdRange(i,j);
                if(!CollectionUtils.isEmpty(users)){
                    try{
                        handleReindexing(users);
                    }catch (Exception ex){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected void handleReindexing(List<UserEntity> users) throws ResourceAccessException, JsonProcessingException {
        List<UserDocumentDTO> documentDto = genericService.convertToDocumentDtoList(users);
        if(AppConstants.ELASTIC_SEARCH_INDEXING_METHOD_HTTP.equals(elasticSearchIndexingMethod)){
            userFullTextSearchService.indexUsers(documentDto);
        }
        if(AppConstants.ELASTIC_SEARCH_INDEXING_METHOD_OUTBOX.equals(elasticSearchIndexingMethod)){
            outboxService.indexUsersByCreatingUserEvents(documentDto);
        }
    }

    @Override
    public Boolean deleteUserIndex() {
        log.info("AdministrationServiceBean --> deleteUserIndex");
        return userFullTextSearchService.deleteUserIndex();
    }
}
