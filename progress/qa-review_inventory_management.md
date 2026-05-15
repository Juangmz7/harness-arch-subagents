# Review — feature 3 (inventory_management)

**Verdict:** NEEDS_FIXES

## Acceptance Criteria Results

### C1 — POST /api/products accepts CreateProductRequest with correct constraints
**PASS**
`CreateProductRequest.java` declares `@NotBlank String name`, `@NotBlank String description`, `@NotNull @DecimalMin("0.01") BigDecimal price`, `@Min(0) int stock`. All required constraints present.

### C2 — POST /api/products returns HTTP 201 with full ProductDTO and Location header
**PASS**
`ProductController.java` line 43: `@PostMapping` handler calls `productService.create(request)`, builds Location via `ServletUriComponentsBuilder` (line 57-60), and returns `ResponseEntity.created(location).body(dto)`. `ProductDTO` record includes `id`, `name`, `description`, `price`, `stock`.

### C3 — POST /api/products returns HTTP 409 Conflict with structured error body on duplicate name
**PASS**
`GlobalExceptionHandler.java` lines 53-66 handle `DuplicateProductNameException` and return `ResponseEntity.status(HttpStatus.CONFLICT).body(error)` with a structured `ErrorResponseDTO`. `ProductService.java` line 62-65 throws `DuplicateProductNameException` via `assertNoDuplicateName` before saving.

### C4 — PUT /api/products/{id}/stock accepts UpdateStockRequest { int quantity (@Min(0)) }; quantity is absolute value
**PASS**
`UpdateStockRequest.java`: `@Min(0) int quantity`. `ProductService.updateStock` line 56: `product.setStock(request.getQuantity())` — sets absolute value (not delta).

### C5 — PUT /api/products/{id}/stock returns HTTP 200 with updated ProductDTO
**PASS**
`ProductController.java` line 50-54: `@PutMapping("/{id}/stock")` returns `ResponseEntity.ok(productService.updateStock(id, request))`.

### C6 — PUT /api/products/{id}/stock returns HTTP 404 via GlobalExceptionHandler (not try-catch in controller)
**PASS**
`ProductService.java` line 55: throws `ResourceNotFoundException` via `orElseThrow`. `ProductController.java` has no try-catch. `GlobalExceptionHandler.java` lines 23-36 handle `ResourceNotFoundException` and return HTTP 404.

### C7 — Both endpoints reject invalid fields with HTTP 400 and body listing which fields failed and why
**FAIL**
File: `src/main/java/com/cne_project/harnessdemo/controller/GlobalExceptionHandler.java`, lines 72-74.

The `handleValidationErrors` method maps errors using `FieldError::getDefaultMessage` only, which returns the constraint message (e.g., "must not be blank") without the field name. The field name is accessible via `FieldError::getField()` but is not included. As a result, the error body does NOT list which fields failed — only why. Criterion 7 requires both. Fix: change the mapping to include the field name, e.g.:
```java
.map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
```

### C8 — Integration tests verify: 201+Location, 409 on duplicate, 200+updated stock, 400 on invalid input, 404 on unknown PUT id
**PASS**
`ProductControllerInventoryIntegrationTest.java` has five tests covering all five scenarios. Verified by `mvn clean verify`: 5 tests run, 0 failures.

### C9 — No integration test relies on hardcoded product id
**PASS**
Tests that need a pre-existing product save via `productRepository.save(...)` and use `saved.getId()` dynamically (lines 91-96, 104). The 404 test uses a deliberately invalid id `9999L` — this is not a hardcoded real id but an intentionally non-existent value, which is acceptable. No test assumes a generated id will have a specific value.

### C10 — ProductService has no overloaded `save` method; operations are distinct methods
**PASS**
`ProductService.java` exposes `create(CreateProductRequest)` and `updateStock(Long, UpdateStockRequest)` — distinct methods with clear intent. No `save` overload exists.

### C11 — No System.out.println or e.printStackTrace() in production code
**PASS**
Grep of `src/main/java` for `System\.out\.println|e\.printStackTrace` returned no matches.

---

## Convention Violations

### V1 — Import ordering in CreateProductRequest.java (FAIL)
File: `src/main/java/com/cne_project/harnessdemo/model/dto/CreateProductRequest.java`, line 11.

Convention: "`java.*`/`jakarta.*` first, followed by external dependencies (Spring, Lombok), and finally local project imports."

The file imports `jakarta.*` (lines 3-6) then `lombok.*` (lines 7-9) and then `java.math.BigDecimal` (line 11). The `java.math.BigDecimal` import must appear BEFORE the `lombok.*` imports (it belongs in the `java.*` group). Fix: move `import java.math.BigDecimal;` to appear before the `jakarta.*` block, or at minimum before the `lombok.*` block.

### V2 — Unjustified class-level Javadoc (pre-existing, noted for completeness)
The following files have class-level Javadoc where the class name and package already fully convey the responsibility (violating the convention "Class-level Javadoc is only allowed if the class name + package do not fully convey its responsibility"):
- `src/main/java/com/cne_project/harnessdemo/service/ProductService.java` line 17-19: `"Service layer responsible for product catalog operations."` — `ProductService` in package `service` is self-descriptive.
- `src/main/java/com/cne_project/harnessdemo/controller/ProductController.java` line 22-24: `"REST controller exposing product catalog and inventory management endpoints."` — self-descriptive.
- `src/main/java/com/cne_project/harnessdemo/repository/ProductRepository.java` line 7-9: `"Spring Data JPA repository for Product entities."` — self-descriptive.

Note: These Javadoc comments predate feature 3 (confirmed via git diff). They were not introduced by the feature-3 implementer. However, the implementer modified these files and had an opportunity to remove them per the "No Javadoc" convention. These are flagged but are not the primary blocker.

---

## Build Result
`mvn clean verify`: **BUILD SUCCESS** — 22 tests run, 0 failures, 0 errors.

---

## Required Changes

1. **[BLOCKING — C7]** Fix `GlobalExceptionHandler.java` `handleValidationErrors` method (line 72-74) to include field names in the validation error message. Change:
   ```java
   .map(FieldError::getDefaultMessage)
   ```
   to:
   ```java
   .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
   ```
   Also update the integration test in `ProductControllerInventoryIntegrationTest.java` (line 128) to assert that the `$.message` value contains the failing field name (e.g., `$.message` contains "name:") to lock in this behavior.

2. **[MINOR — V1]** Fix import ordering in `CreateProductRequest.java`: move `import java.math.BigDecimal;` to appear before the `jakarta.*` imports (line 3), not after them.

3. **[MINOR — V2]** Remove unjustified class-level Javadoc from `ProductService.java` (lines 17-19), `ProductController.java` (lines 22-24), and `ProductRepository.java` (lines 7-9).
