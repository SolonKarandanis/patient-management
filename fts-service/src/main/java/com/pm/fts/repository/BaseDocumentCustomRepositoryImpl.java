package com.pm.fts.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.pm.fts.exception.UnsupportedEsQueryException;
import com.pm.fts.web.dto.DocumentSearchRequest;
import com.pm.fts.web.dto.SearchCriterion;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

@Component
@Log
public abstract class BaseDocumentCustomRepositoryImpl {

    @Value("${document.index.pfx}")
    protected String indexPrefix;

    protected String getIndexPrefix() {
        return indexPrefix ;
    }

    protected abstract String getIndex();

    protected BoolQuery.Builder getRoleAccess(DocumentSearchRequest payload) {
        return RepositoryUtils.getRoleAccessQuery(payload);
    }

    protected NativeQuery getNativeQuery(DocumentSearchRequest payload,Boolean withPageable) throws UnsupportedEsQueryException {
        NativeQueryBuilder searchQueryBuilder = NativeQuery.builder();
        searchQueryBuilder.withFilter(new Query(getBoolQueryBuilder(payload).build()));
        
        // Only apply SourceFilter if specific fields are requested, otherwise it defaults to returning the full source
        if (!CollectionUtils.isEmpty(payload.getResultFields())) {
            searchQueryBuilder.withSourceFilter(RepositoryUtils.getSourceFilter(payload.getResultFields(), null));
        }

        if(withPageable){
            searchQueryBuilder.withPageable(RepositoryUtils.toPageable(payload.getPaging()));
        }
        NativeQuery searchQuery = searchQueryBuilder.build();
        // Manual Logging for Visibility
        if (searchQuery.getFilter() != null) {
            log.info("----------------------------------------------------------------");
            log.info("ES Query Filter Payload: " + searchQuery.getFilter().toString());
            log.info("----------------------------------------------------------------");
        }
        if (searchQuery.getQuery() != null) {
            log.info("----------------------------------------------------------------");
            log.info("ES Query Payload: " + searchQuery.getQuery().toString());
            log.info("----------------------------------------------------------------");
        }
        return searchQuery;
    }

    protected BoolQuery.Builder getBoolQueryBuilder(DocumentSearchRequest payload) throws UnsupportedEsQueryException {
        BoolQuery.Builder bqbFilter = QueryBuilders.bool();
        // add role security
        bqbFilter = getRoleAccess(payload);
        //search for status active or for all items
        setItemStatus(bqbFilter, payload.getStatus());
        addSearchCriteria(payload, bqbFilter);
        return bqbFilter;
    }

    protected String getActiveStatus(){
        return "status.active";
    }

    protected void setItemStatus(BoolQuery.Builder bqbFilter, DocumentSearchRequest.Status status) {
        MatchQuery.Builder matchQueryBuilder = QueryBuilders.match();
        if (Objects.equals(DocumentSearchRequest.Status.ACTIVE, status)) {
            matchQueryBuilder.field("status").query(getActiveStatus());
            bqbFilter.must(new Query(matchQueryBuilder.build()));
        }
    }

    protected void addSearchCriteria(DocumentSearchRequest payload, BoolQuery.Builder bqbFilter) throws UnsupportedEsQueryException {
        if (Objects.nonNull(payload) && !CollectionUtils.isEmpty(payload.getCriteria())) {
            BoolQuery.Builder bqbCriteria = QueryBuilders.bool();
            for (SearchCriterion criterion : payload.getCriteria()) {
                QueryVariant query = (QueryVariant) RepositoryUtils.getCriterionQuery(criterion);
                if (SearchCriterion.FTSOperation.OR.equals(criterion.getOperation())) {
                    bqbCriteria.should(new Query(query));
                } else {
                    bqbFilter.must(new Query(query));
                }
            }
            bqbFilter.must(new Query(bqbCriteria.build()));
        }
    }
}
