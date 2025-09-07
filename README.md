# starBank

A recommendation system for Star Bank that analyzes customer transactions and offers personalized banking products.

Supports static and dynamic rules, REST API, Telegram bot, statistics collection and caching.

--

## Technology stack
- Java 17, Spring Boot
- Gradle
- H2 (read-only) + PostgreSQL (dynamic rules)
- Liquibase (migration to the second DB)
- JDBC + JdbcTemplate
- Telegram Bot API
- Swagger / OpenAPI

---

## Features
- GET /recommendation/{user_id} — recommendations to the user.
- CRUD for dynamic rules: POST /rule, GET /rule, DELETE /rule/{product_id}.
- Statistics of triggers: GET /rule/stats.
- Technological endpoint for clearing caches: POST /management/clear-caches.
- Service information: GET /management/info.
- Telegram bot: command /recommend {username}.

---

## Quick start (Gradle)

### Build
bash
./gradlew clean build

---

## Documentation
- Wiki: ./wiki/Home.md
- OpenAPI specification: ./docs/openapi.yaml
- Diagrams: ./docs/architecture.png, ./docs/activity-diagram.png
- Deployment instructions: ./wiki/Deployment.md
