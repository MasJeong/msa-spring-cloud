# Optimization Review

Date: 2026-02-28
Scope: order-service + catalog-service Kafka stock saga path

## What changed

1) Reuse `ObjectMapper` in Kafka producers
- `order-service/src/main/java/com/example/orderservice/com/msgqueue/KafkaProducer.java`
- `catalog-service/src/main/java/com/example/catalogservice/com/msgqueue/KafkaProducer.java`
- Change: removed per-call `new ObjectMapper()` and injected shared `ObjectMapper` bean.
- Benefit: lower object allocation and GC overhead on high publish volume.

2) Narrow Kafka send exception handling
- Same two producer files above
- Change: replaced broad `Exception` catch with
  - `InterruptedException` (restores interrupt flag)
  - `ExecutionException | TimeoutException`
- Benefit: safer thread-interruption behavior and clearer failure classification.

3) Optimize order compensation delete path
- `order-service/src/main/java/com/example/orderservice/order/repository/OrderRepository.java`
- `order-service/src/main/java/com/example/orderservice/order/service/OrderService.java`
- Change: `cancelOrder` now executes direct delete by `orderId` query (`deleteByOrderId`) instead of find-then-delete.
- Benefit: fewer DB round-trips in compensation path.

4) Remove unnecessary JPA save calls
- `catalog-service/src/main/java/com/example/catalogservice/catalog/service/CatalogService.java`
- Change: removed explicit `catalogRepository.save(entity)` after stock field mutation.
- Benefit: rely on JPA dirty checking under transaction, reducing redundant persistence operations.

## Review checklist

- Verify producer constructors still resolve with Spring DI (`ObjectMapper` bean available)
- Verify stock-decrease and stock-restore behavior is unchanged functionally
- Verify compensation (`cancelOrder`) still handles missing orderId safely (no exception path change)
- Verify Kafka send timeout/interrupt behavior aligns with expected operational policy

## Risk level

- Overall: Low
- Why: no topic/schema/API contract changes; behavior-preserving runtime optimizations only.
