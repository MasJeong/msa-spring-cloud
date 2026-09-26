# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 프로젝트 목적

Spring Cloud MSA, Kafka, Redis를 학습하려고 만든 커머스 프로젝트다. 이후 AWS에 실제로 배포해 사용자 수를 가정한 부하 테스트까지 진행할 예정이다. 변경을 설계할 때 로컬 동작뿐 아니라 다중 인스턴스·부하 상황(멱등성, 블로킹 호출, 로그량, rate limit)에서의 동작도 함께 고려한다.

- 기술 기준: Java 17, Spring Boot 3.3.x, Spring Cloud 2023.0.x (패치 버전은 모듈마다 다름)
- 모듈: `user-service`, `order-service`, `catalog-service`, `cart-service`, `file-service`, `apigateway-service`, `config-service`, `discoveryservice`
- 작업 범위: 대상 모듈을 먼저 정하고 그 모듈 안에서 수정한다. 관련 없는 서비스는 함께 리팩터링하지 않는다. 애매하면 같은 모듈의 기존 패턴을 따르고, 구조 개편보다 동작 보존 리팩터링을 우선한다.

## 명령어

모듈마다 독립 Gradle wrapper가 있고 루트 빌드는 없다. `gradlew`가 실행 권한 없이(`100644`) 커밋되어 있어 `./gradlew`는 `permission denied`가 난다 → 모듈 디렉토리에서 `sh gradlew`로 실행한다.

```bash
cd order-service
sh gradlew clean build -x test --no-daemon    # 기본 검증 (Dockerfile과 같은 명령)
sh gradlew test
sh gradlew test --tests 'com.example.orderservice.OrderServiceApplicationTests'
sh gradlew test --tests 'com.example.orderservice.OrderServiceApplicationTests.contextLoads'
```

- lint/format 도구(Spotless, Checkstyle, PMD, JaCoCo)는 없다.
- `jar { enabled = false }` → `build/libs`에는 bootJar 하나만 생긴다(Dockerfile이 `build/libs/*.jar`에 의존).
- order-service와 user-service는 빌드할 때 `generateProto`로 gRPC 코드를 생성한다.

### 로컬 인프라 (루트, `-f` 필수 — 루트에 `docker-compose.yml` 없음)

```bash
docker-compose -f docker-compose-local.yml up -d                  # 전체
# 단계별
docker-compose -f docker-compose-local.yml up -d mariadb redis kafka rabbitmq
docker-compose -f docker-compose-local.yml up -d config-service discovery-service
docker-compose -f docker-compose-local.yml up -d apigateway-service
docker-compose -f docker-compose-local.yml up -d user-service order-service catalog-service cart-service file-service
docker-compose -f docker-compose-local.yml up -d zipkin prometheus grafana
```

- `docker-compose-local.yml`은 dev 프로필이고 이미지 `masjeong/<svc>:1.0`을 쓴다. 이미지는 모듈 디렉토리에서 `docker build -t masjeong/<svc>:1.0 .`로 빌드한다(`discoveryservice` 모듈 → 이미지/컨테이너명 `discovery-service`).
- `docker-compose-prod.yml`은 Jenkins 배포용 prod 프로필이다. `*_SERVICE_VERSION`, `DB_ROOT_PASSWORD`, `RABBITMQ_USER/PASS`, `KEYSTORE_PASSWORD` env가 필요하고, 현재 order/catalog/cart/file은 주석 처리되어 있다.
- config-service 컨테이너에는 `KEYSTORE_PASSWORD` env와 `./keystore/`(gitignore, 로컬에만 있음) 마운트가 필요하다.

### 테스트 현황

- `user-service`: 기본값은 `test` SKIPPED이고, `-PwithTests`를 줘야 켜진다. 예: `sh gradlew test -PwithTests --tests 'com.example.userservice.api.user.controller.UserControllerHealthCheckTest'`
- `file-service`: `tasks.withType(Test) { enabled = false }`라서 `test`는 항상 SKIPPED다.
- `catalog-service`: `useJUnitPlatform()`이 주석 처리되어 JUnit5 테스트가 발견되지 않는다.
- 실제 테스트는 user-service(Repository/Service/JWT, `UserControllerHealthCheckTest`)와 order-service `OrderControllerValidationTest`에 있고, 나머지는 `contextLoads`뿐이다.
- 인프라 없이 도는 컨트롤러 테스트는 `OrderControllerValidationTest`, `UserControllerHealthCheckTest`처럼 `MockMvcBuilders.standaloneSetup` + Mockito mock(+ `MockEnvironment`)으로 만든다.
- JPA 모듈의 `contextLoads`는 default 프로필로 뜨기 때문에 `127.0.0.1:3306`에 MariaDB가 없으면 Hibernate dialect를 정하지 못해 FAILED가 된다(order-service에서 확인). compose의 MariaDB는 3307번이라 이 조건을 채우지 못한다. user-service의 Repository/Service 테스트가 쓰는 `test` 프로필은 `127.0.0.1:3306` MariaDB, `{cipher}` 값, Windows 경로 keystore(`src/test/resources/bootstrap.yml`)에 의존한다.

### 작업 후 검증

- 수정한 모듈에서 `sh gradlew clean build -x test --no-daemon`을 돌리고, 테스트가 활성화된 모듈이면 `sh gradlew test`도 돌린다.
- 테스트 비활성화, 환경 문제 같은 검증 한계는 최종 보고에 명시한다.

## 아키텍처

### 프로필과 설정 소스

각 모듈에 `application.yml` / `application-dev.yml` / `application-prod.yml`이 있고 내용이 대부분 중복된다. 설정을 바꿀 때는 세 파일을 함께 확인한다.

| 프로필 | 용도 | 인프라 주소 |
|---|---|---|
| (default) | IDE 로컬 실행 | `127.0.0.1` 하드코딩, `server.port: 0`(랜덤) |
| `dev` | `docker-compose-local.yml` | env(`DB_HOST`, `KAFKA_BOOTSTRAP_SERVERS`, `SERVER_PORT`, `REDIS_HOST`, `RABBITMQ_*`) + compose 서비스명 |
| `prod` | `docker-compose-prod.yml` | dev와 같은 구조 + 시크릿 env |

- Config Server(`config-service`, :8888)의 백엔드는 Git 저장소 `github.com/MasJeong/spring-cloud-config`다. 루트의 `git-local-repo/`는 그 로컬 사본(gitignore)이다. `{cipher}...` 값은 `config-service/src/main/resources/apiEncryptionKey.jks`로 복호화된다.
- Config 클라이언트는 `user-service`(name `user-service`), `apigateway-service`(name `ecommerce`), `file-service` 세 개뿐이다. order/catalog/cart는 config client 의존성이 없어 모듈 내부 yml만 쓴다.
- JWT `token.secret`과 `token.expiration-time`은 Config 저장소에만 있다. user-service(발급)와 gateway(검증)가 같은 값을 받아야 하므로, 인증이 동작하려면 config-service가 떠 있어야 한다.
- 설정 변경은 RabbitMQ 기반 Spring Cloud Bus로 전파한다: `POST /actuator/busrefresh` (config/gateway/user/file).

### 요청 경로

- Client → Gateway(:8000) → `lb://<SERVICE>` (Eureka :8761). 라우트는 gateway yml 3종에 중복 정의되어 있다. gateway의 config name이 `ecommerce`라서 Config 저장소의 `apigateway-prod.yml`은 로드되지 않는다.
- 경로 규칙이 서비스마다 다르다.
  - user-service: gateway가 `RewritePath`로 `/user-service` 접두어를 제거한다 → 컨트롤러는 `/users`, `/login`.
  - order/catalog/cart: 컨트롤러 `@RequestMapping`에 서비스명 접두어가 들어 있고(`/order-service/...`, `/catalog-service/catalogs`, `/cart-service/api/v1/carts`), gateway는 경로를 바꾸지 않는다.
  - cart-service와 file-service(`/api/v1/files`)에는 gateway 라우트가 없다. README 다이어그램과 달리 게이트웨이로는 접근할 수 없다.
- 인증: `POST /user-service/login` → user-service `CustomAuthenticationFilter`가 JWT를 발급한다. gateway `AuthorizationHeaderFilter`는 user GET 라우트와 order 라우트에서 JWT를 검증하고 `X-USER-ID` 헤더를 붙인다. user-service는 `CustomJwtValidationFilter`와 `@PreAuthorize`로 한 번 더 검증한다.
- Rate limit: `/user-service/**` GET에 Redis `RequestRateLimiter`가 걸려 있다. 사용자(`X-USER-ID`)당 10 req/s, burst 10(`RateLimiterConfig` 상수).

### 서비스 간 통신

**user → order (gRPC, 동기 조회)**
- order-service가 gRPC 서버다(포트 9091). 이 포트는 default `application.yml`에만 정의되어 있고 dev/prod가 상속한다. user-service는 `discovery:///order-service`로 Eureka를 거쳐 접속한다.
- `order.proto`가 `order-service/src/main/proto`와 `user-service/src/main/proto`에 복제되어 있다. 계약을 바꿀 때는 둘 다 수정한다.
- `UserController.getUser`가 이 호출을 Resilience4J 서킷브레이커 `cb-userToOrder-grpc`로 감싸고, 실패하면 빈 주문 목록을 반환한다(임계값은 `Resilience4JConfig`). `OrderServiceClient`(Feign)는 gRPC 전환 후 비활성화된 레거시다.

**order ↔ catalog (Kafka 코레오그래피 사가)**
1. `POST /order-service/{userId}/orders` → 주문 저장(order_db) → `example-catalog-topic`에 `StockEvent(CATALOG_STOCK_DECREASE)`를 동기로 발행(`.get(3s)`). 발행에 실패하면 주문을 삭제하고 503을 반환한다.
2. catalog `KafkaConsumer`(group `consumer_group01`) → `CatalogService.decreaseStock`(dirty checking, 명시적 save 없음) → `example-catalog-topic-result`에 `StockResultEvent`를 발행한다.
3. order `KafkaConsumer`(group `order-service-group`) → `success=false`이면 `cancelOrder`로 주문을 **하드 삭제**한다(주문 상태 컬럼 없음).

- 토픽명(`com/enums/KafkaTopics`)과 이벤트 DTO(`StockEvent`, `StockResultEvent`, `StockEventType`)는 공유 라이브러리 없이 **서비스별로 복제**되어 있다. 계약(payload·토픽 의미)을 바꿀 때는 양쪽을 동시에 맞춘다.
- 메시지는 key 없는 JSON 문자열(StringSerializer + ObjectMapper)이다. 멱등 처리와 DLQ는 없고, 토픽은 브로커가 자동 생성한다.
- `OrderProducer`(Kafka Connect sink 포맷, `orders` 토픽)는 쓰지 않는 레거시다.

### 데이터

- MariaDB 단일 인스턴스에 서비스별 DB를 둔다: `user_db`, `order_db`, `catalog_db`. 모든 모듈이 `ddl-auto: none`이라 스키마는 `mariadb-docker/all_databases.sql`(gitignore) 덤프를 `masjeong/mariadb:1.0` 이미지에 넣는 방식으로 관리된다. 엔티티를 바꾸면 DDL을 따로 반영해야 한다.
- user-service는 QueryDSL을 쓴다(`*DslRepository` + `*DslRepositoryImpl`).
- Redis: gateway rate limiter, cart-service 장바구니(`cart:{userId}` Hash). cart-service에는 DB가 없다.
- file-service는 Sardine으로 외부 WebDAV에 저장한다(`webdav.base-url`에 LAN IP가 하드코딩됨).

### 관측성

- Zipkin 트레이싱은 user/order에만 있다(B3, sampling 1.0). Prometheus registry 의존성은 user/order/apigateway에만 있다.
- `prometheus-3.2.1/prometheus.yml`(gitignore)은 각 서비스를 gateway 경로 `/<svc>/actuator/prometheus`로 스크랩한다.

### 포트 (docker-compose-local)

| 구분 | 포트 |
|---|---|
| 플랫폼 | gateway 8000, config 8888, eureka 8761 |
| 서비스(컨테이너 내부) | catalog 8081, user 8082, order 8083(gRPC 9091), cart 8084, file 8085 — 호스트에는 `0:808x` 랜덤 포트로 publish |
| 인프라 | MariaDB 호스트 3307, Redis 6379, Kafka 9092(internal)/9094(external), RabbitMQ 5672/15672, Zipkin 9411, Prometheus 9090, Grafana 3001 |

## 코드 규칙

### 구조·네이밍

- 패키지: `com.example.<service>...`. 계층은 `controller` / `service` / `repository` / `domain` / `dto`·`vo`, 인프라 관심사는 `com.msgqueue` / `com.config` / `com.security`.
- 이름: `*Controller`, `*Service`, `*Repository`, 엔티티 `*Entity`(예외적으로 `User` 같은 도메인명), `*Dto`, 요청/응답 VO `Request*`/`Response*`, 이벤트 `*Event`/`*EventType`, 토픽 enum `KafkaTopics`.
- boolean 반환 메서드는 `is*`/`has*`/`can*`/`supports*`, 상수는 `UPPER_SNAKE_CASE`.

### 스타일·DI

- 명시적 import만 쓴다(와일드카드 금지). 순서: 프로젝트 → lombok → 프레임워크/서드파티 → JDK. 들여쓰기는 4-space. 기존 파일 29개에 와일드카드 import가 남아 있는데, 그 파일을 수정할 때만 정리하고 일괄 정리는 하지 않는다.
- 생성자 주입은 `@RequiredArgsConstructor` + `final` 필드로 한다. 스테레오타입은 `@RestController`/`@Service`/`@Configuration`/`@Repository`를 명시하고, Kafka producer/consumer는 `@Component`를 쓴다.
- 쓰기 메서드는 `@Transactional`, 조회 메서드는 `@Transactional(readOnly = true)`.
- 로깅은 `@Slf4j` + 파라미터화 메시지. Javadoc은 공개 동작이나 비자명한 의도를 설명할 때만 단다.

### 복잡도·가독성 (SonarQube 기준)

- 메서드당 Cognitive Complexity 15 이하. 메서드 길이는 Controller 20줄, Service/Repository 30줄 안팎.
- 가드 절과 early return으로 중첩을 줄이고, `&&`/`||`가 섞인 복합 조건은 이름 있는 메서드로 추출한다.
- 같은 로직이 두 번 이상 나오면 private helper로 추출한다.
- Controller는 얇게 두고 비즈니스 규칙은 Service에 둔다. 상태 전이는 숨은 부작용 없이 코드에 드러나게 쓴다.

### 예외·로깅

- 예외를 삼키지 않는다. HTTP 경계에서는 `ResponseStatusException`이나 `ResponseEntity`로 의미 있는 상태 코드를 돌려준다.
- 좁은 예외 타입부터 catch하고, 광범위 catch는 외부 연동 경계에서만 쓴다. `InterruptedException`을 잡으면 인터럽트 상태를 복원한다.
- Kafka·비동기 흐름의 로그에는 `orderId`, `productId`, `eventType`, 실패 사유를 남긴다.

### API

- 새 엔드포인트는 모듈의 경로 규칙(위 "요청 경로")을 따르고, gateway yml 3종에 라우트를 같이 추가한다.
- 요청 VO에 Bean Validation을 달고 컨트롤러에서 `@Valid`로 검증한다. 숫자 필드에는 `@Min`/`@Max`/`@Positive`를 쓴다(`@Size`는 문자열·컬렉션 전용이라 숫자에 붙이면 `UnexpectedTypeException`이 난다).
- 응답으로 Entity를 그대로 내보내지 않는다. ModelMapper로 `Response*` VO로 변환한다.
- `@ControllerAdvice`가 없어서 예외가 나면 500이 된다. 조회 실패는 bare `orElseThrow()` 대신 `ResponseStatusException(HttpStatus.NOT_FOUND, ...)`로 던진다.

### Kafka·이벤트

- 컨슈머는 같은 메시지를 두 번 이상 받을 수 있다고 보고(at-least-once) 멱등하게 만든다. 같은 `orderId`를 다시 처리해도 결과가 같아야 한다.
- 새 이벤트는 집계 ID(`orderId` 등)를 key로 발행해서 파티션 안에서 순서를 보장한다.
- 이벤트 스키마는 필드 추가만 하위 호환으로 본다. 필드 이름 변경이나 삭제는 producer와 consumer를 함께 바꿔서 같이 배포한다.
- 처리할 수 없는 메시지(파싱 실패, 필수 필드 누락)도 그냥 버리지 않는다. 실패 결과 이벤트를 발행하거나, 최소한 correlation 필드를 담아 error 로그를 남긴다.
- Kafka 발행은 DB 커밋 이후에 한다. 트랜잭션 안에서 발행하지 않는다. 현재 패턴은 Service 커밋 → Controller에서 발행.

### DB·JPA

- `ddl-auto: none`이고 마이그레이션 도구가 없다. 엔티티나 컬럼을 바꾸면 적용할 DDL(`ALTER TABLE ...`)을 같이 작성해서 제시한다.
- 여러 요청이 동시에 바꾸는 수치(재고 등)는 `@Version`, `@Lock`, 조건부 `UPDATE ... WHERE stock >= :qty` 중 하나로 보호한다.
- user-service 조회는 `@QueryProjection` DTO + `select(new QXxxDto(...))`로 필요한 컬럼만 가져오고, 연관 컬렉션은 별도 쿼리로 가져온다(N+1 방지). 새 조회도 이 패턴을 따른다.

### 서비스 간 동기 호출

- 새 동기 호출(gRPC, HTTP)은 `UserController.getUser`처럼 Resilience4J 서킷브레이커 + 타임아웃 + fallback으로 감싼다. fallback은 부가 데이터일 때만 빈 값으로 두고, 핵심 데이터면 명시적인 오류로 돌려준다.

### 설정·시크릿

- 새 설정 키는 프로필 yml 3종에 모두 넣는다. 환경마다 다른 값은 `${ENV_VAR}`, 시크릿은 env나 `{cipher}`로 넣고 yml에 평문으로 쓰지 않는다.
- 토큰, 비밀번호, 시크릿은 로그나 응답에 넣지 않는다. 값이 들어왔는지만 알려야 할 때는 `/users/health-check`의 `token secret configured=true`처럼 설정 여부만 보여준다.

### 테스트 작성

- 기존 패턴: Service 테스트는 `@SpringBootTest` + `@Transactional` + `ServiceTestSupport`, Repository 테스트는 `@DataJpaTest` + `@AutoConfigureTestDatabase(replace = NONE)` + `RepositoryTestSupport`. `@DisplayName`은 한국어로 쓰고, 본문은 Given/When/Then 주석으로 나누며, 검증은 JUnit `Assertions`로 한다.
- Kafka·비동기 검증은 sleep 대신 결정적 검증(상태 확인, 타임아웃 기반 대기)으로 한다.
- 변경한 모듈 근처에 집중된 테스트를 추가하고, 무관한 대규모 테스트 리팩터링은 하지 않는다.

### Git 커밋

- 형식: `<type>: <한국어 요약>`. type은 `feat`, `fix`, `refactor`, `perf`, `docs`, `style`, `chore`, `remove` 중에서 쓴다(git 기록 기준). 예: `fix: Circuit Breaker가 gRPC 호출 실패를 인식하도록 수정`

## 로컬 실행 함정

- compose가 넘기는 `CONFIG_URI`/`EUREKA_URI` env는 코드 어디서도 읽지 않는다. Config 클라이언트(gateway/user/file)의 `bootstrap.yml`은 모든 프로필에서 `http://127.0.0.1:8888`로 고정되어 있어, 컨테이너 안에서는 config-service에 닿지 않는다(`token.secret` 미주입). Eureka 주소는 프로필별 yml에 하드코딩되어 있다.
- default 프로필은 MariaDB `127.0.0.1:3306`을 보지만, compose의 MariaDB는 호스트 `3307`로 노출된다.
- Kafka EXTERNAL 리스너가 `kafka:9094`로 advertise되므로, IDE에서 default 프로필(`127.0.0.1:9094`)로 붙으려면 `/etc/hosts`에 `127.0.0.1 kafka`가 있어야 한다.
- RabbitMQ 계정: default 프로필은 `admin/admin`(로컬 설치 가정), compose는 `guest/guest`.
- `k8s/`는 2025-04의 초기 실험물이라 현재 yml과 env 이름이 맞지 않는다(예: `BOOTSTRAP-SERVERS`, 임시 이미지 태그).

## 부하 테스트·AWS 배포 전 확인할 현재 설정

- JPA 모듈(user/order/catalog/file)은 prod를 포함한 모든 프로필에서 `org.hibernate.SQL: DEBUG`, `org.hibernate.orm.jdbc.bind: TRACE`다. user/order의 트레이싱 sampling은 `1.0`이다.
- 주문 API는 Kafka 발행 때문에 HTTP 스레드를 최대 3초 블로킹한다.
- `spring.jpa.open-in-view`가 설정되지 않아 기본값(true)이다. 그래서 `UserController.getUser`는 gRPC 호출(최대 6초) 동안에도 DB 커넥션을 쥐고 있고, 부하가 걸리면 커넥션 풀이 고갈될 수 있다.
- 재고 차감(`CatalogService.decreaseStock`)은 락도 `@Version`도 없이 읽고 나서 쓴다. 지금은 파티션이 1개라 순차 처리되지만, 파티션이나 인스턴스를 늘리면 lost update가 생긴다.
- Kafka는 단일 브로커, replication factor 1이고 토픽이 자동 생성(기본 파티션 1개)된다. 따라서 컨슈머 병렬성이 파티션 수에 묶인다.
- gateway rate limit(사용자당 10 req/s)이 부하 스크립트에서 429를 일으킨다.
- DB 계정과 WebDAV 자격증명이 yml에 평문으로 있고, 암호화 keystore(`apiEncryptionKey.jks`)가 git에 커밋되어 있다. 배포할 때는 외부 시크릿 저장소로 분리해야 한다.
