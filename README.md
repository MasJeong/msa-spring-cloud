# MSA-Spring-Cloud

## 아키텍처 다이어그램(예정)
![Image](https://github.com/user-attachments/assets/601972c0-5a4e-4184-a6db-e4a6a315ba30)

### 구성 요소
- **config-service**: Configuration service
- **discovery-service**: Service registration and search
- **apigateway-service**: Spring Cloud Gateway

### E-commerce 서비스
- **catalog-service**: 상품 목록 관리 서비스
- **order-service**: 주문 처리 서비스
- **user-service**: 사용자 관리 서비스

### Docker 파일
- **docker-files**: 도커 이미지 및 컨테이너 설정 파일

### APIGATEWAY 샘플 프로젝트
- **first-service**: API 게이트웨이 서비스 예제
- **second-service**: API 게이트웨이 서비스 예제