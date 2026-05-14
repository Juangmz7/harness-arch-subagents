# Implementation Summary: Feature #2 — order_checkout

## Status
Implemented and verified. All 17 tests pass (including 8 new tests for this feature).

## Files Created

### Entities
- `src/main/java/com/cne_project/harnessdemo/model/entity/OrderStatus.java` — enum: PENDING, CONFIRMED, CANCELLED
- `src/main/java/com/cne_project/harnessdemo/model/entity/OrderItem.java` — @Embeddable with explicit @Column names (product_id, quantity, unit_price)
- `src/main/java/com/cne_project/harnessdemo/model/entity/Order.java` — @Entity @Table(name="orders"), @ElementCollection + @CollectionTable for items, @PrePersist to set createdAt and default PENDING status

### DTOs (Java Records)
- `src/main/java/com/cne_project/harnessdemo/model/dto/OrderItemRequest.java` — @NotNull productId, @Min(1) quantity
- `src/main/java/com/cne_project/harnessdemo/model/dto/CreateOrderRequest.java` — @NotNull @NotEmpty @Valid items list
- `src/main/java/com/cne_project/harnessdemo/model/dto/OrderItemDTO.java` — productId, quantity, unitPrice
- `src/main/java/com/cne_project/harnessdemo/model/dto/OrderDTO.java` — id, items, total, createdAt, status

### Exception
- `src/main/java/com/cne_project/harnessdemo/model/exception/InsufficientStockException.java` — extends DomainException (unchecked); constructor takes productId, requested, available

### Repository
- `src/main/java/com/cne_project/harnessdemo/repository/OrderRepository.java` — extends JpaRepository<Order, Long>

### Service
- `src/main/java/com/cne_project/harnessdemo/service/OrderService.java` — @Service @Transactional; createOrder() method with 4 private compose-method helpers: resolveProducts (validates all exist first), validateStock (validates all stock before any write), deductStockAndBuildItems, persistOrder

### Controller
- `src/main/java/com/cne_project/harnessdemo/controller/OrderController.java` — POST /api/orders returns 201

## Files Modified
- `pom.xml` — added spring-boot-starter-validation dependency
- `src/main/java/com/cne_project/harnessdemo/controller/GlobalExceptionHandler.java` — added handlers for InsufficientStockException (422) and MethodArgumentNotValidException (400 with field error details)

## Test Files Created
- `src/test/java/com/cne_project/harnessdemo/service/OrderServiceTest.java` — 3 Mockito-only unit tests: successful order reduces stock for all items, InsufficientStockException thrown without any stock modification, ResourceNotFoundException thrown when product missing
- `src/test/java/com/cne_project/harnessdemo/controller/OrderControllerIntegrationTest.java` — 5 integration tests: 201 with correct total + stock verified in DB, 404 when product missing, 422 with stock verified unchanged, 400 for empty items list, 400 for zero quantity

## Key Design Decisions
- Stock validation (resolveProducts + validateStock) runs entirely before any write (deductStockAndBuildItems), preventing partial stock corruption.
- InsufficientStockException extends DomainException (RuntimeException) so Spring @Transactional rolls back automatically.
- Order total computed server-side from product.getPrice() at time of order; never from request body.
- @Slf4j used for all logging; no System.out.println anywhere.
- Integration test uses @BeforeEach deleteAll on both repositories for clean isolation.
