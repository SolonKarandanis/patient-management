# Patient Management System

A microservices-based healthcare application designed for managing patient records, billing, analytics, and notifications.

## Project Overview

The project follows an **Event-Driven Architecture (EDA)** with **CQRS** and **Outbox patterns** for reliable data consistency across services.

### Core Technologies
- **Backend:** Java 25, Spring Boot 4.0.x
- **Frontend:** Angular 21, PrimeNG, TailwindCSS, D3.js
- **Communication:**
  - **Async:** Apache Kafka (Event Sourcing), ActiveMQ Artemis (STOMP/WebSockets)
  - **Sync:** gRPC (Protobuf) for internal service calls, REST for external/frontend
- **Data Stores:** PostgreSQL (Main DB), ClickHouse (Analytics), Elasticsearch (FTS)
- **Infrastructure:** Docker Compose, Kafka Connect (Debezium), Prometheus & Grafana (Monitoring)
- **Integration:** Apache Camel

## Architecture

The system is composed of several independent services:
- `gateway`: Entry point for all external requests (Spring Cloud Gateway).
- `auth-service`: Handles authentication, authorization (JWT), and session management (Hazelcast).
- `patient-service`: Manages core patient information.
- `medical-records-service`: Handles patient clinical records.
- `billing-service`: Manages invoices and financial records.
- `payment-service`: Integrates with payment providers.
- `analytics-service`: Provides data insights using ClickHouse.
- `notification-service`: Sends alerts and notifications.
- `fts-service`: Full-text search capabilities using Elasticsearch.
- `stream-processor`: Real-time data processing via Kafka Streams.
- `camel-integration`: External system integrations.

## Building and Running

### Prerequisites
- Java 25
- Node.js & NPM
- Docker & Docker Compose

### Infrastructure Setup
Navigate to the `infrasturcture` directory and start the required services:
```bash
cd infrasturcture
docker compose up -d
```
This will start Kafka, Zookeeper, Elasticsearch, Kibana, ClickHouse, ActiveMQ, Prometheus, and Grafana.

### Backend Services
Each service can be built and run independently using Maven:
```bash
cd <service-directory>
./mvnw clean install
./mvnw spring-boot:run
```

### Frontend
Navigate to the `frontent` directory (note the spelling):
```bash
cd frontent
npm install
npm start
```

## Infrastructure Ports

| Service | Port | Description |
|---------|------|-------------|
| Grafana | 3000 | Monitoring Dashboards (admin/admin) |
| Kibana | 5601 | ELK Stack Visualization |
| Kafka UI | 8070 | Kafka Cluster Management |
| Prometheus | 9090 | Metrics Storage |
| ActiveMQ UI | 8161 | Message Broker Management |
| ClickHouse | 8123 | Analytics DB (HTTP Interface) |
| Elasticsearch | 9200 | Search Engine API |

## Testing

### Unit Tests
Run unit tests for a specific service:
```bash
cd <service-directory>
./mvnw test -Punit-tests
```

### Integration Tests
Integration tests use Testcontainers and require a Docker environment:
```bash
cd <service-directory>
./mvnw failsafe:integration-test -Pintegration-tests
```

To run all tests:
```bash
./mvnw verify -Pall-tests
```

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

## API Documentation
- **REST:** Swagger/OpenAPI UI is usually available at `http://localhost:<port>/swagger-ui.html`.
- **HTTP Client:** Sample requests can be found in `api-requests/`.
- **gRPC:** Protobuf definitions and sample requests are in `grpc-requests/`.
