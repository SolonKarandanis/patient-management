package com.pm.aiservice.infrastructure.springai;

import com.pm.aiservice.domain.model.ChatMessage;
import com.pm.aiservice.domain.port.LlmPort;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
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
    private final ObjectProvider<ToolCallbackProvider> toolCallbackProviders;

    @Autowired
    public SpringAiAdapter(ChatModel chatModel, ChatMemory chatMemory, VectorStore vectorStore, ObjectProvider<ToolCallbackProvider> toolCallbackProviders) {
        String systemPrompt = """
                You are a support assistant for the Patient Management System.
                Help users navigate the application, understand its features, and answer questions about managing patients, \
                billing, analytics, notifications, and user accounts.
                Be concise and helpful. If you don't know something, say so — do not invent features.
                When relevant documentation is provided in [CONTEXT], use it to answer accurately.
                """;

        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        // 1. Database-backed conversation memory using Builder
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        // 2. pgvector-backed similarity search RAG using Builder
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder().topK(3).build())
                                .build()
                )
                .build();
        this.toolCallbackProviders = toolCallbackProviders;
    }

    @Override
    public String chat(String sessionId, String userMessage) {
        ChatClient.ChatClientRequestSpec spec = chatClient.prompt()
                .user(userMessage)
                // Bind this request to the session ID in the database-backed memory advisor
                .advisors(advisor -> advisor.param("chat_memory_conversation_id", sessionId));

        // Dynamically add all local @Tool beans and all remote MCP connection tools
        List<ToolCallbackProvider> providers = toolCallbackProviders.orderedStream().toList();
        for (ToolCallbackProvider provider : providers) {
            spec = spec.tools(provider);
        }

        return spec.call().content();
    }

    @Override
    public Flux<String> streamChat(String sessionId, String userMessage) {
        ChatClient.ChatClientRequestSpec spec = chatClient.prompt()
                .user(userMessage)
                // Bind this request to the session ID in the database-backed memory advisor
                .advisors(advisor -> advisor.param("chat_memory_conversation_id", sessionId));

        // Dynamically add all local @Tool beans and all remote MCP connection tools
        List<ToolCallbackProvider> providers = toolCallbackProviders.orderedStream().toList();
        for (ToolCallbackProvider provider : providers) {
            spec = spec.tools(provider);
        }

        return spec.stream().content();
    }
}
