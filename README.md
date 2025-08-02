# MSA-Spring-Cloud 

### 프로젝트 정리 노션
https://nickel-painter-d6a.notion.site/msa-spring-cloud-190e2100a14b808a9e99c513edfd6a06

## 🏗️ 프로젝트 구조

이 프로젝트는 Spring Cloud를 사용한 마이크로서비스 아키텍처(MSA) 구현체입니다.

### 📁 서비스 구성

| 서비스 | 포트 | 설명 |
|--------|------|------|
| **Config Service** | 8888 | 중앙화된 설정 관리 |
| **Discovery Service** | 8761 | 서비스 디스커버리 (Eureka) |
| **API Gateway** | 8000 | API 게이트웨이 (Spring Cloud Gateway) |
| **User Service** | 랜덤 | 사용자 관리 서비스 |
| **Order Service** | 랜덤 | 주문 관리 서비스 |
| **Catalog Service** | 랜덤 | 상품 카탈로그 서비스 |
| **Cart Service** | 랜덤 | 장바구니 서비스 |
| **File Service** | 랜덤 | 파일 관리 서비스 |

### 🛠️ 인프라 서비스

| 서비스 | 포트        | 설명 |
|--------|-----------|------|
| **MariaDB** | 3307      | 메인 데이터베이스 |
| **Redis** | 6379      | 캐시 및 세션 저장소 |
| **Kafka** | 9092/9094 | 메시지 큐 |
| **Zipkin** | 9411      | 분산 추적 |
| **Prometheus** | 9090      | 메트릭 수집 |
| **Grafana** | 3000      | 모니터링 대시보드 |

## 🚀 빠른 시작

### 1. 사전 요구사항

- Docker & Docker Compose
- Java 17+
- Gradle

### 2. Docker 이미지 빌드

#### Linux/Mac
```bash
chmod +x build-images.sh
./build-images.sh
```

#### Windows
```cmd
build-images.bat
```

### 3. 서비스 시작

#### 전체 서비스 시작
```bash
docker-compose up -d
```

#### 단계별 시작
```bash
# 1. 인프라 서비스 시작
docker-compose up -d mariadb redis kafka

# 2. 설정 및 디스커버리 서비스 시작
docker-compose up -d config-service discovery-service

# 3. API Gateway 시작
docker-compose up -d apigateway-service

# 4. 비즈니스 서비스 시작
docker-compose up -d user-service order-service catalog-service cart-service file-service

# 5. 모니터링 서비스 시작
docker-compose up -d zipkin prometheus grafana kafka-ui
```

## 🔗 서비스 접속 정보

### 주요 서비스 URL
- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8000
- **Config Service**: http://localhost:8888
- **Zipkin**: http://localhost:9411
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000
- **Kafka UI**: http://localhost:9091

### API 엔드포인트
- **User Service**: http://localhost:8000/user-service/**
- **Order Service**: http://localhost:8000/order-service/**
- **Catalog Service**: http://localhost:8000/catalog-service/**
- **Cart Service**: http://localhost:8000/cart-service/**
- **File Service**: http://localhost:8000/file-service/**

## 📊 모니터링

### Prometheus 메트릭
- 각 서비스의 `/actuator/prometheus` 엔드포인트에서 메트릭 수집
- Prometheus UI에서 실시간 메트릭 확인 가능

### Grafana 대시보드
- 기본 로그인: admin/admin
- Prometheus 데이터 소스로 대시보드 구성 가능

### Zipkin 추적
- 분산 추적을 통한 서비스 간 호출 추적
- 성능 병목 지점 분석 가능

## 🔧 개발 환경

### 설정 관리
- Config Service에서 중앙화된 설정 관리
- Git 저장소와 연동하여 설정 변경 추적
- 프로파일별 설정 분리 (dev, prod)