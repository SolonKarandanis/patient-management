package com.pm.aiservice.infrastructure.openai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.core.http.StreamResponse;
import com.openai.models.chat.completions.*;
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

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "openai")
public class OpenAiAdapter implements LlmPort {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.max-tokens}")
    private long maxTokens;

    private OpenAIClient client;

    @PostConstruct
    void init() {
        client = OpenAIOkHttpClient.builder().apiKey(apiKey).build();
    }

    @Override
    public String chat(List<ChatMessage> history) {
        ChatCompletion completion = client.chat().completions().create(buildParams(history));
        return completion.choices().stream()
                .findFirst()
                .flatMap(c -> c.message().content())
                .orElse("");
    }

    @Override
    public Flux<String> streamChat(List<ChatMessage> history) {
        return Flux.using(
                () -> client.chat().completions().createStreaming(buildParams(history)),
                stream -> Flux.fromStream(stream.stream())
                        .mapNotNull(chunk -> chunk.choices().stream()
                                .findFirst()
                                .flatMap(c -> c.delta().content())
                                .filter(t -> !t.isEmpty())
                                .orElse(null)),
                StreamResponse::close
        ).subscribeOn(Schedulers.boundedElastic());
    }

    private ChatCompletionCreateParams buildParams(List<ChatMessage> history) {
        ChatCompletionCreateParams.Builder builder = ChatCompletionCreateParams.builder()
                .model(model)
                .maxCompletionTokens(maxTokens);

        for (ChatMessage msg : history) {
            switch (msg.getRole()) {
                case USER -> builder.addUserMessage(msg.getContent());
                case ASSISTANT -> builder.addAssistantMessage(msg.getContent());
                case SYSTEM -> builder.addSystemMessage(msg.getContent());
            }
        }

        return builder.build();
    }
}
