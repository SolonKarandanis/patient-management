package com.pm.aiservice.config;

import com.pm.aiservice.common.AiConstants;
import org.springframework.ai.anthropic.AnthropicCacheOptions;
import org.springframework.ai.anthropic.AnthropicCacheStrategy;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

public class ChatModelRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        String provider = environment.getProperty("ai.provider", AiConstants.PROVIDER_ANTHROPIC).toLowerCase();

        switch (provider) {
            case AiConstants.PROVIDER_ANTHROPIC -> {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(AnthropicChatModel.class, () -> {
                    String apiKey = environment.getProperty("anthropic.api-key");
                    String model = environment.getProperty("anthropic.model", "claude-sonnet-4-6");
                    long maxTokens = environment.getProperty("anthropic.max-tokens", Long.class, 8192L);

                    com.anthropic.client.AnthropicClient client = com.anthropic.client.okhttp.AnthropicOkHttpClient.builder()
                            .apiKey(apiKey)
                            .build();
                    return AnthropicChatModel.builder()
                            .anthropicClient(client)
                            .options(AnthropicChatOptions.builder()
                                    .model(model)
                                    .maxTokens((int) maxTokens)
                                    .cacheOptions(AnthropicCacheOptions.builder()
                                            .strategy(AnthropicCacheStrategy.SYSTEM_AND_TOOLS)
                                            .build())
                                    .build())
                            .build();
                });
                registry.registerBeanDefinition("chatModel", builder.getBeanDefinition());
            }
            case AiConstants.PROVIDER_OPENAI -> {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(OpenAiChatModel.class, () -> {
                    String apiKey = environment.getProperty("openai.api-key");
                    String model = environment.getProperty("openai.model", "gpt-4o");
                    long maxTokens = environment.getProperty("openai.max-tokens", Long.class, 8192L);

                    com.openai.client.OpenAIClient client = com.openai.client.okhttp.OpenAIOkHttpClient.builder()
                            .apiKey(apiKey)
                            .build();
                    return OpenAiChatModel.builder()
                            .openAiClient(client)
                            .options(OpenAiChatOptions.builder()
                                    .model(model)
                                    .maxCompletionTokens((int) maxTokens)
                                    .build())
                            .build();
                });
                registry.registerBeanDefinition("chatModel", builder.getBeanDefinition());
            }
            case AiConstants.PROVIDER_GEMINI -> {
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(GoogleGenAiChatModel.class, () -> {
                    String apiKey = environment.getProperty("gemini.api-key");
                    String model = environment.getProperty("gemini.model", "gemini-2.0-flash");
                    int maxTokens = environment.getProperty("gemini.max-tokens", Integer.class, 8192);

                    com.google.genai.Client client = com.google.genai.Client.builder().apiKey(apiKey).build();
                    return GoogleGenAiChatModel.builder()
                            .genAiClient(client)
                            .options(GoogleGenAiChatOptions.builder()
                                    .model(model)
                                    .maxOutputTokens(maxTokens)
                                    .build())
                            .build();
                });
                registry.registerBeanDefinition("chatModel", builder.getBeanDefinition());
            }
            default -> throw new IllegalArgumentException("Unknown AI provider configured: " + provider);
        }
    }
}
