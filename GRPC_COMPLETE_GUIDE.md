# gRPC 완전 정리 가이드

## **gRPC란?**

gRPC = g(Google) + RPC(Remote Procedure Call)

- RPC: 원격 프로시저 호출 = 다른 서버의 함수를 마치 로컬 함수처럼 호출
- g: Google에서 개발

## **개념 비교**

**로컬 함수 호출**

```java
// 같은 프로세스 내
int result = calculate(10, 20);
```

**원격 함수 호출 (RPC)**

```java
// 다른 서버의 함수를 로컬 함수처럼 호출
List<Order> orders = orderServiceStub.getOrdersByUserId(request);
```

## **gRPC의 특징**

1. **마치 로컬 메서드처럼 사용**

```java
// REST: HTTP 요청 직접 처리
GET /order-service/user123/orders

// gRPC: 메서드 호출처럼 사용
orderServiceStub.getOrdersByUserId(request)
```

2. **HTTP/2 + Protobuf 사용**
- HTTP/2: 멀티플렉싱, 헤더 압축
- Protobuf: 바이너리 직렬화 (JSON보다 작고 빠름)

3. **타입 안정성**
- 프로토 파일로 계약 정의
- 컴파일 시점에 타입 검증

## **Protobuf란?**

Protobuf(Protocol Buffers)는 Google에서 만든 데이터 직렬화 형식입니다.

- JSON의 대체 형식
- 바이너리 형태로 데이터를 직렬화
- JSON보다 작고 빠름 (3-10배 작은 크기)
- 여러 언어를 지원 (Java, Python, Go, C++ 등)

## **.proto 파일이란?**

.proto 파일은 데이터 구조와 서비스 인터페이스를 정의하는 파일입니다.

**비유:** REST API의 Swagger 문서와 유사한 역할

- 어떤 데이터를 보낼지 정의
- 어떤 메서드를 호출할지 정의
- 이를 컴파일하면 Java 클래스가 생성됨

## **전체 흐름 (6단계)**

### **단계 1: .proto 파일 작성 (계약 정의)**

```protobuf
// order.proto 파일
syntax = "proto3";

package com.example.orderservice.grpc;

service OrderService {
  rpc GetOrdersByUserId (GetOrdersRequest) returns (GetOrdersResponse);
}

message GetOrdersRequest {
  string user_id = 1;  // 필드 이름과 번호
}

message GetOrdersResponse {
  repeated Order orders = 1;  // repeated = 배열/리스트
}

message Order {
  string order_id = 1;
  string user_id = 2;
  string product_id = 3;
  int32 qty = 4;
  int32 unit_price = 5;
  int32 total_price = 6;
  string created_at = 7;
}
```

**의미:**

- 서비스: OrderService라는 서비스 정의
- 메서드: GetOrdersByUserId 메서드 정의
- 메시지: 요청/응답 데이터 구조 정의

### **단계 2: 컴파일 (Java 클래스 생성)**

Gradle 빌드 시 Protobuf 플러그인이 .proto 파일을 Java 클래스로 변환합니다:

```
order.proto (텍스트 파일)
    ↓ [Protobuf 컴파일러]
build/generated/source/proto/main/grpc/.../OrderServiceGrpc.java
build/generated/source/proto/main/java/.../GetOrdersRequest.java
build/generated/source/proto/main/java/.../GetOrdersResponse.java
build/generated/source/proto/main/java/.../Order.java
```

**생성되는 클래스:**

- OrderServiceGrpc: 서비스 인터페이스 (클라이언트 stub, 서버 base 클래스)
- GetOrdersRequest, GetOrdersResponse, Order: 메시지 클래스

### **단계 3: 클라이언트 코드 작성 (User Service)**

```java
// OrderServiceGrpcClient.java
@GrpcClient("order-service")
private OrderServiceGrpc.OrderServiceBlockingStub orderServiceStub;

public List<ResponseOrder> getOrders(String userId) {
    // 1. Request 객체 생성 (프로토에서 생성된 클래스)
    GetOrdersRequest request = GetOrdersRequest.newBuilder()
            .setUserId(userId)
            .build();
    
    // 2. gRPC 호출 (네트워크 통신)
    GetOrdersResponse response = orderServiceStub.getOrdersByUserId(request);
    
    // 3. Response에서 데이터 추출
    return response.getOrdersList().stream()
            .map(this::convertToResponseOrder)
            .collect(Collectors.toList());
}
```

### **단계 4: 서버 코드 작성 (Order Service)**

```java
// OrderGrpcServiceImpl.java
@GrpcService  // gRPC 서버로 등록
@RequiredArgsConstructor
public class OrderGrpcServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {
    
    private final OrderService orderService;
    
    /**
     * gRPC 서버 메서드 구현
     * - StreamObserver: 비동기 응답 전송을 위한 콜백 객체
     */
    @Override
    public void getOrdersByUserId(
            GetOrdersRequest request,
            StreamObserver<GetOrdersResponse> responseObserver) {
        
        try {
            // 1. 비즈니스 로직 호출 (기존 OrderService 재사용)
            List<OrderEntity> orders = orderService.getOrdersByUserId(request.getUserId());
            
            // 2. Entity → gRPC 메시지 변환
            GetOrdersResponse response = GetOrdersResponse.newBuilder()
                    .addAllOrders(orders.stream()
                            .map(this::convertToGrpcOrder)
                            .collect(Collectors.toList()))
                    .build();
            
            // 3. 응답 전송 (비동기)
            responseObserver.onNext(response);      // 응답 데이터 전송
            responseObserver.onCompleted();          // 정상 완료 알림
            
        } catch (Exception e) {
            responseObserver.onError(e);             // 에러 발생 시 알림
        }
    }
    
    // Entity를 gRPC Order 메시지로 변환
    private Order convertToGrpcOrder(OrderEntity entity) {
        return Order.newBuilder()
                .setOrderId(entity.getOrderId())
                .setUserId(entity.getUserId())
                .setProductId(entity.getProductId())
                .setQty(entity.getQty())
                .setUnitPrice(entity.getUnitPrice())
                .setTotalPrice(entity.getTotalPrice())
                .setCreatedAt(entity.getCreatedAt().format(FORMATTER))  // LocalDateTime → String
                .build();
    }
}
```

**핵심 포인트:**

- `@GrpcService`: Spring Boot가 gRPC 서버로 인식
- `extends OrderServiceGrpc.OrderServiceImplBase`: 프로토에서 생성된 Base 클래스 상속
- `StreamObserver`: 비동기 응답 전송용 콜백 (서버는 항상 비동기 API 사용)
  - `onNext()`: 응답 데이터 전송
  - `onCompleted()`: 정상 완료
  - `onError()`: 에러 발생 시

**application.yml 설정 (Order Service)**

```yaml
# gRPC Server 설정
grpc:
  server:
    port: 9091  # gRPC 서버 포트 (Eureka를 통해 자동 등록됨)
```

### **단계 5: Controller에서 사용**

```java
// UserController.java
@GetMapping("/{userId}")
public ResponseEntity<ResponseUser> getUser(@PathVariable String userId) {
    // 사용자 정보 조회
    UserDto userDto = userService.getUserByUserId(userId);
    
    // gRPC로 주문 정보 조회 (Order Service 호출)
    List<ResponseOrder> orders = orderServiceGrpcClient.getOrders(userId);
    
    userDto.setOrders(orders);
    return ResponseEntity.ok(responseUser);
}
```

### **단계 6: 네트워크 통신 (실제 호출)**

```
User Service (클라이언트)
    ↓ gRPC 호출 (HTTP/2 + Protobuf 바이너리)
    ↓ 네트워크
Order Service (서버)
    ↓ OrderGrpcServiceImpl.getOrdersByUserId() 처리
    ↓ GetOrdersResponse 반환
    ↓ gRPC 응답
User Service
```

## **REST vs gRPC 비교**

### **REST (기존 방식)**

```java
// OpenFeign 방식
@FeignClient(name = "order-service")
public interface OrderServiceClient {
    @GetMapping("/order-service/{userId}/orders")
    List<ResponseOrder> getOrders(@PathVariable String userId);
}
```

**데이터 전송:**

- 형식: JSON (텍스트)
- 예: `{"userId": "user123", "orders": [...]}`
- 크기: 큼 (가독성 포함)

### **gRPC (새로운 방식)**

```java
// gRPC 방식
GetOrdersRequest request = GetOrdersRequest.newBuilder()
        .setUserId(userId)
        .build();
GetOrdersResponse response = orderServiceStub.getOrdersByUserId(request);
```

**데이터 전송:**

- 형식: Protobuf (바이너리)
- 예: `[바이너리 데이터]`
- 크기: 작음 (JSON 대비 3-10배 작음)

## **서버 vs 클라이언트 비교**

| 항목 | 클라이언트 (User Service) | 서버 (Order Service) |
|------|-------------------------|---------------------|
| **역할** | 요청을 보내는 쪽 | 요청을 받아 처리하는 쪽 |
| **Stub 타입** | `BlockingStub` (동기) | `ServiceImplBase` 상속 |
| **응답 방식** | `return` (동기) | `StreamObserver` (비동기) |
| **어노테이션** | `@GrpcClient` | `@GrpcService` |
| **메서드 시그니처** | `GetOrdersResponse method(Request)` | `void method(Request, StreamObserver)` |
| **설정 파일** | `grpc.client.order-service.address` | `grpc.server.port` |

## **프로젝트에서의 실제 흐름**

### **1. 프로토 파일 위치**

- **user-service/src/main/proto/order.proto** (클라이언트용)
- **order-service/src/main/proto/order.proto** (서버용)

⚠️ **각 서비스에 필요한 이유:** 각 서비스가 독립적으로 빌드되므로, 각자 컴파일해서 Java 클래스를 생성해야 함

### **2. 컴파일 후 생성되는 위치**

**User Service:**
```
user-service/build/generated/source/proto/main/
├─ grpc/com/example/orderservice/grpc/
│   └─ OrderServiceGrpc.java  ← 클라이언트 Stub (BlockingStub)
└─ java/com/example/orderservice/grpc/
├─ GetOrdersRequest.java
├─ GetOrdersResponse.java
└─ Order.java
```

**Order Service:**
```
order-service/build/generated/source/proto/main/
├─ grpc/com/example/orderservice/grpc/
│   └─ OrderServiceGrpc.java  ← 서버 Base 클래스 (ServiceImplBase)
└─ java/com/example/orderservice/grpc/
├─ GetOrdersRequest.java
├─ GetOrdersResponse.java
└─ Order.java
```

### **3. 코드에서 사용**

**클라이언트 (User Service):**
```java
// 동기 방식 호출
GetOrdersResponse response = orderServiceStub.getOrdersByUserId(request);
```

**서버 (Order Service):**
```java
// 비동기 방식 응답
responseObserver.onNext(response);
responseObserver.onCompleted();
```

### **4. 전체 호출 흐름**

1. 사용자가 브라우저에서 요청
   ```
   GET /users/user123
   ```

2. API Gateway (REST)
   ```
   → User Service (포트 8082)
   ```

3. UserController.getUser()
   ```
   → userService.getUserByUserId()
   → orderServiceGrpcClient.getOrders() ← gRPC 클라이언트 호출
   ```

4. OrderServiceGrpcClient (User Service)
   ```
   → GetOrdersRequest 생성
   → BlockingStub.getOrdersByUserId(request) ← 동기 호출 (블로킹)
   → gRPC 네트워크 통신 (HTTP/2 + Protobuf)
   → Order Service (포트 9090)
   ```

5. OrderGrpcServiceImpl (Order Service)
   ```
   → getOrdersByUserId(request, responseObserver) ← 서버 메서드 호출
   → orderService.getOrdersByUserId() ← 비즈니스 로직
   → GetOrdersResponse 생성
   → responseObserver.onNext(response) ← 응답 전송
   → responseObserver.onCompleted() ← 완료
   ```

6. OrderServiceGrpcClient (User Service)
   ```
   → GetOrdersResponse 수신
   → ResponseOrder로 변환
   → 반환
   ```

7. UserController
   ```
   → 사용자 정보 + 주문 정보 합쳐서
   → JSON 응답
   ```

## **gRPC 서버의 특징: StreamObserver**

서버는 항상 `StreamObserver`를 사용하는 비동기 API를 사용합니다.

**왜 비동기인가?**

1. **HTTP/2 스트리밍 지원**: 여러 번 데이터 전송 가능
2. **다양한 RPC 타입 지원**: Unary, Server Streaming, Client Streaming, Bidirectional Streaming
3. **리소스 효율성**: 스레드 블로킹 없이 높은 처리량

**하지만 클라이언트는 동기/비동기 선택 가능:**

- `BlockingStub`: 동기 방식 (현재 사용 중) - REST와 유사하게 사용
- `AsyncStub`: 비동기 방식 (Future 기반)
- `Stub`: 스트리밍 지원

**실제 동작:**
- 서버: 비동기 API 사용 (`StreamObserver`)
- 클라이언트: 동기적으로 호출 (`BlockingStub`) - 응답이 올 때까지 대기

## **핵심 정리**

1. **.proto 파일 = 계약서**
   - 서비스와 데이터 구조를 정의

2. **컴파일 = 코드 생성**
   - .proto → Java 클래스 자동 생성

3. **gRPC = 통신 방식**
   - HTTP/2 + Protobuf 바이너리
   - REST보다 빠르고 효율적

4. **사용 방식**
   - 생성된 Java 클래스 사용
   - 빌더 패턴으로 객체 생성
   - 메서드 호출처럼 사용

5. **서버 구현**
   - `@GrpcService` 어노테이션
   - `ServiceImplBase` 상속
   - `StreamObserver`로 비동기 응답

6. **클라이언트 구현**
   - `@GrpcClient` 어노테이션
   - `BlockingStub`으로 동기 호출

## **REST와 비교한 장점**

| 항목 | REST (JSON) | gRPC (Protobuf) |
|------|------------|----------------|
| 데이터 형식 | 텍스트 (JSON) | 바이너리 |
| 크기 | 큼 | 작음 (3-10배) |
| 속도 | 느림 | 빠름 |
| 타입 안정성 | 약함 | 강함 (프로토 파일로 검증) |
| 계약 정의 | Swagger/OpenAPI | .proto 파일 |
| 서버 API | 동기 (return) | 비동기 (StreamObserver) |
| 클라이언트 API | 동기 | 동기/비동기 선택 가능 |

