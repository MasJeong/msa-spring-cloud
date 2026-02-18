# AGENTS.md

Repository guidance for autonomous coding agents working in `SPRING_CLOUD_MSA`.

## Scope and architecture

- This is a multi-repo-style workspace with **independent Gradle modules**.
- There is no root Gradle wrapper for all services; run commands inside each service directory.
- Main services: `user-service`, `order-service`, `catalog-service`, `cart-service`, `file-service`, `apigateway-service`, `config-service`, `discoveryservice`.
- Infra/dev orchestration is done with Docker Compose from repo root.

## External agent rule files

- `.cursorrules`: not found.
- `.cursor/rules/`: not found.
- `.github/copilot-instructions.md`: not found.
- This file is the authoritative agent guideline until those files are added.

## Required workflow defaults

- Prefer **small, module-local changes**; do not refactor unrelated modules.
- Use each module's `./gradlew` wrapper (not system `gradle`) for reproducibility.
- Validate with compile/tests in the module you changed before finalizing.
- Do not introduce new build plugins (Spotless/Checkstyle/etc.) unless explicitly requested.

## Build, test, and run commands

All commands below are run from a module directory (example: `user-service/`).

### Common module commands

- Install wrapper deps cache (used in Dockerfiles):
  - `./gradlew dependencies --no-daemon`
- Build artifact without tests (current Docker build pattern):
  - `./gradlew clean build -x test --no-daemon`
- Run all tests in a module:
  - `./gradlew test`
- Run one test class:
  - `./gradlew test --tests 'com.example.orderservice.OrderServiceApplicationTests'`
- Run one test method:
  - `./gradlew test --tests 'com.example.orderservice.OrderServiceApplicationTests.contextLoads'`

### Service startup (repo root)

- Start everything:
  - `docker-compose up -d`
- Local staged startup:
  - `docker-compose -f docker-compose-local.yml up -d mariadb redis kafka rabbitmq`
  - `docker-compose -f docker-compose-local.yml up -d config-service discovery-service`
  - `docker-compose -f docker-compose-local.yml up -d apigateway-service`
  - `docker-compose -f docker-compose-local.yml up -d user-service order-service catalog-service cart-service file-service`
  - `docker-compose -f docker-compose-local.yml up -d zipkin prometheus grafana`

## Command caveats and gotchas

- `jar { enabled = false }` is set in all module `build.gradle` files (plain jar disabled).
- Test task exists in all modules (`tasks.named('test') { useJUnitPlatform() }`).
- Tests are globally disabled in **some modules** via `tasks.withType(Test) { enabled = false }`:
  - `user-service/build.gradle`
  - `file-service/build.gradle`
- For those modules, `./gradlew test` will not execute tests until that block is changed.
- Do not assume a lint command exists; no Spotless/Checkstyle/PMD/Jacoco plugin configuration was found.

## Module command quick map

From repo root, use one of these patterns:

- `./user-service/gradlew <task>`
- `./order-service/gradlew <task>`
- `./catalog-service/gradlew <task>`
- `./cart-service/gradlew <task>`
- `./file-service/gradlew <task>`
- `./apigateway-service/gradlew <task>`
- `./config-service/gradlew <task>`
- `./discoveryservice/gradlew <task>`

## Code style and structure conventions

### Language and platform

- Java 17 toolchain across modules.
- Spring Boot 3.3.x and Spring Cloud 2023.0.x family.
- JUnit 5 platform (`useJUnitPlatform`) for tests.

### Package and layering

- Base package style: `com.example.<service>...`.
- Common layering pattern by domain:
  - `controller` (HTTP entrypoint)
  - `service` (business logic)
  - `repository` (persistence)
  - `domain` (JPA entities)
  - `dto` and `vo` (transport objects)
- Keep cross-service integration code in dedicated subpackages (`client`, `msgqueue`, `config`, `security`).

### Imports and formatting

- Use explicit imports; avoid wildcard imports.
- Typical import grouping in current code:
  1. project imports (`com.example...`)
  2. Lombok
  3. framework/third-party (`org.springframework...`, etc.)
  4. JDK (`java...`)
- Keep 4-space indentation and standard brace/newline formatting.
- Javadoc/KDoc-style comments are used on public APIs and important methods; follow existing tone.

### Naming conventions

- Controllers: `*Controller`
- Services: `*Service`
- Repositories: `*Repository`
- Entities: `*Entity` (except when aggregate name itself is used, e.g. `User`)
- DTOs: `*Dto`
- Response/request objects: `Response*`, `Request*`
- Enum/constants grouped under `enums` or `constants` packages.

### Dependency injection and annotations

- Prefer constructor injection via Lombok `@RequiredArgsConstructor` with `final` fields.
- Use Spring stereotypes explicitly: `@RestController`, `@Service`, `@Configuration`, `@Repository`.
- Use `@Slf4j` for logging instead of manual logger creation.

### DTO/entity mapping

- Model mapping is commonly handled via `ModelMapper`.
- Convert entities to response VOs in controller/service boundaries.
- Avoid exposing JPA entities directly from controller responses.

### Persistence and domain modeling

- JPA annotations use `jakarta.persistence` namespace.
- Keep entity relationship annotations explicit (`@OneToMany`, `@JoinColumn`, etc.).
- Prefer descriptive column constraints (`nullable`, `length`, `unique`) in entities.

### Error handling

- Do not swallow exceptions silently.
- For HTTP boundary failures, return meaningful status codes (`ResponseStatusException` or `ResponseEntity` statuses).
- For async/event flows (Kafka), log enough context (`orderId`, `productId`, reason) and apply compensation when implemented.
- Keep catch blocks narrow when possible; only use broad `Exception` catch at integration boundaries with explicit fallback.

### Logging

- Use parameterized logging (`log.info("... {}", value)`) not string concatenation.
- Prefer `debug` for high-volume trace logs, `info` for business events, `warn/error` for abnormal paths.
- Preserve traceability fields already configured in logging patterns (trace/span IDs via observability stack).

### Security and API behavior

- Follow current stateless security patterns in `user-service` and gateway filters.
- Preserve existing authorization annotations (`@PreAuthorize`) and request matcher behavior.
- Any auth/role change should include endpoint-level verification.

### Testing conventions

- Baseline test style uses `@SpringBootTest` and context-load tests per service.
- Keep tests under `src/test/java` mirroring production package layout.
- If adding focused service/repository tests, reuse existing test support classes where present.
- When tests are disabled by build script in a module, document that limitation in PR notes.

## Agent execution checklist

- Identify target module first.
- Run `./gradlew clean build -x test --no-daemon` for compile safety.
- Run `./gradlew test` where tests are enabled.
- If single-test validation is needed, use `--tests` class/method filters.
- Do not claim lint/format pass unless a real lint tool is added and executed.
- Keep changes consistent with package/layering conventions above.

## When uncertain

- Prefer matching nearest existing implementation in the same module.
- Do not propagate patterns from one module to all modules without checking local differences.
- Call out module-specific caveats (especially test-task disablement) in your final notes.
