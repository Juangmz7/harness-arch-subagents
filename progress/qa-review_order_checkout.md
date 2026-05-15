# Review — feature order_checkout (id=2)

**Verdict:** APPROVED

## Checkpoints

- C1: [x] — Base files exist (AGENTS.md, init.sh, feature_list.json, progress/current.md); docs exist (architecture.md, conventions.md, verification.md); build compiles successfully.
- C2: [x] — Only feature #2 is in_progress; feature #1 is done with passing tests; progress/current.md describes the active session with no leftover garbage.
- C3: [x] — All new files reside in the correct layers (Controller, Service, Repository, Model/Entity, DTO, Exception); spring-boot-starter-validation is an approved Spring Boot starter; no System.out.println or e.printStackTrace() found in any production file.
- C4: [x] — OrderServiceTest (3 Mockito-only unit tests) and OrderControllerIntegrationTest (5 MockMvc + H2 integration tests) both exist; mvn test reports 17 tests run, 0 failures, 0 errors — BUILD SUCCESS.
- C5: [x] — No stray .class files or target/ directory outside .gitignore; progress/history.md has an entry for the prior session; feature #2 is correctly marked in_progress.

## Acceptance Criteria Results

1. Order entity fields: [x] — Long id, List<OrderItem> items, BigDecimal total, LocalDateTime createdAt, OrderStatus status (PENDING, CONFIRMED, CANCELLED) all present in Order.java and OrderStatus.java.
2. OrderItem @Embeddable with explicit @Column names: [x] — @Embeddable with @Column(name="product_id"), @Column(name="quantity"), @Column(name="unit_price").
3. POST /api/orders request shape: [x] — CreateOrderRequest record with @NotNull @NotEmpty @Valid List<OrderItemRequest>; OrderItemRequest has @NotNull productId and @Min(1) quantity.
4. POST /api/orders returns HTTP 201 with OrderDTO: [x] — OrderController returns ResponseEntity.status(HttpStatus.CREATED).body(orderDTO); OrderDTO includes id, items, total, createdAt, status.
5. Missing product returns 404, no stock modified: [x] — resolveProducts() calls findById(...).orElseThrow(ResourceNotFoundException) before any write; GlobalExceptionHandler maps to 404; integration test verifies.
6. Insufficient stock returns 422, no stock deducted for ANY item: [x] — validateStock() runs entirely before deductStockAndBuildItems(); GlobalExceptionHandler maps InsufficientStockException to 422; integration test verifies stock unchanged.
7. Stock validation before write: [x] — resolveProducts() and validateStock() both complete before deductStockAndBuildItems() is called; no partial write possible.
8. GlobalExceptionHandler: [x] — ResourceNotFoundException -> 404, InsufficientStockException -> 422, MethodArgumentNotValidException -> 400; all return ErrorResponseDTO with timestamp, status, error, message, path fields.
9. OrderServiceTest Mockito-only: [x] — @ExtendWith(MockitoExtension.class), no @SpringBootTest; covers successful order (stock reduced for all items), InsufficientStockException (no save calls), ResourceNotFoundException (no save calls).
10. Test isolation: [x] — OrderControllerIntegrationTest uses @BeforeEach with orderRepository.deleteAll() and productRepository.deleteAll().
11. No System.out.println or e.printStackTrace(): [x] — grep on src/main/java found zero matches; @Slf4j used for all logging.

## Architecture and Convention Compliance

- Strict layer separation: [x] — OrderController delegates entirely to OrderService; no JPA or repository access in the controller; no business logic in the controller.
- Constructor injection: [x] — OrderController and OrderService both use @RequiredArgsConstructor with private final fields; no @Autowired field injection in production code. (The @Autowired in OrderControllerIntegrationTest is the accepted test pattern per docs/verification.md.)
- Transactional at service level: [x] — OrderService.createOrder() is @Transactional; InsufficientStockException extends DomainException (RuntimeException) ensuring automatic rollback.
- No domain logic in controller: [x] — total computation, stock validation, and order persistence are all in OrderService private helpers.
- DTOs not entities in responses: [x] — OrderController accepts CreateOrderRequest and returns OrderDTO; Order entity is never exposed.
- Compose-method pattern: [x] — createOrder() delegates to resolveProducts(), validateStock(), deductStockAndBuildItems(), persistOrder(), toDTO() — consistent with conventions.md code style example.
- Naming conventions: [x] — all classes PascalCase, methods camelCase, endpoints kebab-case (/api/orders).
- Import ordering and wildcard imports: [x] — all files use explicit imports; no wildcard imports found.
- Java records for DTOs: [x] — OrderItemRequest, CreateOrderRequest, OrderItemDTO, OrderDTO are all records.
- SLF4J logging via @Slf4j: [x] — OrderService uses log.debug(); GlobalExceptionHandler uses log.warn().

## Minor Observations (non-blocking)

- The readOnly=true @Transactional hint is not applied to any read methods in OrderService. The feature only defines createOrder() (a write operation), so there are no read methods to annotate; this is not a violation.
- progress/history.md does not yet have an entry for the feature #2 session (the session is still in_progress per current.md), so C5 is met as documented.
