# AGENTS.ko.md

`SPRING_CLOUD_MSA` 저장소에서 작업하는 코딩 에이전트를 위한 한글 가이드입니다.

## 1) 워크스페이스 범위

- 이 저장소는 서비스별 독립 Gradle Wrapper를 사용하는 멀티 모듈 구조입니다.
- 루트에서 전체 모듈을 한 번에 빌드하는 단일 Gradle Wrapper는 없습니다.
- 주요 모듈: `user-service`, `order-service`, `catalog-service`, `cart-service`, `file-service`, `apigateway-service`, `config-service`, `discoveryservice`
- 한 작업에서는 대상 모듈 중심으로 수정하고, 관련 없는 서비스 대규모 리팩터링은 피합니다.

## 2) Cursor / Copilot 규칙 파일

- `.cursorrules`: 없음
- `.cursor/rules/`: 없음
- `.github/copilot-instructions.md`: 없음
- 현재는 `AGENTS.md` / `AGENTS.ko.md`를 기준 규칙으로 사용합니다.

## 3) 빌드/테스트 명령 (검증됨)

기본적으로 대상 모듈 디렉터리에서 실행합니다.

### 공통 Gradle 명령

- 의존성 사전 다운로드 (Docker 캐시 패턴):
  - `./gradlew dependencies --no-daemon`
- 테스트 제외 빌드 (현재 Dockerfile 패턴):
  - `./gradlew clean build -x test --no-daemon`
- 모듈 전체 테스트 실행:
  - `./gradlew test`
- 단일 테스트 클래스 실행:
  - `./gradlew test --tests 'com.example.orderservice.OrderServiceApplicationTests'`
- 단일 테스트 메서드 실행:
  - `./gradlew test --tests 'com.example.orderservice.OrderServiceApplicationTests.contextLoads'`

### 루트에서 모듈별 Wrapper 실행

- `./user-service/gradlew <task>`
- `./order-service/gradlew <task>`
- `./catalog-service/gradlew <task>`
- `./cart-service/gradlew <task>`
- `./file-service/gradlew <task>`
- `./apigateway-service/gradlew <task>`
- `./config-service/gradlew <task>`
- `./discoveryservice/gradlew <task>`

### 로컬 인프라 실행 (루트)

- 전체 실행:
  - `docker-compose up -d`
- 단계별 실행:
  - `docker-compose -f docker-compose-local.yml up -d mariadb redis kafka rabbitmq`
  - `docker-compose -f docker-compose-local.yml up -d config-service discovery-service`
  - `docker-compose -f docker-compose-local.yml up -d apigateway-service`
  - `docker-compose -f docker-compose-local.yml up -d user-service order-service catalog-service cart-service file-service`
  - `docker-compose -f docker-compose-local.yml up -d zipkin prometheus grafana`

## 4) 빌드/테스트 주의사항

- 대부분 모듈에 `jar { enabled = false }`가 설정되어 plain jar를 만들지 않습니다.
- `user-service`, `file-service`는 `tasks.withType(Test) { enabled = false }`로 테스트가 비활성화되어 있습니다.
- `catalog-service/build.gradle`의 `test` 블록은 `useJUnitPlatform()`이 주석 처리되어 있습니다.
- Spotless/Checkstyle/PMD/Jacoco 등 전역 lint/format 표준은 활성 구성으로 확인되지 않았습니다.
- `order-service`는 gRPC proto 생성이 있어 환경에 따라 `generateProto` 단계에서 플러그인 실행 오류가 날 수 있습니다.

## 5) 기술 기준

- Java 17
- Spring Boot 3.3.x 계열
- Spring Cloud 2023.0.x 계열
- JPA/Hibernate (`jakarta.persistence`)
- Kafka 기반 비동기 처리
- 테스트는 JUnit 5 스타일(활성 모듈 기준)

## 6) 코드 구조 규칙

- 기본 패키지 형태: `com.example.<service>...`
- 계층 분리:
  - `controller`: HTTP 진입점
  - `service`: 비즈니스 로직
  - `repository`: 영속성 접근
  - `domain`: 엔티티/도메인 모델
  - `dto` / `vo`: 전송 객체
  - `com.msgqueue` / `com.config` / `com.security`: 인프라/통합 관심사

## 7) import / 포맷 / 코드 형태

- 와일드카드 import를 피하고 명시적 import를 사용합니다.
- import 그룹 순서를 유지합니다: 프로젝트 -> lombok -> 프레임워크/서드파티 -> JDK
- Java 파일은 4-space 들여쓰기 기준을 따릅니다.
- 메서드는 짧고 목적이 분명해야 합니다.
- 중괄호/개행 스타일은 주변 코드와 일관되게 맞춥니다.
- Javadoc은 공개 API 또는 비자명 로직 설명이 필요할 때만 추가합니다.

## 8) 네이밍 규칙

- Controller: `*Controller`
- Service: `*Service`
- Repository: `*Repository`
- Entity: 일반적으로 `*Entity` (예외적으로 `User` 같은 도메인명 단독 사용 존재)
- DTO: `*Dto`
- 요청/응답 VO: `Request*`, `Response*`
- 이벤트 모델: `*Event`, `*EventType`
- Kafka 토픽 enum: `KafkaTopics`

## 9) DI / 어노테이션 규칙

- `@RequiredArgsConstructor` + `final` 필드 기반 생성자 주입을 우선 사용합니다.
- `@RestController`, `@Service`, `@Configuration`, `@Repository`를 명시적으로 사용합니다.
- 쓰기 메서드는 `@Transactional`, 조회 메서드는 `@Transactional(readOnly = true)` 권장.
- 로깅은 `@Slf4j` + 파라미터화 메시지 형식을 사용합니다.

## 10) 예외 처리 / 로깅

- 예외를 무시하거나 삼키지 않습니다.
- HTTP 경계에서는 의미 있는 상태 코드로 응답합니다 (`ResponseStatusException`, `ResponseEntity`).
- Kafka/비동기 흐름에서는 `orderId`, `productId`, `eventType`, 사유를 로그에 남깁니다.
- 가능한 한 좁은 예외 타입부터 처리하고, 광범위 예외는 외부 연동 경계에서만 제한적으로 사용합니다.
- `InterruptedException`을 처리하면 인터럽트 상태를 복원합니다.

## 11) 가독성 / 클린 코드 규칙

- 설명 주석보다 의도가 드러나는 이름을 우선합니다.
- 메서드는 한 가지 책임만 가지게 작성합니다.
- 동일 로직이 2회 이상 반복되면 private helper로 추출을 검토합니다.
- Controller는 얇게 유지하고 비즈니스 규칙은 Service에 둡니다.
- 가능한 범위에서 입력/출력을 불변에 가깝게 유지합니다.
- 숨은 부작용을 피하고 상태 전이는 코드로 명확히 표현합니다.
- 경계(Controller/Consumer)에서 입력 검증 후 빠르게 실패하도록 구성합니다.
- 이벤트 계약(payload/토픽 의미)은 하위 소비자 영향 없이 임의 변경하지 않습니다.

## 12) 테스트 작성 가이드

- 최소 기준: 수정한 모듈에서 컴파일/빌드가 성공해야 합니다.
- 테스트가 활성화된 모듈은 `./gradlew test`까지 실행합니다.
- 비동기/Kafka 테스트는 sleep보다 결정적 검증(상태 확인, 타임아웃 기반 대기)을 우선합니다.
- 변경 범위에 가까운 테스트를 추가하고, 무관한 대규모 테스트 리팩터링은 피합니다.

## 13) 에이전트 실행 체크리스트

- 먼저 대상 모듈을 식별하고 해당 모듈 경로에서 명령을 실행합니다.
- 변경 코드가 패키지/계층/네이밍 규칙을 지키는지 확인합니다.
- 예외 처리와 로그에 운영 추적 정보가 충분한지 확인합니다.
- 대상 모듈에서 `./gradlew clean build -x test --no-daemon` 수행.
- 테스트 활성 모듈은 `./gradlew test` 수행.
- 테스트 비활성/환경 이슈 등 검증 한계를 최종 보고에 명시합니다.

## 14) 애매할 때 원칙

- 같은 모듈의 기존 패턴을 우선적으로 따릅니다.
- 동작 보존 리팩터링을 우선하고, 구조 개편은 요구가 있을 때만 수행합니다.
- 모듈별 제약(테스트 비활성, generateProto 오류 등)을 숨기지 않고 기록합니다.
