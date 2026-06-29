package com.pm.aiservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KnowledgeSeeder implements ApplicationRunner {

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    public KnowledgeSeeder(VectorStore vectorStore, JdbcTemplate jdbcTemplate) {
        this.vectorStore = vectorStore;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("KnowledgeSeeder starting...");

        try {
            seedKnowledge();
        } catch (Exception e) {
            log.error("Knowledge base seeding failed — RAG will not be available. Cause: {}", e.getMessage(), e);
        }
    }

    private void seedKnowledge() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:knowledge/*.md");

        log.info("Found {} knowledge file(s) to evaluate.", resources.length);

        for (Resource resource : resources) {
            String filename = resource.getFilename();
            String content = readContent(resource);
            String hash = sha256(content);

            String storedHash = jdbcTemplate.query(
                    "SELECT content_hash FROM knowledge_seed_log WHERE source = ?",
                    rs -> rs.next() ? rs.getString("content_hash") : null,
                    filename
            );

            if (hash.equals(storedHash)) {
                log.info("Knowledge file '{}' unchanged — skipping.", filename);
                continue;
            }

            if (storedHash != null) {
                log.info("Knowledge file '{}' changed — removing old chunks and re-ingesting.", filename);
                jdbcTemplate.update(
                        "DELETE FROM vector_store WHERE metadata->>'source' = ?", filename
                );
            } else {
                log.info("Knowledge file '{}' is new — ingesting.", filename);
            }

            List<Document> documents = splitBySections(content).stream()
                    .filter(section -> !section.isBlank())
                    .map(section -> new Document(
                            section.trim(),
                            Map.of("source", filename, "category", "help")
                    ))
                    .collect(Collectors.toList());

            vectorStore.add(documents);

            jdbcTemplate.update(
                    """
                    INSERT INTO knowledge_seed_log (source, content_hash)
                    VALUES (?, ?)
                    ON CONFLICT (source) DO UPDATE SET content_hash = EXCLUDED.content_hash
                    """,
                    filename, hash
            );

            log.info("  Ingested {} sections from '{}'.", documents.size(), filename);
        }
    }

    private String readContent(Resource resource) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private String sha256(String content) throws Exception {
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(content.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }

    private List<String> splitBySections(String content) {
        List<String> sections = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String line : content.split("\n")) {
            if (line.startsWith("## ") && !current.isEmpty()) {
                sections.add(current.toString());
                current = new StringBuilder();
            }
            current.append(line).append("\n");
        }
        if (!current.isEmpty()) {
            sections.add(current.toString());
        }
        return sections;
    }
}
