package com.pm.authservice.service;

import com.pm.authservice.outbox.service.OutboxService;
import com.pm.authservice.service.fts.UserFullTextSearchService;
import com.pm.authservice.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdministrationServiceBean implements AdministrationService{

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

        return null;
    }

    @Override
    public Boolean deleteUserIndex() {
        log.debug("AdministrationServiceBean --> deleteUserIndex");
        return userFullTextSearchService.deleteUserIndex();
    }
}
