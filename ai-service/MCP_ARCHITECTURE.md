# Model Context Protocol (MCP) & Spring AI Architecture

This document describes the dynamic, composable Model Context Protocol (MCP) client-server architecture and Aspect-Oriented Advisor pipeline implemented inside the `ai-service`.

---

## 1. Architectural Overview

The `ai-service` acts as an **Agentic Broker** utilizing Spring AI 2.0.0. It orchestrates conversations for the frontend support chatbot and dynamically resolves tools and cross-cutting features (Memory, RAG, Tool Search, Logging) using pluggable **Advisors**. It is wired as an **MCP Client** (`spring-ai-starter-mcp-client` on the classpath) and as an **MCP Server** (`spring-ai-starter-mcp-server` on the classpath too), but as of this writing neither is actually configured — see §6.

```
 ┌─────────────────┐       POST /chat
 │ Frontend Angular │ ──────────────────────────┐
 └─────────────────┘                           │
                                               ▼
 ┌──────────────────┐               ┌─────────────────────┐
 │   ai-service     │               │    auth-service     │
 │                  │ ◄──────────── │ (Auth & Gateway RT) │
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
          ├──► [1. Memory]:    MessageChatMemoryAdvisor      -> Auto-reads/saves conversation turns to PostgreSQL DB
          ├──► [2. RAG]:       QuestionAnswerAdvisor          -> Similarity-searches pgvector `vector_store` (Ollama embeddings) & augments prompts
          ├──► [3. ToolSearch]:ToolSearchToolCallingAdvisor   -> Resolves only relevant tools via ToolIndex (pgvector `tool_search_index`), instead of sending every tool def on every call
          ├──► [4. Logging]:   AvailableToolsLoggingAdvisor   -> Logs tools visible to / called by the model
          ├──► [5. Metrics]:   TokenCounterAdvisor            -> Logs per-call + running-total token usage
          │
          ▼  [Tool Invocation Spec — providers attached dynamically per request via ObjectProvider<ToolCallbackProvider>]
          └──► [Local Tool]: AiServiceMcpTools -> Exposes pgvector knowledge search + session transcripts

 Remote MCP tools (e.g. a hypothetical patient-service MCP server) are NOT wired today — see §5
 for the connection mechanism and §6 for why it's inert without `spring.ai.mcp.client.connections.*`.
```

---

## 2. Dynamic Model Registration (`ChatModelRegistrar`)

To prevent lock-in and support multiple LLM vendors, the active `ChatModel` bean is registered programmatically at runtime using `ChatModelRegistrar`.

* **How it works:** `ChatModelRegistrar` implements Spring's `ImportBeanDefinitionRegistrar` and `EnvironmentAware`. It reads the `ai.provider` property from the environment and uses an enhanced `switch` expression to instantiate and register **exactly one** unique `ChatModel` bean definition.
* **Benefits:**
  * Avoids multiple-bean autowiring collisions during Spring context startup.
  * Allows developers to swap providers (`anthropic`, `openai`, `gemini`) dynamically via `application.properties` without changing any Java code.
  * Autowires Spring Boot's unified `ChatClient.Builder` automatically using the registered active bean.
* **Prompt caching:** for the `anthropic` branch, the registered `AnthropicChatOptions` sets `.cacheOptions(AnthropicCacheOptions.builder().strategy(AnthropicCacheStrategy.SYSTEM_AND_TOOLS).build())` — Anthropic `cache_control` caching of the system prompt and tool definitions is **enabled**, not just available.

---

## 3. Aspect-Oriented Interceptor Pipeline (Spring AI Advisors)

Instead of manually orchestrating conversation history, document retrieval, and tool resolution in the service layer, `SpringAiAdapter` configures a declarative **Advisor Interceptor Pipeline** inside its `ChatClient` builder. The pipeline currently has five advisors, in order:

```java
this.chatClient = ChatClient.builder(chatModel)
        .defaultSystem(systemPrompt) // Prepends the support-assistant system instructions
        .defaultAdvisors(
                // 1. Intercepts calls to load/save chat messages from/to PostgreSQL automatically
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                // 2. Intercepts calls to similarity-search pgvector & inject [CONTEXT] dynamically
                QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(SearchRequest.builder().topK(3).build())
                        .build(),
                // 3. Progressive tool disclosure: replaces the ChatClient's default auto-registered
                //    tool-calling advisor, resolving only relevant tools per call via a ToolIndex
                //    instead of sending every tool definition every time (see §4)
                ToolSearchToolCallingAdvisor.builder()
                        .toolIndex(toolIndex)
                        .build(),
                // 4. Observability: logs which tools were visible to the model vs. which it called
                new AvailableToolsLoggingAdvisor(),
                // 5. Observability: logs per-call and running-total token usage (process-wide counter,
                //    resets on restart — not scoped per session)
                new TokenCounterAdvisor()
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

    // Attach every registered ToolCallbackProvider — local @Tool beans today, plus
    // any remote MCP connection tools once/if those are configured (see §5, §6)
    List<ToolCallbackProvider> providers = toolCallbackProviders.orderedStream().toList();
    for (ToolCallbackProvider provider : providers) {
        spec = spec.tools(provider);
    }

    return spec.call().content();
}
```

---

## 4. Progressive Tool Disclosure (`ToolSearchToolCallingAdvisor`)

As more tools get registered (today: the three local `@Tool`s below; eventually: remote MCP tools too), sending every tool's full JSON schema on every LLM call wastes context and tokens. `ToolSearchToolCallingAdvisor` (from `spring-ai-tool-search-advisor`) replaces the ChatClient's default tool-calling advisor and instead resolves only the tools relevant to the current turn.

* **`ToolIndex` bean** (`config/ToolSearchConfig.java`): a `VectorToolIndex` — it embeds tool descriptions and does similarity search over them, conceptually the same way `QuestionAnswerAdvisor` does for RAG documents.
* **Separate pgvector table:** `VectorToolIndex` is backed by its **own** table, `tool_search_index` (768-dim, HNSW, cosine) — deliberately distinct from the clinical-knowledge `vector_store` table used by `QuestionAnswerAdvisor`. `VectorToolIndex` writes into whatever store it's given with no content-type filtering, so sharing the RAG table would let tool descriptions leak into clinical similarity-search results.
* **Migration:** `db/changelog/00000000000005_tool_search_index.xml` creates the table.
* Today, with only 3 local tools registered, this mostly costs a similarity-search round trip for little benefit — its value grows as more local tools and (eventually) remote MCP tools are added.

---

## 5. Local Tools vs. Remote Tools

### A. Local Tools (`AiServiceMcpTools`)
Local tools are operations executed directly within the `ai-service` JVM. Any Spring Bean annotated with `@Tool` is automatically detected by Spring Boot and compiled into a local `ToolCallbackProvider`.
* **Exposed Tools:**
  * `searchClinicalKnowledge(String query)`: Executes a cosine similarity search against the `vector_store` pgvector table.
  * `listChatSessions()`: Lists active chat conversation IDs inside `spring_ai_chat_memory`.
  * `getChatSessionTranscript(String sessionId)`: Generates a chronological text transcript of a chat session for auditing.

### B. Remote Tools (External Microservices) — NOT YET WIRED
The mechanism below lets a remote microservice (e.g. `patient-service` or `billing-service`) expose tools over HTTP/SSE that `ai-service` discovers as an MCP client. **As of this writing, no service in this repo implements an MCP server, and `ai-service`'s `application.properties` has no `spring.ai.mcp.client.connections.*` entries** — `spring-ai-starter-mcp-client`/`-mcp-server` are on the classpath but otherwise dormant. §6 explains why. Treat §6 below as a how-to for adding the first one, not a description of something already running.

---

## 6. How to Expose & Consume Remote Tools

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

On next startup, `ai-service` will auto-negotiate with `patient-service`, discover the `searchPatient` tool, and give your chatbot active capabilities to search patient records — once this is actually done (it isn't yet, see §5B).

---

## 7. Configuration Reference (`application.properties`)

Current, as-set configuration relevant to this architecture:

```properties
# Active provider selection (anthropic | openai | gemini)
ai.provider=anthropic

# Disable Springdoc QueryDSL to prevent reflection crashes due to Spring Data 4 package moves
springdoc.querydsl.enabled=false
```

Auto-configuration exclusions live in code, not properties — `AiServiceApplication`'s `@SpringBootApplication(excludeName = {...})` excludes (to prevent default-bean collisions with `ChatModelRegistrar`'s manually-registered beans):
- `OllamaChatAutoConfiguration`, `AnthropicChatAutoConfiguration`, `OpenAiChatAutoConfiguration`, `GoogleGenAiChatAutoConfiguration` (chat models)
- `OpenAiEmbeddingAutoConfiguration`, `GoogleGenAiTextEmbeddingAutoConfiguration`, `GoogleGenAiEmbeddingConnectionAutoConfiguration` (embedding models — leaving Ollama's `OllamaEmbeddingAutoConfiguration` as the only one active, since embeddings currently come from local Ollama `nomic-embed-text`, not OpenAI or Google)

**Not currently set**, despite earlier drafts of this doc implying otherwise:
```properties
# Only needed once a remote MCP connection is actually added (§6) — absent today,
# which is why there is no remote MCP tool in the §1 diagram.
spring.ai.mcp.client.enabled=true
spring.ai.mcp.client.connections.<name>.protocol=HTTP
spring.ai.mcp.client.connections.<name>.uri=...
```
