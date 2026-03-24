package com.pm.fts.repository.user;

import com.pm.fts.repository.BaseDocumentCustomRepositoryImpl;
import com.pm.fts.web.dto.DocumentSearchRequest;
import com.pm.fts.web.dto.UserSearchResponseDTO;
import lombok.extern.java.Log;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Repository;

@Repository
@Log
public class UserDocumentCustomRepositoryImpl extends BaseDocumentCustomRepositoryImpl
        implements UserDocumentCustomRepository<UserSearchResponseDTO,DocumentSearchRequest>{

    private static final String USER_INDEX = "auth_service_users";

    private final ElasticsearchOperations esOps;

    public UserDocumentCustomRepositoryImpl(ElasticsearchOperations esOps) {
        this.esOps = esOps;
    }

    @Override
    protected String getIndex() {
        return getIndexPrefix() + USER_INDEX;
    }

    @Override
    public UserSearchResponseDTO quickSearch(DocumentSearchRequest payload) throws Exception {
        return null;
    }

    @Override
    public UserSearchResponseDTO advancedSearch(DocumentSearchRequest payload) throws Exception {
        return null;
    }

    @Override
    public Long countItems(DocumentSearchRequest payload) throws Exception {
        return 0L;
    }

    @Override
    public UserSearchResponseDTO findActiveItems(DocumentSearchRequest payload) throws Exception {
        return null;
    }
}
