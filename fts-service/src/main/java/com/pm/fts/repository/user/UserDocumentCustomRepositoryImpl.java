package com.pm.fts.repository.user;

import com.pm.fts.document.user.UserDocument;
import com.pm.fts.repository.BaseDocumentCustomRepositoryImpl;
import com.pm.fts.web.dto.DocumentSearchRequest;
import com.pm.fts.web.dto.UserSearchResponseDTO;
import lombok.extern.java.Log;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
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
        NativeQuery searchQuery = getNativeQuery(payload,true);
        // 2. Execute search
        SearchHits<UserDocument> hits = esOps.search(searchQuery, UserDocument.class, IndexCoordinates.of(getIndex()));
        log.info("UserDocumentCustomRepositoryImpl -> quickSearch -> hits " + hits.getTotalHits());

        return new UserSearchResponseDTO(hits, hits.getTotalHits());
    }

    @Override
    public UserSearchResponseDTO advancedSearch(DocumentSearchRequest payload) throws Exception {
        NativeQuery searchQuery = getNativeQuery(payload,true);
        // 2. Execute search
        SearchHits<UserDocument> hits = esOps.search(searchQuery, UserDocument.class, IndexCoordinates.of(getIndex()));
        log.info("UserDocumentCustomRepositoryImpl -> advancedSearch-> hits " + hits.getTotalHits());
        return new UserSearchResponseDTO(hits, hits.getTotalHits());
    }

    @Override
    public Long countItems(DocumentSearchRequest payload) throws Exception {
        NativeQuery searchQuery = getNativeQuery(payload,false);
        return esOps.count(searchQuery, UserDocument.class, IndexCoordinates.of(getIndex()));
    }

    @Override
    public UserSearchResponseDTO findDocumentItems(DocumentSearchRequest payload) throws Exception {
        NativeQuery searchQuery = getNativeQuery(payload,false);
        // 2. Execute search
        SearchHits<UserDocument> hits = esOps.search(searchQuery, UserDocument.class, IndexCoordinates.of(getIndex()));
        log.info("UserDocumentCustomRepositoryImpl -> findDocumentItems-> hits " + hits.getTotalHits());
        return new UserSearchResponseDTO(hits, hits.getTotalHits());
    }
}
