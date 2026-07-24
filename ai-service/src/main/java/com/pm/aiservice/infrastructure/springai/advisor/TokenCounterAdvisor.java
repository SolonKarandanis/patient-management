package com.pm.aiservice.infrastructure.springai.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.core.Ordered;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Logs per-call and running-total token usage. State is process-wide (one instance
 * lives for the lifetime of the singleton ChatClient) — the running total is across
 * all sessions since startup, not scoped per conversation, and resets on restart.
 */
public class TokenCounterAdvisor implements BaseAdvisor {

    private static final Logger log = LoggerFactory.getLogger(TokenCounterAdvisor.class);

    private final AtomicInteger requests = new AtomicInteger();
    private final AtomicInteger promptTokens = new AtomicInteger();
    private final AtomicInteger completionTokens = new AtomicInteger();
    private final AtomicInteger totalTokens = new AtomicInteger();

    @Override
    public int getOrder() {
        // Last in the chain, so it sees each tool-calling iteration's usage.
        return Ordered.LOWEST_PRECEDENCE - 1000;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest request, AdvisorChain chain) {
        return request;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse response, AdvisorChain chain) {
        var chatResponse = response.chatResponse();
        if (chatResponse == null) {
            return response;
        }
        var usage = chatResponse.getMetadata().getUsage();
        this.requests.incrementAndGet();
        this.promptTokens.addAndGet(usage.getPromptTokens());
        this.completionTokens.addAndGet(usage.getCompletionTokens());
        this.totalTokens.addAndGet(usage.getTotalTokens());

        log.info("tokens this call: prompt={} completion={} total={} | running total over {} call(s): {}",
                usage.getPromptTokens(), usage.getCompletionTokens(), usage.getTotalTokens(),
                this.requests.get(), this.totalTokens.get());
        return response;
    }
}
