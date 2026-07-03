# Patient Management System

A microservices-based healthcare application designed for managing patient records, billing, analytics, notifications, and AI-powered support.

## Project Overview

The project follows an **Event-Driven Architecture (EDA)** with **CQRS** and **Outbox patterns** for reliable data consistency across services. It also integrates a cutting-edge **AI Assistant layer** with Retrieval-Augmented Generation (RAG).

### Core Technologies
- **Backend:** Java 25, Spring Boot 4.0.x, Spring AI 2.0.0
- **Frontend:** Angular 21, NgRx Signals, PrimeNG, TailwindCSS, D3.js
- **Communication:**
  - **Async:** Apache Kafka (Event Sourcing via Debezium Outbox), ActiveMQ Artemis (STOMP/WebSockets)
  - **Sync:** Spring RestClient (Internal REST calls), gRPC (Protobuf) for key internal service actions, REST for external/frontend
- **Data Stores:**
  - **Relational DB:** PostgreSQL 17 (Main DB for core services)
  - **Vector Store:** PostgreSQL 18 with pgvector (AI service embedding store)
  - **Analytics DB:** ClickHouse (Data analysis and insights)
  - **Search Engine:** Elasticsearch (Full-text search)
- **Infrastructure:** Docker Compose, Kafka Connect (Debezium CDC), Prometheus & Grafana (Monitoring)
- **Integration:** Apache Camel

## Architecture

The system is composed of several independent, modular services:
- `gateway`: Entry point for all external requests (Spring Cloud Gateway, WebFlux).
- `auth-service`: Handles authentication, authorization (dual-mode custom JWT or OAuth2 Keycloak), and session management (Hazelcast-clustered Spring Session). Built using **Hexagonal Architecture**.
- `ai-service`: Support assistant backend utilizing Spring AI 2.0.0. Provides conversational support with a 50-message sliding window and Retrieval-Augmented Generation (RAG) over local domain documentation. Built using **Hexagonal Architecture**.
- `patient-service`: Manages core patient information.
- `medical-records-service`: Handles patient clinical records.
- `billing-service`: Manages invoices and financial records.
- `payment-service`: Integrates with payment providers.
- `analytics-service`: Provides data insights using ClickHouse.
- `notification-service`: Sends alerts and notifications.
- `fts-service`: Full-text search capabilities using Elasticsearch.
- `stream-processor`: Real-time data processing via Kafka Streams.
- `camel-integration`: External system integrations.

---

## Building and Running

### Prerequisites
- Java 25
- Node.js & NPM
- Docker & Docker Compose
- Ollama (running locally at `http://localhost:11434` for embedding generation)

### Infrastructure Setup
1. Start the Docker-compose stack:
   ```bash
   cd infrasturcture
   docker compose up -d
   ```
   This starts Kafka, Zookeeper, Elasticsearch, Kibana, ClickHouse, ActiveMQ Artemis, Prometheus, and Grafana.
2. Ensure Postgres instances are running on:
   - `192.168.1.6:5432` — Core services main database.
   - `192.168.1.6:5433` — AI Service database (PostgreSQL 18 instance with `pgvector` extension enabled).

### Backend Services
Each service can be built and run independently from its own folder:
```bash
cd <service-directory>
./mvnw clean install
./mvnw spring-boot:run
```
*Note: For `auth-service`, use Gradle as the primary build system:*
```bash
cd auth-service
./gradlew build
./gradlew bootRun
```

### Frontend
Navigate to the `frontent` directory (note the folder spelling):
```bash
cd frontent
npm install
npm start
```

---

## Network and Port Configuration

### Infrastructure Ports

| Service | Port | Description |
|---------|------|-------------|
| Grafana | 3000 | Monitoring Dashboards (admin/admin) |
| Kibana | 5601 | ELK Stack Visualization |
| Kafka UI | 8070 | Kafka Cluster Management |
| Prometheus | 9090 | Metrics Storage |
| ActiveMQ UI | 8161 | Message Broker Management |
| ClickHouse | 8123 | Analytics DB (HTTP Interface) |
| Elasticsearch | 9200 | Search Engine API |

### Service Ports & Context Paths

| Service | Port | gRPC Port | Context Path | Gateway Path |
|---------|------|-----------|--------------|--------------|
| `gateway` | 4004 | — | — | — |
| `auth-service` | 4005 | — | `/auth-service` | `/auth/**`, `/i18n/**` |
| `ai-service` | 4012 | — | `/ai-service` | *Internal (Invoked via auth-service RestClient)* |
| `patient-service` | 4000 | 9091 | `/patient-service` | `/api/patients/**` |
| `billing-service` | 4001 | 9092 | — | *Internal (gRPC/Kafka only)* |
| `analytics-service`| 4002 | — | `/analytics-service` | `/analytics/**` |
| `fts-service` | 4006 | — | `/fts` | *Internal* |
| `camel-integration`| 4007 | — | `/camel-integration`| — |
| `payment-service` | 4008 | — | — | — |
| `medical-records` | 4009 | — | — | — |
| `notification` | 4010 | — | `/notification-service` | — |
| `stream-processor` | 4011 | — | — | — |

---

## Testing

### Unit Tests
Run unit tests for a specific service:
```bash
cd <service-directory>
./mvnw test -Punit-tests
```
*For `auth-service`: `./gradlew test -PskipUnitTests=false`*

### Integration Tests
Integration tests run inside Testcontainers and require a running Docker environment:
```bash
cd <service-directory>
./mvnw failsafe:integration-test -Pintegration-tests
```
*For `auth-service`: `./gradlew integrationTest -PskipIntegrationTests=false`*

To run all tests in a Maven service:
```bash
./mvnw verify -Pall-tests
```

To run cross-service integration tests:
```bash
cd integration-tests && ./mvnw test
```

---

## Development Conventions

### Code Style
- Follow standard Spring Boot and Java 25 conventions.
- Use **Lombok** for boilerplate reduction.
- **QueryDSL** is used for type-safe database queries.
- **Protobuf** definitions are located in the respective service resources or a shared module if applicable.

### Event Sourcing & Outbox Pattern
- Services use the **Debezium Outbox pattern** to ensure "at-least-once" delivery of events to Kafka.
- Look for `Outbox` entity and `OutboxService` in backend projects.

### Communication Patterns
- Prefer **gRPC** for internal, synchronous service-to-service calls.
- Use **Kafka events** for cross-service state propagation.
- Use **WebSockets (STOMP)** via ActiveMQ for real-time frontend updates.
- Synchronous HTTP communication is modern, using Spring **RestClient** instead of the older `RestTemplate` (e.g. `auth-service` → `ai-service` / `fts-service`).

---

## AI Integration & RAG Architecture

The support chatbot utilizes an advanced AI architecture within the `ai-service`:

### 1. Hexagonal Design
To prevent LLM provider lock-in, the service defines an `LlmPort` interface inside the `domain` layer. Out-of-the-box adapters (`AnthropicAdapter`, `OpenAiAdapter`, `GeminiAdapter`) implement this port and are dynamically activated using `@ConditionalOnProperty(name="ai.provider")`. 

### 2. Retrieval-Augmented Generation (RAG)
To provide accurate, contextual help based on local documentation, the chatbot injects relevant documentation context dynamically:
- **Vector Database:** Uses `pgvector` inside a PostgreSQL 18 instance (port 5433). It maintains a `vector_store` table with HNSW index using cosine similarity.
- **Embeddings:** Vector representations are generated using 768-dimensional embeddings via local Ollama (`nomic-embed-text`) or Google `text-embedding-004`.
- **Knowledge Seeding:** An automated `KnowledgeSeeder` component scans `classpath:knowledge/*.md` files, computes SHA-256 hashes to track state in `knowledge_seed_log`, splits documents into sections based on `##` subheadings, and dynamically updates the `vector_store` when updates occur.

### 3. Chat Memory and History
- Chat sessions are stateful, storing user and assistant turns in a `spring_ai_chat_memory` table.
- A sliding window algorithm preserves the last **50 messages** to ensure optimal performance and context constraints.
- Real-time history is queryable by clients using the `/chat/{sessionId}/history` endpoint, allowing the UI to maintain persistent state across reloads.

---

## API Documentation
- **REST:** Swagger/OpenAPI UI is usually available at `http://localhost:<port>/swagger-ui.html`.
- **HTTP Client:** Sample requests can be found in `api-requests/`.
- **gRPC:** Protobuf definitions and sample requests are in `grpc-requests/`.
