# Historical log (append-only)

> Each time a session is closed, its summary is appended here.
> Do not edit previous entries. Only add at the end.

---

## 2026-04-20 — Project bootstrap
- **Agent:** human (Juan)
- **Changes:** initial harness structure (AGENTS.md, feature_list.json, docs/).
- **Result:** environment ready. `./init.sh` green.

---

## 2026-05-14 — Feature 1: product_catalog

- **Agent:** leader → implementer → qa-reviewer
- **Branch:** `working-on-product_catalog`
- **Changes:**
  - `src/main/java/.../model/exception/DomainException.java` — base domain exception
  - `src/main/java/.../model/exception/ResourceNotFoundException.java` — extends DomainException
  - `src/main/java/.../model/entity/Product.java` — @Entity with explicit @Column names
  - `src/main/java/.../model/dto/ProductDTO.java` — Java Record
  - `src/main/java/.../model/dto/ErrorResponseDTO.java` — Java Record for structured errors
  - `src/main/java/.../repository/ProductRepository.java` — extends JpaRepository
  - `src/main/java/.../service/ProductService.java` — @Transactional(readOnly=true) on both methods
  - `src/main/java/.../controller/ProductController.java` — GET /api/products + GET /api/products/{id}
  - `src/main/java/.../controller/GlobalExceptionHandler.java` — ResourceNotFoundException → 404
  - `src/test/.../service/ProductServiceTest.java` — 4 Mockito-only unit tests
  - `src/test/.../controller/ProductControllerIntegrationTest.java` — 4 integration tests
- **Result:** 9/9 tests pass, QA approved, feature marked done. `./init.sh` green.

---

## 2026-05-15 — Feature 2: order_checkout

- **Agent:** leader → implementer → qa-reviewer
- **Branch:** `working-on-order_checkout`
- **Changes:**
  - `pom.xml` — added spring-boot-starter-validation dependency
  - `src/main/java/.../model/entity/OrderStatus.java` — enum: PENDING, CONFIRMED, CANCELLED
  - `src/main/java/.../model/entity/OrderItem.java` — @Embeddable with explicit @Column names
  - `src/main/java/.../model/entity/Order.java` — @Entity @Table("orders"), @ElementCollection items, @PrePersist for createdAt + default status
  - `src/main/java/.../model/dto/OrderItemRequest.java` — record: @NotNull productId, @Min(1) quantity
  - `src/main/java/.../model/dto/CreateOrderRequest.java` — record: @NotNull @NotEmpty @Valid items
  - `src/main/java/.../model/dto/OrderItemDTO.java` — record: productId, quantity, unitPrice
  - `src/main/java/.../model/dto/OrderDTO.java` — record: id, items, total, createdAt, status
  - `src/main/java/.../model/exception/InsufficientStockException.java` — extends DomainException
  - `src/main/java/.../repository/OrderRepository.java` — extends JpaRepository<Order, Long>
  - `src/main/java/.../service/OrderService.java` — atomic @Transactional createOrder(): validate all → deduct all → persist
  - `src/main/java/.../controller/OrderController.java` — POST /api/orders → 201
  - `src/main/java/.../controller/GlobalExceptionHandler.java` — added InsufficientStockException → 422 and MethodArgumentNotValidException → 400
  - `src/test/.../service/OrderServiceTest.java` — 3 Mockito-only unit tests
  - `src/test/.../controller/OrderControllerIntegrationTest.java` — 5 integration tests
- **Result:** 17/17 tests pass, QA approved. `./init.sh` green.