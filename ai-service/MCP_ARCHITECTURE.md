# Model Context Protocol (MCP) & Spring AI Architecture

This document describes the dynamic, composable Model Context Protocol (MCP) client-server architecture and Aspect-Oriented Advisor pipeline implemented inside the `ai-service`.

---

## 1. Architectural Overview

The `ai-service` acts as an **Agentic Broker** utilizing Spring AI 2.0.0. It acts as an **MCP Client** that orchestrates conversations for the frontend support chatbot and dynamically resolves tools and cross-cutting features (Memory, RAG, Logging) using pluggable **Advisors**.

```
 ┌─────────────────┐       POST /chat
 │ Frontend Angular │ ──────────────────────────┐
 └─────────────────┘                           │
                                               ▼
 ┌──────────────────┐               ┌─────────────────────┐
 │   ai-service     │               │    auth-service     │
 │   (MCP Client)   │ ◄──────────── │ (Auth & Gateway RT) │
 └────────┬─────────┘               └─────────────────────┘
          │
          │ [Delegates raw message + sessionId]
          ▼
 ┌──────────────────┐
 │   ChatService    │ [No custom RAG or Memory boilerplate]
 └────────┬─────────┘
          │
          ▼
 ┌──────────────────┐
 │  SpringAiAdapter │ [Maintains Advisor Interceptor Pipeline]
 └────────┬─────────┘
          │
          ├──► [Memory Advisor]: MessageChatMemoryAdvisor -> Auto-reads/saves conversation turns to PostgreSQL DB
          ├──► [RAG Advisor]: QuestionAnswerAdvisor -> Auto similarity-searches pgvector & augments prompts
          │
          ▼  [Tool Invocation Spec]
          ├──► [Local Tool]: AiServiceMcpTools -> Exposes pgvector guides and session transcripts
          └───► [Remote MCP Tool]: patient-service -> Query patient records over SSE
```

---

## 2. Dynamic Model Registration (`ChatModelRegistrar`)

To prevent lock-in and support multiple LLM vendors, the active `ChatModel` bean is registered programmatically at runtime using `ChatModelRegistrar`.

* **How it works:** `ChatModelRegistrar` implements Spring's `ImportBeanDefinitionRegistrar` and `EnvironmentAware`. It reads the `ai.provider` property from the environment and uses an enhanced `switch` expression to instantiate and register **exactly one** unique `ChatModel` bean definition.
* **Benefits:** 
  * Avoids multiple-bean autowiring collisions during Spring context startup.
  * Allows developers to swap providers (`anthropic`, `openai`, `gemini`) dynamically via `application.properties` without changing any Java code.
  * Autowires Spring Boot's unified `ChatClient.Builder` automatically using the registered active bean.

---

## 3. Aspect-Oriented Interceptor Pipeline (Spring AI Advisors)

Instead of manually orchestrating conversation history and retrieving document contexts in the service layer, the `SpringAiAdapter` configures a declarative **Advisor Interceptor Pipeline** inside its `ChatClient` builder.

```java
this.chatClient = ChatClient.builder(chatModel)
        .defaultSystem(systemPrompt) // Prepends standard clinical system instructions
        .defaultAdvisors(
                // 1. Intercepts calls to load/save chat messages from/to PostgreSQL automatically
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                // 2. Intercepts calls to similarity-search pgvector & inject [CONTEXT] dynamically
                QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder().topK(3).build())
                        .build()
        )
        .build();
```

### Request Execution & Parameter Binding
When executing a request, the adapter passes the raw user message and binds the dynamic conversational parameters (like `chat_memory_conversation_id`) in a single fluent thread:

```java
@Override
public String chat(String sessionId, String userMessage) {
    ChatClient.ChatClientRequestSpec spec = chatClient.prompt()
            .user(userMessage)
            // Binds request thread to the database conversation ID
            .advisors(advisor -> advisor.param("chat_memory_conversation_id", sessionId));

    // Stream and attach all registered local and remote MCP tools
    List<ToolCallbackProvider> providers = toolCallbackProviders.orderedStream().toList();
    for (ToolCallbackProvider provider : providers) {
        spec = spec.tools(provider);
    }

    return spec.call().content();
}
```

---

## 4. Local Tools vs. Remote Tools

### A. Local Tools (`AiServiceMcpTools`)
Local tools are operations executed directly within the `ai-service` JVM. Any Spring Bean annotated with `@Tool` is automatically detected by Spring Boot and compiled into a local `ToolCallbackProvider`.
* **Exposed Tools:**
  * `searchClinicalKnowledge(String query)`: Executes a cosine similarity search against the PostgreSQL 18 `pgvector` store.
  * `listChatSessions()`: Lists active chat conversation IDs inside `spring_ai_chat_memory`.
  * `getChatSessionTranscript(String sessionId)`: Generates a chronological text transcript of a chat session for auditing.

### B. Remote Tools (External Microservices)
Remote tools are exposed by external microservices (like `patient-service` or `billing-service`) over HTTP/SSE. The `ai-service` registers them as an MCP client, discovers their tools on startup, and makes them available to the active LLM.

---

## 5. How to Expose & Consume Remote Tools

To connect a remote service (e.g., `patient-service`) as an MCP server:

### Step 1: Add Dependency to the Remote Service
Add the Spring AI MCP Server starter to the remote service's `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-mcp-server</artifactId>
</dependency>
```

### Step 2: Annotate Remote Methods with `@Tool`
In the remote service, mark any methods you want to expose with `@Tool`:
```java
@Component
public class PatientMcpTools {

    @Autowired
    private PatientService patientService;

    @Tool(description = "Search for a patient by first or last name")
    public List<PatientDto> searchPatient(String query) {
        return patientService.search(query);
    }
}
```

### Step 3: Configure Server in Remote Service
Expose the MCP endpoints over SSE:
```properties
spring.ai.mcp.server.enabled=true
spring.ai.mcp.server.transport=SSE
spring.ai.mcp.server.path=/mcp
```

### Step 4: Add Connection to `ai-service`
Add the client connection in `ai-service/src/main/resources/application.properties`:
```properties
spring.ai.mcp.client.connections.patient-service.protocol=HTTP
spring.ai.mcp.client.connections.patient-service.uri=http://localhost:4000/patient-service/mcp
```

On next startup, `ai-service` will auto-negotiate with `patient-service`, discover the `searchPatient` tool, and give your chatbot active capabilities to search patient records!

---

## 6. Configuration Reference (`application.properties`)

Below is the required reference configuration to maintain this clean, collision-free architecture:

```properties
# Active provider selection (anthropic | openai | gemini)
ai.provider=anthropic

# Auto-configuration exclusions (essential to prevent default bean collisions)
# Handled dynamically/statically in AiServiceApplication class:
# - OllamaChatAutoConfiguration
# - AnthropicChatAutoConfiguration
# - OpenAiChatAutoConfiguration
# - OpenAiEmbeddingAutoConfiguration
# - GoogleGenAiChatAutoConfiguration
# - GoogleGenAiTextEmbeddingAutoConfiguration
# - GoogleGenAiEmbeddingConnectionAutoConfiguration

# Disable Springdoc QueryDSL to prevent reflection crashes due to Spring Data 4 package moves
springdoc.querydsl.enabled=false

# Enable MCP Client
spring.ai.mcp.client.enabled=true
```
