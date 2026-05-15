# Review — feature 1 (product_catalog)

**Verdict:** APPROVED

## Checkpoints

- C1: [x] — Base files exist (`AGENTS.md`, `init.sh`, `feature_list.json`, `progress/current.md`); docs exist; `mvn clean verify` exits with BUILD SUCCESS.
- C2: [x] — Exactly one feature in `in_progress` (`product_catalog`, id=1); all tests pass; `progress/current.md` reflects the active session cleanly.
- C3: [x] — Source tree contains only the permitted layers (Controller, Service, Repository, Model/Entity, DTO, Mapper, Config/Exception). No unapproved dependencies. No `System.out.println`, no `e.printStackTrace()`, no TODOs found. One minor finding (non-blocking): `GlobalExceptionHandler.java` carries two unused imports (`WebRequest` at line 9 and `DomainException` at line 15) that should be removed in a follow-up cleanup, but they do not affect correctness or test outcomes.
- C4: [x] — `ProductServiceTest` (3 tests, Mockito only, `@ExtendWith(MockitoExtension.class)`, no Spring context) and `ProductControllerIntegrationTest` (4 tests, `@SpringBootTest` + real H2 + `MockMvcBuilders.webAppContextSetup`) exist and are green. `mvn test` reports 8 tests executed, 0 failures, 0 errors.
- C5: [x] — No suspicious untracked files. `progress/history.md` exists. Feature 1 is reflected as `in_progress` in `feature_list.json` (to be closed by the leader after this review).

## Acceptance Criteria Verification

1. [x] `Product.java` — `@Entity` and `@Table(name = "products")` present; all five fields (`id`, `name`, `description`, `price`, `stock`) have explicit `@Column(name = "...")` annotations.
2. [x] `ProductRepository` extends `JpaRepository<Product, Long>` with no custom queries.
3. [x] `ProductDTO` is a Java record with fields `Long id, String name, String description, BigDecimal price, int stock` — exact match to the acceptance criterion.
4. [x] `GET /api/products` returns HTTP 200 with a JSON array. `getAllProducts_returns200WithEmptyArray_whenNoProducts` confirms `[]` (not null) on empty data; `jsonPath("$", hasSize(0))` would fail on null.
5. [x] `GET /api/products/{id}` returns HTTP 200 with a single `ProductDTO` when product exists — verified in `getProductById_returns200WithCorrectData_whenExists`.
6. [x] `GET /api/products/{id}` returns HTTP 404 with a structured JSON error body — verified in `getProductById_returns404WithErrorBody_whenNotExists`; asserts `status`, `error`, `message`, `timestamp`, and `path` fields.
7. [x] `ProductServiceImpl.findAll()` at line 26 and `findById()` at line 36 are both annotated `@Transactional(readOnly = true)` using `org.springframework.transaction.annotation.Transactional`.
8. [x] `ResourceNotFoundException` extends `DomainException` (not `RuntimeException` directly). `DomainException` extends `RuntimeException`. Chain is correct.
9. [x] `ProductServiceTest` covers: `findAll_returnsMappedDtos`, `findById_returnsCorrectDto`, `findById_throwsResourceNotFoundException_whenNotFound` — all using `@ExtendWith(MockitoExtension.class)`, `@Mock`, and `@InjectMocks`. No Spring context loaded.
10. [x] `ProductControllerIntegrationTest` uses `@SpringBootTest(webEnvironment = RANDOM_PORT)` + `MockMvc` built from `WebApplicationContext` + real H2. Verifies HTTP status, `Content-Type: application/json`, JSON field values, and empty-list returns `[]` (not null).
11. [x] No `System.out.println` or `e.printStackTrace()` found anywhere in `src/`.

## Non-blocking Observations (no changes required)

- `GlobalExceptionHandler.java` lines 9 and 15: unused imports `WebRequest` and `DomainException`. These are dead imports — the `WebRequest` parameter was never wired into any method, and `DomainException` is not handled by a dedicated `@ExceptionHandler`. Recommend removing both in a follow-up cleanup.
- `ProductControllerIntegrationTest` is named `...IntegrationTest` whereas `docs/conventions.md` prescribes `...IT` for controller integration tests. This is a naming convention deviation but the acceptance criterion explicitly names this class, so it does not constitute a blocking defect.
- The `@Transactional(readOnly = true)` annotations are on the implementation class (`ProductServiceImpl`) rather than the interface (`ProductService`). This is the correct Spring convention; annotations on interfaces are not reliably picked up by Spring's AOP proxying when using class-based proxies (CGLIB).

## Build Results

```
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
Total time: 20.236 s
```
