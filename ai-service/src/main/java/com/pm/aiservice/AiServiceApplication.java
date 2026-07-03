package com.pm.aiservice;

import com.pm.aiservice.config.ChatModelRegistrar;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

@SpringBootApplication(excludeName = {
        // Exclude all auto-configured ChatModels (since our ChatModelRegistrar registers our selected active one)
        "org.springframework.ai.model.ollama.autoconfigure.OllamaChatAutoConfiguration",
        "org.springframework.ai.model.anthropic.autoconfigure.AnthropicChatAutoConfiguration",
        "org.springframework.ai.model.openai.autoconfigure.OpenAiChatAutoConfiguration",
        "org.springframework.ai.model.google.genai.autoconfigure.chat.GoogleGenAiChatAutoConfiguration",

        // Exclude all conflicting non-Ollama EmbeddingModels (leaving only Ollama for pgvector document embeddings)
        "org.springframework.ai.model.openai.autoconfigure.OpenAiEmbeddingAutoConfiguration",
        "org.springframework.ai.model.google.genai.autoconfigure.embedding.GoogleGenAiTextEmbeddingAutoConfiguration",
        "org.springframework.ai.model.google.genai.autoconfigure.embedding.GoogleGenAiEmbeddingConnectionAutoConfiguration"
})
@Import(ChatModelRegistrar.class)
public class AiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiServiceApplication.class, args);
    }

    @Bean
    public static BeanFactoryPostProcessor removeQuerydslBeanPostProcessor() {
        return new PriorityOrderedBeanFactoryPostProcessor();
    }

    private static class PriorityOrderedBeanFactoryPostProcessor implements BeanFactoryPostProcessor, PriorityOrdered {
        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            if (beanFactory instanceof BeanDefinitionRegistry registry) {
                if (registry.containsBeanDefinition("queryDslQuerydslPredicateOperationCustomizer")) {
                    registry.removeBeanDefinition("queryDslQuerydslPredicateOperationCustomizer");
                }
            }
        }

        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }
    }
}
