# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Build all modules (must install before running novel-web)
mvn install -DskipTests

# Run from novel-web module
mvn spring-boot:run -pl novel-web

# Single module compile
mvn compile -pl novel-common
```

## Architecture

**Multi-module Maven project** — Spring Boot 2.7.18, Java 8, MyBatis, MySQL 8.0.

```
novel-outline (parent pom)
├── novel-common   — Entities, DTOs, Enums, MyBatis Mapper interfaces
├── novel-analyzer — AI analysis: DeepSeek client, chapter/volume/outline analysis, context management
├── novel-service  — Business logic: upload+parse, analysis orchestration (@Async)
└── novel-web      — Spring Boot app entry, REST controllers, config (YAML, logback, SQL)
```

**Dependency direction**: web → service → analyzer → common. All modules depend on common.

## Key Design Decisions

- **Java 8 + Spring Boot 2.7.18**: Deliberate downgrade from Spring Boot 3.x/Java 17 because the runtime only has JDK 8.
- **MyBatis, not JPA**: Entities are plain POJOs with `@Data` (Lombok). Mapper interfaces use annotation-based SQL (`@Select`, `@Insert`, `@Update`, `@Delete`). No XML mappers despite `mapper-locations` being configured.
- **Sequential analysis with accumulated context**: Chapters are analyzed one-by-one. `NovelContext` stores protagonist, characters, factions, foreshadowing, conflicts across the entire run. Context is pruned after each volume (max 10 foreshadowing, 5 conflicts) to manage prompt size.
- **DeepSeek API via RestTemplate**: OpenAI-compatible `/v1/chat/completions` endpoint. `RestTemplate.exchange()` (not RestClient — not available in Spring 5.x). System prompt + user message pattern with Chinese prompt engineering.
- **Encoding handling**: `juniversalchardet` detects file encoding, falls back to GBK for Chinese novels. `schema.sql` + `data.sql` use `spring.sql.init.encoding: UTF-8` to prevent double-encoding in MySQL.
- **`@Async` analysis**: `AnalysisOrchestrator.startAnalysis()` runs in a separate thread. `@EnableAsync` on the application class. No thread pool customization (uses Spring defaults).
- **Resume-after-crash**: `Novel.lastAnalyzedChapterId` tracks progress. `analyzeVolume()` skips chapters already marked `COMPLETED`.
- **Retry with exponential backoff**: 3 attempts, 2s/4s/8s delays on DeepSeek API failures.

## Configuration

- `application.yml` — Server port 8088, MySQL (localhost:3306/novel_outline), DeepSeek API settings
- `DEEPSEEK_API_KEY` env var required for AI analysis (or set in YAML)
- `MYSQL_PASSWORD` env var for database (defaults to `root`)
- `logback-spring.xml` — Console + rolling file logs in `log/` directory; SQL logs in separate file

## REST API

All under `http://localhost:8088/api/`:

| Method | Path | Purpose |
|--------|------|---------|
| POST | `/novels` | Upload .txt (multipart `file` + `parseRuleId`) |
| GET | `/novels` | List all novels |
| GET | `/novels/{id}` | Novel detail |
| DELETE | `/novels/{id}` | Delete novel + cascade |
| POST | `/novels/{id}/analyze` | Start async analysis |
| GET | `/novels/{id}/progress` | Analysis progress |
| GET | `/novels/{id}/outline` | Generated outline JSON |
| GET | `/parse-rules` | List parsing rules |
| POST | `/parse-rules` | Create parsing rule |
| PUT | `/parse-rules/{id}` | Update parsing rule |
| GET | `/volumes/{id}` | Volume detail |
| GET | `/volumes/by-novel/{novelId}` | Volumes by novel |
| GET | `/chapters/{id}` | Chapter detail |
| GET | `/chapters/by-novel/{novelId}` | Chapters by novel |

## Database

Tables: `novel`, `volume`, `chapter`, `novel_outline`, `parse_rule`. Auto-created by `schema.sql` on startup (`spring.sql.init.mode: always`). Seed data in `data.sql` inserts 3 default parse rules. Columns use underscore_case; MyBatis auto-maps to camelCase via `map-underscore-to-camel-case: true`.

## Entity Statuses

- **Novel**: `NOT_STARTED` → `ANALYZING` → `COMPLETED` / `FAILED`
- **Chapter**: `PENDING` → `COMPLETED` / `FAILED`
