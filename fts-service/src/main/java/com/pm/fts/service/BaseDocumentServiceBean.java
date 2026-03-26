package com.pm.fts.service;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.pm.fts.document.AbstractDocument;
import com.pm.fts.exception.FtsEsException;
import com.pm.fts.web.dto.AbstractDocumentDTO;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

@Service
@Log
public abstract class BaseDocumentServiceBean implements BaseDocumentService{

    @Value("${document.index.pfx}")
    protected String indexPrefix;

    @Autowired
    protected ElasticsearchOperations esOps;

    @Autowired
    protected ElasticsearchClient client;

    protected String getIndexPrefix() {
        return indexPrefix ;
    }

    protected abstract String getIndex();

    @Override
    public AbstractDocument convertToDocument(AbstractDocumentDTO dto) {
        AbstractDocument document = new AbstractDocument();
        document.setId(dto.getId());
        document.setPublicId(dto.getPublicId());
        return document;
    }

    protected Boolean existsActiveItemIndex() {
        Boolean exists = false;
        ExistsRequest request = ExistsRequest.of(req -> req.index(getIndex()));

        try {
            BooleanResponse existsResponse = client.indices().exists(request);
            exists = existsResponse.value();
        } catch (ElasticsearchException | IOException e) {
            log.log(Level.SEVERE, "Caught Error: {}", e.getMessage());
        }

        return exists;
    }

    @Override
    public void deleteByIds(List<Integer> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            return;
        }
        TermsQueryField values = new TermsQueryField.Builder()
                .value(itemIds.stream().map(FieldValue::of).toList())
                .build();
        TermsQuery query = TermsQuery.of(q -> q.field("id").terms(values));
        DeleteByQueryRequest request = DeleteByQueryRequest.of(req -> req.index(getIndex()).query(query._toQuery()));
        try {
            client.deleteByQuery(request);
        } catch (ElasticsearchException | IOException e) {
            log.log(Level.SEVERE, "Caught Error: {}", e.getMessage());
        }
    }

    @Override
    public Boolean deleteDocumentIndex() throws FtsEsException {
        Boolean doesIndexExist = existsActiveItemIndex();
        if (!doesIndexExist) {
            throw new FtsEsException("Index does not exist");
        }

        DeleteIndexRequest deleteRequest = DeleteIndexRequest.of(req -> req.index(getIndex()));

        try {
            DeleteIndexResponse response = client.indices().delete(deleteRequest);
            return response.acknowledged();
        } catch (ElasticsearchException | IOException e) {
            log.log(Level.SEVERE, "Caught Error: {}", e.getMessage());
            throw new FtsEsException("Error when deleting index");
        }
    }
}
