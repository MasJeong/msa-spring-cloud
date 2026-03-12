# AGENTS.md

Repository guidance for coding agents working in `SPRING_CLOUD_MSA`.

## 1) Workspace and Scope

- This repo is a multi-module workspace with independent Gradle wrappers per service.
- There is no root Gradle wrapper for all modules.
- Core modules: `user-service`, `order-service`, `catalog-service`, `cart-service`, `file-service`, `apigateway-service`, `config-service`, `discoveryservice`.
- Prefer module-local changes. Do not refactor unrelated services in one task.

## 2) Rule Files (Cursor / Copilot)

- `.cursorrules`: not found
- `.cursor/rules/`: not found
- `.github/copilot-instructions.md`: not found
- Use this file as the authoritative agent rule set for now.

## 3) Build/Test Commands (Verified)

Run commands from the target module directory unless noted.

### Core Gradle commands

- Download dependencies (used in Docker build cache):
  - `./gradlew dependencies --no-daemon`
- Build without tests (current Docker pattern):
  - `./gradlew clean build -x test --no-daemon`
- Run all tests in a module:
  - `./gradlew test`
- Run a single test class:
  - `./gradlew test --tests 'com.example.orderservice.OrderServiceApplicationTests'`
- Run a single test method:
  - `./gradlew test --tests 'com.example.orderservice.OrderServiceApplicationTests.contextLoads'`

### From repo root (module wrapper map)

- `./user-service/gradlew <task>`
- `./order-service/gradlew <task>`
- `./catalog-service/gradlew <task>`
- `./cart-service/gradlew <task>`
- `./file-service/gradlew <task>`
- `./apigateway-service/gradlew <task>`
- `./config-service/gradlew <task>`
- `./discoveryservice/gradlew <task>`

### Local infra startup (repo root)

- Full startup:
  - `docker-compose up -d`
- Staged startup:
  - `docker-compose -f docker-compose-local.yml up -d mariadb redis kafka rabbitmq`
  - `docker-compose -f docker-compose-local.yml up -d config-service discovery-service`
  - `docker-compose -f docker-compose-local.yml up -d apigateway-service`
  - `docker-compose -f docker-compose-local.yml up -d user-service order-service catalog-service cart-service file-service`
  - `docker-compose -f docker-compose-local.yml up -d zipkin prometheus grafana`

## 4) Build/Test Caveats

- `jar { enabled = false }` is configured across service modules.
- `user-service` and `file-service` disable tests via `tasks.withType(Test) { enabled = false }`.
- `catalog-service/build.gradle` has `useJUnitPlatform()` commented in the `test` task block.
- No verified lint/format plugins configured globally (Spotless, Checkstyle, PMD, JaCoCo not found as active standards).
- `order-service` build includes gRPC proto generation; local env plugin executability can fail at `generateProto`.

## 5) Technology Baseline

- Java 17 toolchain
- Spring Boot 3.3.x family
- Spring Cloud 2023.0.x family
- JPA/Hibernate with `jakarta.persistence`
- Kafka for async integration
- JUnit 5 style test setup in enabled modules

## 6) Code Organization Conventions

- Base package shape: `com.example.<service>...`
- Common layers:
  - `controller` (HTTP boundary)
  - `service` (business logic)
  - `repository` (persistence)
  - `domain` (entities)
  - `dto` / `vo` (transport objects)
  - `com.msgqueue` / `com.config` / `com.security` for integration and infra concerns

## 7) Import, Formatting, and Structure

- Use explicit imports; avoid wildcard imports.
- Keep import groups consistent: project -> lombok -> framework/third-party -> JDK.
- Use 4-space indentation in Java source.
- Keep methods short and purpose-focused.
- Keep braces/newlines consistent with existing files.
- Use Javadoc only when it clarifies public behavior or non-obvious intent.

## 8) Naming Conventions

- Controllers: `*Controller`
- Services: `*Service`
- Repositories: `*Repository`
- Entities: usually `*Entity` (some aggregates intentionally use plain names, for example `User`)
- DTOs: `*Dto`
- Request/Response VOs: `Request*`, `Response*`
- Event payloads: `*Event`, `*EventType`
- Topic enum wrappers: `KafkaTopics`
- Boolean-returning methods: prefer `is*`, `has*`, `can*`, `supports*`
- Constants: use `UPPER_SNAKE_CASE` (for example `MAX_RETRY_COUNT`)

## 9) Dependency Injection and Annotations

- Prefer constructor injection with `@RequiredArgsConstructor` and `final` fields.
- Use explicit stereotypes: `@RestController`, `@Service`, `@Configuration`, `@Repository`.
- Use `@Transactional` on write methods, `@Transactional(readOnly = true)` on read methods.
- Use `@Slf4j` + parameterized logging.

## 10) Error Handling and Logging

- Do not swallow exceptions.
- At HTTP boundaries, use meaningful status responses (`ResponseStatusException` or `ResponseEntity`).
- In async/Kafka flows, log enough correlation context (`orderId`, `productId`, `eventType`, reason).
- Catch narrow exception types first; avoid broad catch unless integrating with external boundaries.
- If interruption is caught, restore thread interrupt status.

## 11) Readability and Clean Code Rules

- Prefer clear names over comments.
- Keep one method = one responsibility.
- Keep Cognitive Complexity at 15 or below per method (SonarQube default threshold).
- Use practical method-length targets: Controller ~20 lines, Service/Repository ~30 lines.
- Extract duplicated logic into private helpers when duplication appears twice or more.
- Keep controller logic thin; business rules belong in services.
- Prefer immutable inputs/outputs where practical.
- Use guard clauses and early returns to reduce nesting.
- Extract complex conditional expressions (`&&` / `||`) into well-named helper methods.
- Avoid deep nesting; prefer small focused helpers.
- Avoid hidden side effects; make state transitions explicit.
- Validate inputs near boundaries, fail fast with clear messages.
- Keep event contracts stable; avoid ad-hoc payload changes without coordinated consumer updates.

## 12) Testing Guidance for Agents

- Minimum expectation after edits: module compile/build command succeeds.
- Run tests where enabled; if disabled by build script, report that clearly.
- For async/Kafka behavior, prefer deterministic assertions over sleeps.
- Add focused tests near changed modules; do not add unrelated broad test refactors.

## 13) Agent Execution Checklist

- Identify target module and run commands in that module.
- Verify changed code follows package/layer naming patterns.
- Verify error handling and logs include enough runtime context.
- Run `./gradlew clean build -x test --no-daemon` in affected modules.
- Run `./gradlew test` when the module test task is active.
- Summarize caveats (disabled tests, env-specific build blockers) in final report.

## 14) When Unsure

- Follow the nearest existing pattern in the same module first.
- Prefer behavior-preserving refactors over architectural rewrites.
- Document any module-specific limitations encountered during verification.
