# MSA-Spring-Cloud 

> 본 프로젝트는 학습 목적의 토이 프로젝트로 Spring Cloud 기반 마이크로서비스 아키텍처(MSA) 구현 프로젝트입니다.

## 📌 프로젝트 개요

**문제 정의**
- 단일 애플리케이션의 확장성 한계
- 서비스 간 강한 결합으로 인한 배포 및 유지보수 어려움
- 높은 트래픽 상황에서의 성능 및 안정성 보장 필요

**해결 방안**
- **마이크로서비스 아키텍처**: 도메인별 서비스 분리 및 독립적 배포로 확장성과 유지보수성 향상
- **이벤트 기반 비동기 처리**: Kafka를 통한 비동기 이벤트 처리로 응답 시간 단축 및 서비스 간 결합도 감소
- **Circuit Breaker 패턴**: Resilience4J로 장애 격리하여 한 서비스 장애가 전체 시스템으로 전파되는 것 방지
- **분산 추적 및 모니터링**: Zipkin, Prometheus, Grafana를 통한 운영 가시성 확보로 장애 대응 시간 단축

**비즈니스 가치**
- **확장성**: 수평 확장 가능한 아키텍처로 트래픽 증가 시 서비스 인스턴스 추가만으로 대응
- **개발 속도**: 서비스별 독립적 배포로 팀별 병렬 개발 및 배포 가능
- **운영 효율성**: 분산 추적 및 모니터링으로 장애 대응 시간 단축, 중앙화된 설정 관리로 운영 부담 감소
- **비용 최적화**: 필요한 서비스만 스케일링하여 리소스 효율적 사용, 캐싱으로 DB 부하 감소

### 📚 상세 문서

- **프로젝트 개발 노트(notion)**: https://nickel-painter-d6a.notion.site/msa-spring-cloud-190e2100a14b808a9e99c513edfd6a06
<!-- - **gRPC 도입 가이드**: [docs/GRPC_INTRODUCTION_GUIDE.md](docs/GRPC_INTRODUCTION_GUIDE.md) -->

## 🏗️ 아키텍처

### 시스템 아키텍처

```
                         ┌─────────────────┐
                         │   API Gateway   │
                         │  (Spring Cloud  │
                         │     Gateway)    │
                         └────┬──────┬─────┘
                              │      │
              ┌───────────────┼──────┼───────────────┐
              │               │      │               │
         ┌────▼────┐    ┌────▼────┐  │         ┌────▼────┐
         │  User   │    │  Order  │  │         │ Catalog │
         │Service  │    │Service  │  │         │ Service │
         └────┬────┘    └────┬────┘  │         └────┬────┘
              │              │       │              │
         ┌────▼────┐    ┌────▼────┐  │         ┌────▼────┐
         │Cart     │    │  Kafka  │  │         │ File    │
         │Service  │    │(Message │  │         │Service  │
         └────┬────┘    │  Queue) │  │         └─────────┘
              │         └────┬────┘  │
              │              │       │
         ┌────▼────┐         │       │         ┌─────────┐
         │ Redis   │         │       │         │ MariaDB │
         │(Cache)  │         │       │         │         │
         └─────────┘         │       │         └────┬────┘
                             │       │              │
                ┌────────────┼───────┼──────────────┼
                │            │       │              │            
           ┌────▼────┐   ┌───▼────┐  │       ┌──────▼────┐
           │  User   │   │ Order  │  │       │ Catalog   │
           │   DB    │   │   DB   │  │       │   DB      │
           └─────────┘   └────────┘  │       └───────────┘
                                     │
         ┌───────────────────────────┴──────────────────────────┐
         │         Eureka (Service Discovery)                   │
         │  (모든 서비스 등록 및 서비스 발견)                     │
         └──────────────────────────────────────────────────────┘
```


**연결 관계:**
- **API Gateway** → 모든 비즈니스 서비스 라우팅, Redis Rate Limiting
- **User Service** → MariaDB (user_db)
- **Order Service** → MariaDB (order_db) + Kafka (Producer: 주문 이벤트 발행)
- **Catalog Service** → MariaDB (catalog_db) + Kafka (Consumer: 재고 업데이트 이벤트 구독)
- **Cart Service** → Redis (장바구니 데이터 저장)
- **File Service** → 독립 서비스 (RabbitMQ 사용)
- **모든 서비스** → Eureka (Service Discovery: User, Order, Catalog, Cart, File, API Gateway)

## ✨ 핵심 기능

### 1. 장애 격리 및 회복력 (Resilience)

#### Circuit Breaker (Resilience4J)
- **설계 의도**: 서비스 장애 시 자동으로 요청을 차단하여 장애 전파 방지
- **구현 내용**:
  - 실패율 50% 초과 시 서킷 오픈
  - 느린 호출(5초 이상) 30% 초과 시 서킷 오픈
  - 60초 후 HALF-OPEN 상태로 전환하여 자동 복구 시도
- **실무 적용 장점**:
  - 한 서비스 장애가 전체 시스템으로 전파되는 것을 방지
  - 불필요한 재시도로 인한 리소스 낭비 방지
  - Fallback 메커니즘으로 사용자 경험 보장

#### Timeout & Fallback
- **설계 의도**: 외부 서비스 응답 지연 시 무한 대기 방지 및 기본 응답 제공
- **구현 내용**: 외부 서비스 호출 타임아웃 6초 설정, 장애 시 기본값 반환
- **실무 적용 장점**: 느린 응답으로 인한 전체 시스템 지연 방지, 사용자 대기 시간 최소화

### 2. 성능 최적화

#### 비동기 이벤트 처리 (Kafka)
- **설계 의도**: 동기적 서비스 호출을 비동기 이벤트로 전환하여 응답 시간 단축
- **구현 내용**:
  - 주문 생성 시 재고 업데이트를 비동기로 처리
  - 이벤트 소싱 패턴으로 데이터 일관성 보장
- **실무 적용 장점**:
  - 응답 시간 50-70% 단축 (동기 호출 제거)
  - 서비스 간 결합도 감소로 독립적 배포 가능
  - 트래픽 급증 시 이벤트 큐 버퍼링으로 시스템 안정성 확보

#### 캐싱 (Redis)
- **설계 의도**: 자주 조회되는 데이터를 메모리에 저장하여 DB 부하 감소
- **구현 내용**: 세션 관리, 장바구니 데이터 캐싱
- **실무 적용 장점**:
  - DB 쿼리 수 감소로 응답 시간 단축
  - DB 부하 감소로 인프라 비용 절감

#### Rate Limiting
- **설계 의도**: API 요청 빈도 제한으로 DDoS 공격 방지 및 서버 리소스 보호
- **구현 내용**: Redis 기반 토큰 버킷 알고리즘으로 API 요청 제한
- **실무 적용 장점**: 악의적 공격 방지, 예상치 못한 비용 증가 방지

### 3. 보안

#### JWT 인증
- **설계 의도**: 무상태(Stateless) 인증으로 서버 확장성 확보
- **구현 내용**: 토큰 기반 인증, JWT 토큰에 사용자 정보 및 역할 정보 포함
- **실무 적용 장점**:
  - 세션 저장소 불필요로 서버 확장 용이
  - 토큰에 권한 정보 포함으로 매 요청 시 권한 검증 가능
  - 마이크로서비스 환경에서 서비스 간 인증 정보 공유 용이
  - 향후 개선: Refresh Token 도입으로 보안 강화 및 사용자 편의성 향상 계획

#### RBAC (Spring Security)
- **설계 의도**: 역할 기반 접근 제어로 세밀한 권한 관리
- **구현 내용**: `@PreAuthorize` 어노테이션을 활용한 메서드 레벨 보안
- **실무 적용 장점**:
  - API별 세밀한 접근 제어로 보안 강화
  - 역할 변경 시 코드 수정 없이 어노테이션만 변경 가능
  - 감사(Audit) 추적 용이

#### API Gateway 필터링
- **설계 의도**: 중앙에서 인증/인가 처리로 보안 정책 일관성 유지
- **구현 내용**: 인증 토큰 검증 및 헤더 처리
- **실무 적용 장점**: 각 서비스에서 인증 로직 중복 구현 불필요, 보안 정책 변경 시 한 곳만 수정

### 4. 분산 시스템 관리

#### Service Discovery (Eureka)
- **설계 의도**: 동적 서비스 등록/발견으로 하드코딩된 엔드포인트 의존성 제거
- **구현 내용**: Eureka를 통한 자동 서비스 등록 및 헬스 체크
- **실무 적용 장점**:
  - 서비스 인스턴스 추가/제거 시 자동 로드밸런싱
  - 수평 확장 시 무중단 서비스 추가
  - 장애 인스턴스 자동 제거로 가용성 향상

#### Config Management
- **설계 의도**: 중앙화된 설정 관리로 환경별 설정 분리 및 동적 갱신
- **구현 내용**: Git 기반 설정 관리, Spring Cloud Bus를 통한 동적 갱신
- **실무 적용 장점**:
  - 설정 변경 시 재배포 없이 동적 갱신 가능
  - 환경별(dev, staging, prod) 설정 분리로 관리 용이
  - 설정 변경 이력 추적 가능

#### Distributed Tracing (Zipkin)
- **설계 의도**: 분산 환경에서 요청 추적 및 성능 병목 지점 파악
- **구현 내용**: Zipkin을 통한 분산 추적, Trace ID 기반 요청 추적
- **실무 적용 장점**:
  - 서비스 간 호출 체인 전체 추적 가능
  - 성능 병목 지점 빠른 파악
  - 장애 발생 시 원인 분석 시간 단축

### 5. CI/CD 파이프라인

#### 자동화된 빌드 및 배포
- **설계 의도**: 수동 배포 과정 자동화로 배포 시간 단축 및 인적 오류 방지
- **구현 내용**: Jenkins를 통한 자동 빌드, Docker 이미지 생성, 자동 배포
- **실무 적용 장점**:
  - 배포 시간 80-90% 단축
  - 일관된 배포 프로세스로 인적 오류 방지
  - 빠른 피드백으로 개발 생산성 향상
  - 롤백 자동화로 장애 대응 시간 단축

### 🛠️ 기술 스택

#### Backend
- **Framework**: Spring Boot 3.3.x, Spring Cloud 2023.0.x
- **Language**: Java 17
- **Build Tool**: Gradle
- **API Communication**: OpenFeign (REST)
- **Database**: MariaDB
- **Cache**: Redis
- **Message Queue**: Apache Kafka
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway

#### Infrastructure & DevOps
- **Containerization**: Docker, Docker Compose
- **CI/CD**: Jenkins, 자동화된 빌드 및 배포 파이프라인

#### Monitoring & Observability
- **Distributed Tracing**: Zipkin
- **Metrics**: Prometheus
- **Visualization**: Grafana
- **Logging**: Structured Logging with Micrometer

#### Security
- **Authentication**: JWT
- **Authorization**: Spring Security (RBAC)
- **API Security**: API Gateway 필터링

### 📁 서비스 구성

| 서비스 | 포트 | 설명 | 주요 기능 |
|--------|------|------|----------|
| **Config Service** | 8888 | 중앙화된 설정 관리 | Git 기반 설정 관리, 프로파일별 설정 분리 |
| **Discovery Service** | 8761 | 서비스 디스커버리 (Eureka) | 서비스 등록/발견, 헬스 체크 |
| **API Gateway** | 8000 | API 게이트웨이 | 라우팅, 인증, Rate Limiting, 로깅 |
| **User Service** | 랜덤 | 사용자 관리 서비스 | 회원가입, 로그인, JWT 발급, 사용자 정보 관리 |
| **Order Service** | 랜덤 | 주문 관리 서비스 | 주문 생성, 주문 조회, Kafka 이벤트 발행 |
| **Catalog Service** | 랜덤 | 상품 카탈로그 서비스 | 상품 조회, 재고 관리, Kafka 이벤트 구독 |
| **Cart Service** | 랜덤 | 장바구니 서비스 | 장바구니 CRUD, Redis 캐싱 |
| **File Service** | 랜덤 | 파일 관리 서비스 | 파일 업로드/다운로드, WebDAV 연동 |

### 🛠️ 인프라 서비스

| 서비스 | 포트        | 설명 | 용도 |
|--------|-----------|------|------|
| **MariaDB** | 3307      | 메인 데이터베이스 | 영구 데이터 저장 |
| **Redis** | 6379      | 캐시 저장소 | 세션 관리, Rate Limiting, 캐싱 |
| **Kafka** | 9092/9094 | 메시지 큐 | 비동기 이벤트 처리, 이벤트 소싱 |
| **Zipkin** | 9411      | 분산 추적 | 서비스 간 호출 추적, 성능 분석 |
| **Prometheus** | 9090      | 메트릭 수집 | 시스템 메트릭 수집 및 저장 |
| **Grafana** | 3001      | 모니터링 대시보드 | 메트릭 시각화 및 알림 |

## 🚀 빠른 시작

### 1. 사전 요구사항

- Docker & Docker Compose
- Java 17+
- Gradle

### 2. 서비스 시작

#### 전체 서비스 시작
```bash
docker-compose up -d
```

#### 단계별 시작
```bash
# 1. 인프라 서비스 시작
docker-compose -f docker-compose-local.yml up -d mariadb redis kafka rabbitmq

# 2. 설정 및 디스커버리 서비스 시작
docker-compose -f docker-compose-local.yml up -d config-service discovery-service

# 3. API Gateway 시작
docker-compose -f docker-compose-local.yml up -d apigateway-service

# 4. 비즈니스 서비스 시작
docker-compose -f docker-compose-local.yml up -d user-service order-service catalog-service cart-service file-service

# 5. 모니터링 서비스 시작
docker-compose -f docker-compose-local.yml up -d zipkin prometheus grafana
```

## 🔗 서비스 접속 정보

### 주요 서비스 URL
- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8000
- **Config Service**: http://localhost:8888
- **Zipkin**: http://localhost:9411
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3001 (기본 로그인: admin/admin)
- **RabbitMQ Management**: http://localhost:15672 (기본 로그인: guest/guest)

### API 엔드포인트
- **User Service**: http://localhost:8000/user-service/**
- **Order Service**: http://localhost:8000/order-service/**
- **Catalog Service**: http://localhost:8000/catalog-service/**
- **Cart Service**: http://localhost:8000/cart-service/**
- **File Service**: http://localhost:8000/file-service/**

## 📊 모니터링 및 관찰성

### Prometheus 메트릭
- 각 서비스의 `/actuator/prometheus` 엔드포인트에서 메트릭 수집
- HTTP 요청 수, 응답 시간, 에러율 등 실시간 메트릭 수집
- Prometheus UI에서 실시간 메트릭 확인 가능

### Grafana 대시보드
- 기본 로그인: admin/admin
- Prometheus 데이터 소스로 대시보드 구성
- CPU, 메모리, 네트워크, API 응답 시간 등 시각화

### Zipkin 분산 추적
- 분산 추적을 통한 서비스 간 호출 추적
- 요청별 Trace ID로 전체 호출 체인 추적
- 성능 병목 지점 분석 및 최적화

### Micrometer
- Spring Boot Actuator와 연동
- 커스텀 메트릭 수집 가능
- 비즈니스 메트릭 모니터링


## 🐳 배포 및 인프라

### Docker
- 각 서비스를 독립적인 Docker 컨테이너로 구성
- Docker Compose를 통한 로컬 개발 환경 구성
- 멀티 스테이지 빌드로 이미지 크기 최적화

### 환경 분리
- **dev**: 로컬 개발 환경
- **prod**: 프로덕션 환경
- 프로파일별 설정 분리

## 🎓 학습 내용 및 트러블슈팅

### 주요 학습 내용
1. **MSA 아키텍처 설계**: 서비스 분리 기준 및 경계 설계
2. **이벤트 기반 아키텍처**: Kafka를 통한 비동기 처리 패턴
3. **분산 트랜잭션**: Saga 패턴 고려 및 이벤트 기반 처리 방식

### 트러블슈팅 경험
- **서비스 간 통신 지연**: OpenFeign 타임아웃 설정 및 Circuit Breaker 적용
- **이벤트 순서 보장**: Kafka 파티셔닝 전략 수립

## 📈 향후 개선 계획

### 단기 (1-2개월)
- [ ] gRPC 도입으로 서비스 간 통신 성능 개선
- [ ] API 문서화 (Swagger/OpenAPI)

### 중기 (3-6개월)
- [ ] 로깅 중앙화 (ELK Stack)
- [ ] 성능 테스트 및 부하 테스트
- [ ] CQRS 패턴 도입 검토 (읽기/쓰기 모델 분리)

### 장기 (6개월 이상)
- [ ] 서비스 메시 도입 검토 (Istio)
- [ ] 자동 스케일링 구현

## 📚 참고 자료

- [Spring Cloud 공식 문서](https://spring.io/projects/spring-cloud)
- [Resilience4J 문서](https://resilience4j.readme.io/)
- [Kafka 공식 문서](https://kafka.apache.org/documentation/)
<!-- - [gRPC 도입 가이드](docs/GRPC_INTRODUCTION_GUIDE.md) -->

## 👥 기여

이슈 및 개선 제안은 언제든 환영합니다!

---