package com.pm.aiservice.infrastructure.springai.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.model.tool.ToolCallingChatOptions;

import java.util.List;

/**
 * Logs, for every LLM call in the tool-calling loop, the tools visible to the model
 * and the tools it chooses to call. Purely observational — registering it does not
 * make any tool visible; it only reports what {@link ToolCallingChatOptions} already
 * resolved for that request.
 */
public class AvailableToolsLoggingAdvisor implements BaseAdvisor {

    public static final int DEFAULT_ORDER = 1000;

    private static final Logger log = LoggerFactory.getLogger(AvailableToolsLoggingAdvisor.class);

    private final int order;

    public AvailableToolsLoggingAdvisor() {
        this(DEFAULT_ORDER);
    }

    public AvailableToolsLoggingAdvisor(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest request, AdvisorChain chain) {
        List<String> toolNames = List.of();
        if (request.prompt().getOptions() instanceof ToolCallingChatOptions options
                && options.getToolCallbacks() != null) {
            toolNames = options.getToolCallbacks()
                    .stream()
                    .map(tc -> tc.getToolDefinition().name())
                    .sorted()
                    .toList();
        }

        log.info("LLM call - {} tool(s) visible to the model: {}", toolNames.size(), toolNames);

        Message last = request.prompt().getLastUserOrToolResponseMessage();
        if (last instanceof ToolResponseMessage toolResponse) {
            toolResponse.getResponses()
                    .forEach(r -> log.debug("tool result [{}]: {}", r.name(), truncate(r.responseData(), 260)));
        }
        return request;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse response, AdvisorChain chain) {
        var chatResponse = response.chatResponse();
        if (chatResponse == null) {
            return response;
        }
        chatResponse.getResults()
                .stream()
                .map(Generation::getOutput)
                .filter(message -> !message.getToolCalls().isEmpty())
                .flatMap(message -> message.getToolCalls().stream())
                .forEach(toolCall -> log.info("model -> calls {}({})", toolCall.name(),
                        truncate(toolCall.arguments(), 160)));
        return response;
    }

    private static String truncate(String text, int max) {
        if (text == null) {
            return "";
        }
        return text.length() <= max ? text : text.substring(0, max) + "...";
    }
}
