# MSA-Spring-Cloud 

> Spring Cloud 기반 마이크로서비스 아키텍처(MSA) 구현 프로젝트  
> 초당 500+ 요청 처리 및 확장 가능한 이커머스 플랫폼

### 📌 프로젝트 개요

**문제 정의**
- 단일 애플리케이션의 확장성 한계
- 서비스 간 강한 결합으로 인한 배포 및 유지보수 어려움
- 높은 트래픽 상황에서의 성능 및 안정성 보장 필요

**해결 방안**
- 마이크로서비스 아키텍처로 서비스 분리 및 독립적 배포
- 이벤트 기반 비동기 처리로 응답 시간 최적화
- Circuit Breaker 패턴으로 장애 격리 및 시스템 안정성 확보
- 분산 추적 및 모니터링으로 운영 가시성 확보

**비즈니스 가치**
- 서비스별 독립적 배포로 개발 속도 향상
- 장애 격리로 전체 시스템 안정성 향상
- 수평 확장 가능한 아키텍처로 트래픽 대응력 강화

### 📚 상세 문서

- **프로젝트 정리 노션**: https://nickel-painter-d6a.notion.site/msa-spring-cloud-190e2100a14b808a9e99c513edfd6a06
- **gRPC 도입 가이드**: [docs/GRPC_INTRODUCTION_GUIDE.md](docs/GRPC_INTRODUCTION_GUIDE.md)
- **포트폴리오 작성 가이드**: [docs/PORTFOLIO_GUIDE.md](docs/PORTFOLIO_GUIDE.md)

## 🏗️ 아키텍처

### 시스템 아키텍처

```
                         ┌─────────────────┐
                         │   API Gateway   │
                         │  (Spring Cloud  │
                         │     Gateway)    │
                         └────────┬────────┘
                                  │
              ┌───────────────────┼───────────────────┐
              │                   │                   │
         ┌────▼────┐        ┌────▼────┐        ┌────▼────┐
         │  User   │        │  Order  │        │ Catalog │
         │Service  │        │Service  │        │ Service │
         └────┬────┘        └────┬────┘        └────┬────┘
              │                  │                  │
              │                  │                  │
         ┌────▼────┐        ┌────▼────┐        ┌────▼────┐
         │Cart     │        │  Kafka  │        │ MariaDB │
         │Service  │        │(Message │        │         │
         └────┬────┘        │  Queue) │        │         │
              │             └────┬────┘        └─────────┘
              │                  │
         ┌────▼────┐        ┌────▼────┐
         │ Redis   │        │ File    │
         │(Cache)  │        │Service  │
         └─────────┘        └─────────┘
              │
              │
         ┌────▼────────────────────────────────────┐
         │    Eureka (Service Discovery)           │
         │  (모든 서비스 등록 및 서비스 발견)          │
         └─────────────────────────────────────────┘
```

**연결 관계:**
- **API Gateway** → 모든 비즈니스 서비스 라우팅
- **User/Order/Catalog Service** → MariaDB (각각 독립 DB: user_db, order_db, catalog_db)
- **Order Service** → Kafka (Producer: 주문 이벤트 발행)
- **Catalog Service** → Kafka (Consumer: 재고 업데이트 이벤트 구독)
- **Cart Service** → Redis (장바구니 데이터 저장)
- **API Gateway** → Redis (Rate Limiting)
- **모든 서비스** → Eureka (Service Discovery: User, Order, Catalog, Cart, File, API Gateway)

### 핵심 설계 패턴

- **API Gateway Pattern**: 단일 진입점 제공 및 라우팅
- **Service Discovery**: Eureka를 통한 동적 서비스 등록/발견
- **Circuit Breaker Pattern**: Resilience4J로 장애 격리
- **Event-Driven Architecture**: Kafka를 통한 비동기 이벤트 처리
- **CQRS**: 명령과 조회 분리
- **Rate Limiting**: Redis 기반 API 요청 제한

### 🛠️ 기술 스택

#### Backend
- **Framework**: Spring Boot 3.3.x, Spring Cloud 2023.0.x
- **Language**: Java 17
- **Build Tool**: Gradle
- **API Communication**: OpenFeign (REST), gRPC (계획)
- **Database**: MariaDB
- **Cache**: Redis
- **Message Queue**: Apache Kafka
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway

#### Infrastructure & DevOps
- **Containerization**: Docker, Docker Compose
- **Orchestration**: Kubernetes (k8s)
- **CI/CD**: Jenkins (AWS 프리티어), 자동화된 빌드 및 배포 파이프라인

#### Monitoring & Observability
- **Distributed Tracing**: Zipkin
- **Metrics**: Prometheus
- **Visualization**: Grafana
- **Logging**: Structured Logging with Micrometer

#### Security
- **Authentication**: JWT (JSON Web Token)
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
- **Kafka UI**: http://localhost:9091 (선택 사항, docker-compose에서 주석 처리됨)

### API 엔드포인트
- **User Service**: http://localhost:8000/user-service/**
- **Order Service**: http://localhost:8000/order-service/**
- **Catalog Service**: http://localhost:8000/catalog-service/**
- **Cart Service**: http://localhost:8000/cart-service/**
- **File Service**: http://localhost:8000/file-service/**

## ✨ 핵심 기능

### 1. 장애 격리 및 회복력 (Resilience)
- **Circuit Breaker**: Resilience4J를 통한 장애 격리
  - 실패율 50% 초과 시 서킷 오픈
  - 느린 호출(5초 이상) 30% 초과 시 서킷 오픈
  - 60초 후 HALF-OPEN 상태로 전환하여 자동 복구 시도
- **Timeout**: 외부 서비스 호출 타임아웃 6초 설정
- **Fallback**: 서비스 장애 시 기본값 반환으로 사용자 경험 보장

### 2. 성능 최적화
- **비동기 처리**: Kafka를 통한 이벤트 기반 아키텍처
  - 주문 생성 시 재고 업데이트를 비동기로 처리하여 응답 시간 단축
  - 이벤트 소싱 패턴으로 데이터 일관성 보장
- **캐싱**: Redis를 활용한 세션 및 데이터 캐싱
- **Rate Limiting**: Redis 기반 API 요청 제한 (초당 10건, 버스트 10건)

### 3. 보안
- **JWT 인증**: 토큰 기반 인증으로 무상태(Stateless) 아키텍처 구현
- **Spring Security**: 역할 기반 접근 제어 (RBAC)
- **API Gateway 필터링**: 인증 토큰 검증 및 헤더 처리

### 4. 분산 시스템 관리
- **Service Discovery**: Eureka를 통한 동적 서비스 등록/발견
- **Config Management**: 중앙화된 설정 관리로 환경별 설정 분리
- **Distributed Tracing**: Zipkin을 통한 분산 추적으로 성능 병목 지점 파악

### 5. CI/CD 파이프라인
- **Jenkins**: AWS 프리티어 환경에 Jenkins 설치 및 구성
- **자동화된 빌드**: Docker 이미지 자동 빌드 및 배포
- **지속적 통합**: 코드 변경 시 자동 빌드 및 테스트 실행
- **지속적 배포**: 빌드 성공 시 자동 배포 프로세스

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

## 🚀 성능 지표

### 처리량
- **목표**: 초당 500건 이상의 요청 처리
- **현재**: REST 기반 통신으로 초당 200-300건 처리 가능
- **개선 계획**: gRPC 도입으로 초당 1000-1500건 처리 목표

### 응답 시간
- **평균 레이턴시**: 50-100ms (서비스 간 통신 포함)
- **개선 목표**: gRPC 도입 시 20-40ms (50-60% 개선 예상)

### 가용성
- Circuit Breaker를 통한 장애 격리
- 서비스별 독립적 배포로 무중단 업데이트 가능

## 🔒 보안 구현

### 인증/인가
- **JWT 토큰**: 사용자 인증 및 권한 정보 포함
- **Spring Security**: 메서드 레벨 보안 (`@PreAuthorize`)
- **API Gateway**: 인증 토큰 검증 필터

### 보안 모범 사례
- 비밀번호 암호화 저장
- 토큰 만료 시간 설정
- CORS 설정
- 헤더 보안 설정

## 🐳 배포 및 인프라

### Docker
- 각 서비스를 독립적인 Docker 컨테이너로 구성
- Docker Compose를 통한 로컬 개발 환경 구성
- 멀티 스테이지 빌드로 이미지 크기 최적화

### Kubernetes
- Kubernetes 매니페스트 파일 제공
- ConfigMap을 통한 설정 관리
- 서비스 디스커버리와 연동

### 환경 분리
- **dev**: 로컬 개발 환경
- **prod**: 프로덕션 환경
- 프로파일별 설정 분리

## 🎓 학습 내용 및 트러블슈팅

### 주요 학습 내용
1. **MSA 아키텍처 설계**: 서비스 분리 기준 및 경계 설계
2. **이벤트 기반 아키텍처**: Kafka를 통한 비동기 처리 패턴
3. **장애 대응**: Circuit Breaker 패턴으로 시스템 안정성 확보
4. **분산 추적**: Zipkin을 통한 분산 시스템 디버깅
5. **성능 최적화**: 비동기 처리로 응답 시간 개선

### 트러블슈팅 경험
- **서비스 간 통신 지연**: OpenFeign 타임아웃 설정 및 Circuit Breaker 적용
- **이벤트 순서 보장**: Kafka 파티셔닝 전략 수립
- **분산 트랜잭션**: Saga 패턴 고려 (현재는 이벤트 기반으로 처리)

## 📈 향후 개선 계획

### 단기 (1-2개월)
- [ ] gRPC 도입으로 서비스 간 통신 성능 개선
- [ ] API 문서화 (Swagger/OpenAPI)

### 중기 (3-6개월)
- [ ] 로깅 중앙화 (ELK Stack)
- [ ] 성능 테스트 및 부하 테스트

### 장기 (6개월 이상)
- [ ] 서비스 메시 도입 검토 (Istio)
- [ ] 자동 스케일링 구현

## 🔧 개발 환경

### 사전 요구사항
- Docker & Docker Compose
- Java 17+
- Gradle 7.x+

### 설정 관리
- Config Service에서 중앙화된 설정 관리
- Git 저장소와 연동하여 설정 변경 추적
- 프로파일별 설정 분리 (dev, prod)
- Spring Cloud Bus를 통한 설정 동적 갱신

## 📚 참고 자료

- [Spring Cloud 공식 문서](https://spring.io/projects/spring-cloud)
- [Resilience4J 문서](https://resilience4j.readme.io/)
- [Kafka 공식 문서](https://kafka.apache.org/documentation/)
- [gRPC 도입 가이드](docs/GRPC_INTRODUCTION_GUIDE.md)

## 👥 기여

이슈 및 개선 제안은 언제든 환영합니다!

---

**Made with ❤️ using Spring Cloud**