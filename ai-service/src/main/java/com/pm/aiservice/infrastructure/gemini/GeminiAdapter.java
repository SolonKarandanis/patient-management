package com.pm.aiservice.infrastructure.gemini;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.pm.aiservice.domain.model.ChatMessage;
import com.pm.aiservice.domain.model.Role;
import com.pm.aiservice.domain.port.LlmPort;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "gemini")
public class GeminiAdapter implements LlmPort {

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    @Value("${gemini.max-tokens}")
    private int maxTokens;

    private Client client;

    @PostConstruct
    void init() {
        client = Client.builder().apiKey(apiKey).build();
    }

    @Override
    public String chat(List<ChatMessage> history) {
        GenerateContentResponse response = client.models.generateContent(
                model, buildContents(history), buildConfig(history));
        return response.text() != null ? response.text() : "";
    }

    @Override
    public Flux<String> streamChat(List<ChatMessage> history) {
        return Flux.using(
                () -> client.models.generateContentStream(model, buildContents(history), buildConfig(history)),
                stream -> Flux.fromIterable(stream)
                        .flatMapIterable(GenerateContentResponse::parts)
                        .mapNotNull(part -> {
                            String t = part.text().orElse(null);
                            return (t != null && !t.isEmpty()) ? t : null;
                        }),
                stream -> {
                    try { stream.close(); } catch (Exception ignored) {}
                }
        ).subscribeOn(Schedulers.boundedElastic());
    }

    private List<Content> buildContents(List<ChatMessage> history) {
        return history.stream()
                .filter(msg -> msg.getRole() != Role.SYSTEM)
                .map(msg -> Content.builder()
                        .role(msg.getRole() == Role.USER ? "user" : "model")
                        .parts(List.of(Part.fromText(msg.getContent())))
                        .build())
                .collect(Collectors.toList());
    }

    private GenerateContentConfig buildConfig(List<ChatMessage> history) {
        GenerateContentConfig.Builder configBuilder = GenerateContentConfig.builder()
                .maxOutputTokens(maxTokens);

        history.stream()
                .filter(msg -> msg.getRole() == Role.SYSTEM)
                .map(ChatMessage::getContent)
                .findFirst()
                .ifPresent(systemText -> configBuilder.systemInstruction(
                        Content.builder()
                                .parts(List.of(Part.fromText(systemText)))
                                .build()));

        return configBuilder.build();
    }
}
