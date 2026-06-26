package com.pm.aiservice.infrastructure.anthropic;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.core.http.StreamResponse;
import com.anthropic.models.messages.*;
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
@ConditionalOnProperty(name = "ai.provider", havingValue = "anthropic")
public class AnthropicAdapter implements LlmPort {

    @Value("${anthropic.api-key}")
    private String apiKey;

    @Value("${anthropic.model}")
    private String model;

    @Value("${anthropic.max-tokens}")
    private long maxTokens;

    private AnthropicClient client;

    @PostConstruct
    void init() {
        client = AnthropicOkHttpClient.builder().apiKey(apiKey).build();
    }

    @Override
    public String chat(List<ChatMessage> history) {
        Message response = client.messages().create(buildParams(history));
        return response.content().stream()
                .flatMap(block -> block.text().stream())
                .map(TextBlock::text)
                .collect(Collectors.joining());
    }

    @Override
    public Flux<String> streamChat(List<ChatMessage> history) {
        return Flux.using(
                () -> client.messages().createStreaming(buildParams(history)),
                stream -> Flux.fromStream(stream.stream())
                        .mapNotNull(event -> event.contentBlockDelta()
                                .map(RawContentBlockDeltaEvent::delta)
                                .flatMap(RawContentBlockDelta::text)
                                .map(TextDelta::text)
                                .filter(t -> !t.isEmpty())
                                .orElse(null)),
                StreamResponse::close
        ).subscribeOn(Schedulers.boundedElastic());
    }

    private MessageCreateParams buildParams(List<ChatMessage> history) {
        List<MessageParam> params = history.stream()
                .filter(msg -> msg.getRole() != Role.SYSTEM)
                .map(msg -> MessageParam.builder()
                        .role(toSdkRole(msg.getRole()))
                        .content(msg.getContent())
                        .build())
                .collect(Collectors.toList());

        MessageCreateParams.Builder builder = MessageCreateParams.builder()
                .model(model)
                .maxTokens(maxTokens)
                .messages(params);

        history.stream()
                .filter(msg -> msg.getRole() == Role.SYSTEM)
                .map(ChatMessage::getContent)
                .findFirst()
                .ifPresent(builder::system);

        return builder.build();
    }

    private MessageParam.Role toSdkRole(Role role) {
        return switch (role) {
            case USER -> MessageParam.Role.USER;
            case ASSISTANT -> MessageParam.Role.ASSISTANT;
            case SYSTEM -> throw new IllegalStateException("SYSTEM messages are filtered before this point");
        };
    }
}
