package com.pm.aiservice.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.tool.toolsearch.ToolIndex;
import org.springframework.ai.tool.toolsearch.index.vectorstore.VectorToolIndex;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Tool-reference embeddings live in their own pgvector table (tool_search_index),
 * separate from the clinical-knowledge RAG table (vector_store) that VectorStore
 * resolves to elsewhere. VectorToolIndex writes into whatever store it's given with
 * no content-type filtering, so sharing the RAG table would let tool descriptions
 * surface in clinical similarity search results.
 */
@Configuration
public class ToolSearchConfig {

    @Bean
    public VectorStore toolSearchVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName("tool_search_index")
                .dimensions(768)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .initializeSchema(false)
                .build();
    }

    @Bean
    public ToolIndex toolIndex(@Qualifier("toolSearchVectorStore") VectorStore toolSearchVectorStore) {
        return new VectorToolIndex(toolSearchVectorStore);
    }
}
