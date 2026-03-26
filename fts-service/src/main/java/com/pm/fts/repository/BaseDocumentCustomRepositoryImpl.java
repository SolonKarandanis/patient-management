package com.pm.fts.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.*;
import com.pm.fts.exception.UnsupportedEsQueryException;
import com.pm.fts.web.dto.DocumentSearchRequest;
import com.pm.fts.web.dto.SearchCriterion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

@Component
public abstract class BaseDocumentCustomRepositoryImpl {

    @Value("${ccm.document.index.pfx}")
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
        searchQueryBuilder.withFilter(new Query(getBoolQueryBuilder(payload).build())).withSourceFilter(RepositoryUtils.getSourceFilter(payload.getResultFields(), null));
        if(withPageable){
            searchQueryBuilder.withPageable(RepositoryUtils.toPageable(payload.getPaging()));
        }
        return searchQueryBuilder.build();
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

    protected void setItemStatus(BoolQuery.Builder bqbFilter, DocumentSearchRequest.Status status) {
        MatchQuery.Builder matchQueryBuilder = QueryBuilders.match();
        if (Objects.equals(DocumentSearchRequest.Status.ACTIVE, status)) {
            matchQueryBuilder.field("status").query("status.active");
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
