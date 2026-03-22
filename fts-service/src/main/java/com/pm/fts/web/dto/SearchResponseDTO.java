package com.pm.fts.web.dto;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.Data;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public abstract class SearchResponseDTO<R, D> implements Serializable {
    private static final long serialVersionUID = 6364580673406318792L;

    protected List<R> content;

    protected Long totalElements;

    protected SearchResponseDTO(SearchHits<D> hits) {
        super();
        List<R> results = new ArrayList<>();
        hits.getSearchHits().forEach(hit -> results.add(toDTO(hit.getContent())));
        this.content = results;
        this.totalElements = hits.getTotalHits();
    }

    protected SearchResponseDTO(SearchHits<D> hits, long count) {
        super();
        List<R> results = new ArrayList<>();
        hits.getSearchHits().forEach(hit -> results.add(toDTO(hit.getContent())));
        this.content = results;
        this.totalElements = count;
    }

    protected SearchResponseDTO(SearchResponse<D> response, long count) {
        super();
        List<R> results = new ArrayList<>();
        List<Hit<D>> hits = response.hits().hits();
        for (Hit<D> hit: hits) {
            results.add(toDTO(hit.source()));
        }
        this.content = results;
        this.totalElements = count;
    }

    protected abstract R toDTO(D document);
}
