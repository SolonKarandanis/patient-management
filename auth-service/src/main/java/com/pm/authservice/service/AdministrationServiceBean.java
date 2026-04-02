package com.pm.authservice.service;

import com.pm.authservice.outbox.service.OutboxService;
import com.pm.authservice.service.fts.UserFullTextSearchService;
import com.pm.authservice.user.dto.MinMaxUserIdDTO;
import com.pm.authservice.user.model.UserEntity;
import com.pm.authservice.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class AdministrationServiceBean implements AdministrationService{
    private static final int USER_UPDATE_BATCH = 50;
    private static final Logger log = LoggerFactory.getLogger(AdministrationServiceBean.class);

    private final UserFullTextSearchService userFullTextSearchService;
    private final UserService userService;
    private final OutboxService outboxService;

    @Value("${search.elasticSearch.indexing.method:elastic.search.indexing.outbox}")
    private Boolean elasticSearchIndexingMethod;

    public AdministrationServiceBean(
            UserFullTextSearchService userFullTextSearchService,
            UserService userService,
            OutboxService outboxService) {
        this.userFullTextSearchService = userFullTextSearchService;
        this.userService = userService;
        this.outboxService = outboxService;
    }


    @Override
    public Boolean triggerAdHocIndexing() {
        log.debug("AdministrationServiceBean --> triggerAdHocIndexing");
        MinMaxUserIdDTO dto = userService.getMinAndMaxUserId();
        Integer minUserId = dto.minId();
        Integer maxUserId = dto.maxId();
        log.debug("AdministrationServiceBean --> triggerAdHocIndexing --> IdRange=[{}-{}]",minUserId,maxUserId);
        if(minUserId != null && maxUserId != null){
            for (int i = minUserId; i <= maxUserId; i += USER_UPDATE_BATCH){
                int j = Math.min(i + USER_UPDATE_BATCH - 1, maxUserId);
                log.info(" CASE-Processing: minId={}, maxId={} ", i, j);
                List<UserEntity> users = userService.findUsersToBeIndexedByIdRange(i,j);
                if(!CollectionUtils.isEmpty(users)){
                    initiateReindexing(users);
                }
            }
        }
        return null;
    }

    protected void initiateReindexing(List<UserEntity> users){

    }

    @Override
    public Boolean deleteUserIndex() {
        log.debug("AdministrationServiceBean --> deleteUserIndex");
        return userFullTextSearchService.deleteUserIndex();
    }
}
