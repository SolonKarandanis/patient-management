package com.pm.aiservice.infrastructure.springai;

import com.pm.aiservice.domain.model.ChatMessage;
import com.pm.aiservice.domain.port.LlmPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Primary
public class SpringAiAdapter implements LlmPort {

    private final ChatClient chatClient;
    private final ObjectProvider<ToolCallbackProvider> mcpToolsProvider;

    @Autowired
    public SpringAiAdapter(ChatModel chatModel, ObjectProvider<ToolCallbackProvider> mcpToolsProvider) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.mcpToolsProvider = mcpToolsProvider;
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
