# Review — feature 2 (order_checkout)

**Verdict:** NEEDS_FIXES

**Build:** `mvn clean verify` — BUILD SUCCESS, 17 tests, 0 failures, 0 errors.

---

## Acceptance Criteria Results

### AC1 — Order entity fields
PASS. Order.java declares Long id, List<OrderItem> items, BigDecimal total, LocalDateTime createdAt, OrderStatus status. OrderStatus enum has PENDING, CONFIRMED, CANCELLED.

### AC2 — OrderItem as @Embeddable with price capture
PASS. OrderItem.java is @Embeddable with productId (Long), quantity (int), unitPrice (BigDecimal). The unit price is copied from the product at order time in OrderService.deductStockAndBuildItems() (line 85).

### AC3 — POST /api/orders request shape and validation
PASS. OrderController maps POST /api/orders. CreateOrderRequest holds @NotNull @NotEmpty @Valid List<OrderItemRequest>. OrderItemRequest has @NotNull Long productId and @Min(1) int quantity.

### AC4 — HTTP 201 with OrderDTO including id and total
PASS. OrderController.createOrder() returns ResponseEntity.status(HttpStatus.CREATED).body(orderDTO). OrderDTO record includes id, items, total, createdAt, status. Integration test createOrder_shouldReturn201WithOrderDTO_whenRequestIsValid asserts $.id, $.total, and status().isCreated().

### AC5 — HTTP 404 when product does not exist, no stock modified
PASS. OrderService.resolveProducts() throws ResourceNotFoundException before any stock deduction. GlobalExceptionHandler maps it to HTTP 404. Both the integration test and the unit test verify this behavior.

### AC6 — HTTP 422 on insufficient stock, no stock deducted for any item
PASS. OrderService.validateStock() iterates all items and throws InsufficientStockException before deductStockAndBuildItems() is called. GlobalExceptionHandler maps to HTTP 422. Integration test verifies stock unchanged; unit test asserts productRepository.save() is never called.

### AC7 — Stock validation before any write
PASS. In OrderService.createOrder() lines 46-48, call order is: resolveProducts() then validateStock() then deductStockAndBuildItems(). No write occurs until full validation passes.

### AC8 — GlobalExceptionHandler handles all three exceptions with uniform structure
PASS. GlobalExceptionHandler (@RestControllerAdvice) handles ResourceNotFoundException to 404, InsufficientStockException to 422, MethodArgumentNotValidException to 400. All return ErrorResponseDTO(timestamp, status, error, message, path).

Minor note: HttpStatus.UNPROCESSABLE_ENTITY is deprecated in the Spring Boot version in use. It causes compiler warnings at GlobalExceptionHandler.java lines 48, 49, 53. Build still succeeds.

### AC9 — OrderServiceTest coverage
PASS. OrderServiceTest contains three tests: (1) happy path verifying stock reduced and save called, (2) insufficient stock verifying exception thrown and no save, (3) product not found verifying exception thrown and no save.

### AC10 — Tests use @Transactional or manual rollback
FAIL.

OrderServiceTest is a pure Mockito unit test with @ExtendWith(MockitoExtension.class) — no database, no isolation mechanism needed. Correct.

OrderControllerIntegrationTest (@SpringBootTest) does NOT use @Transactional on test methods and does NOT perform a manual rollback. It uses @BeforeEach with orderRepository.deleteAll() and productRepository.deleteAll() at lines 37-38. This is pre-test cleanup, not rollback-after. If a test throws an unexpected exception partway through and leaves dirty state, it would be cleaned before the next test starts — but this is still not the "manual rollback" the criterion requires. The criterion states "@Transactional on the test method or manual rollback."

Required fix: Add @Transactional at the class level of OrderControllerIntegrationTest so Spring rolls back the database after each test automatically.
File: src/test/java/com/cne_project/harnessdemo/controller/OrderControllerIntegrationTest.java

### AC11 — No System.out.println or e.printStackTrace() in production code
PASS. Grep of src/main/java for System.out and e.printStackTrace returns zero matches.

---

## Additional Convention Violations

### V1 — Excessive Javadoc (docs/conventions.md violation)

docs/conventions.md: "Comments are forbidden by default. No Javadoc... Class-level Javadoc is only allowed if the class name + package do not fully convey its responsibility."

The following files contain class-level and/or method-level Javadoc that is not justified by any of the three permitted exceptions:
- src/main/java/com/cne_project/harnessdemo/model/entity/OrderStatus.java lines 4-6
- src/main/java/com/cne_project/harnessdemo/model/exception/DomainException.java lines 4-6
- src/main/java/com/cne_project/harnessdemo/model/exception/ResourceNotFoundException.java lines 4-6
- src/main/java/com/cne_project/harnessdemo/model/dto/ErrorResponseDTO.java lines 6-8
- src/main/java/com/cne_project/harnessdemo/model/dto/OrderItemDTO.java lines 7-9
- src/main/java/com/cne_project/harnessdemo/controller/OrderController.java lines 15-17
- src/main/java/com/cne_project/harnessdemo/model/entity/Order.java lines 27-30
- src/main/java/com/cne_project/harnessdemo/model/entity/OrderItem.java lines 13-17

### V2 — Deprecated API in GlobalExceptionHandler (advisory)

HttpStatus.UNPROCESSABLE_ENTITY is deprecated in Spring 6.x. The compiler warns at GlobalExceptionHandler.java lines 48, 49, 53. Should be replaced with HttpStatus.valueOf(422) or equivalent non-deprecated constant.

---

## Required Changes

1. BLOCKING (AC10): Add @Transactional at the class level (or per @Test method) in OrderControllerIntegrationTest so database state is rolled back after each test, not just cleaned before the next.
   File: src/test/java/com/cne_project/harnessdemo/controller/OrderControllerIntegrationTest.java

2. ADVISORY (V1): Remove unjustified Javadoc from all files listed under V1 above.

3. ADVISORY (V2): Replace HttpStatus.UNPROCESSABLE_ENTITY at GlobalExceptionHandler.java lines 48, 49, 53 to eliminate the deprecation warning.
