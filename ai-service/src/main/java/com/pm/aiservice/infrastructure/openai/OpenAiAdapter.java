package com.pm.aiservice.infrastructure.openai;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.pm.aiservice.domain.model.ChatMessage;
import com.pm.aiservice.domain.port.LlmPort;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "openai")
public class OpenAiAdapter implements LlmPort {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.max-tokens}")
    private long maxTokens;

    @Autowired
    private ObjectProvider<ToolCallbackProvider> mcpToolsProvider;

    private ChatClient chatClient;

    @PostConstruct
    void init() {
        OpenAIClient client = OpenAIOkHttpClient.builder().apiKey(apiKey).build();
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiClient(client)
                .options(OpenAiChatOptions.builder()
                        .model(model)
                        .maxCompletionTokens((int) maxTokens)
                        .build())
                .build();
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @Override
    public String chat(List<ChatMessage> history) {
        ChatClient.ChatClientRequestSpec spec = chatClient.prompt()
                .messages(toSpringAiMessages(history));

        ToolCallbackProvider mcpTools = mcpToolsProvider.getIfAvailable();
        if (mcpTools != null) {
            spec = spec.tools(mcpTools);
        }

        return spec.call().content();
    }

    @Override
    public Flux<String> streamChat(List<ChatMessage> history) {
        ChatClient.ChatClientRequestSpec spec = chatClient.prompt()
                .messages(toSpringAiMessages(history));

        ToolCallbackProvider mcpTools = mcpToolsProvider.getIfAvailable();
        if (mcpTools != null) {
            spec = spec.tools(mcpTools);
        }

        return spec.stream().content();
    }

    private List<Message> toSpringAiMessages(List<ChatMessage> history) {
        return history.stream()
                .map(msg -> switch (msg.getRole()) {
                    case USER -> new UserMessage(msg.getContent());
                    case ASSISTANT -> new AssistantMessage(msg.getContent());
                    case SYSTEM -> new SystemMessage(msg.getContent());
                })
                .collect(Collectors.toList());
    }
}
