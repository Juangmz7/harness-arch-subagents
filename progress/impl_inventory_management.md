# Implementation: inventory_management (feature 3)

## Files Created

- `src/main/java/com/cne_project/harnessdemo/model/dto/CreateProductRequest.java`
- `src/main/java/com/cne_project/harnessdemo/model/dto/UpdateStockRequest.java`
- `src/main/java/com/cne_project/harnessdemo/model/exception/DuplicateProductNameException.java`
- `src/test/java/com/cne_project/harnessdemo/controller/ProductControllerInventoryIntegrationTest.java`

## Files Modified

- `src/main/java/com/cne_project/harnessdemo/repository/ProductRepository.java` — added `existsByName(String name)`
- `src/main/java/com/cne_project/harnessdemo/service/ProductService.java` — added `create()` and `updateStock()` methods
- `src/main/java/com/cne_project/harnessdemo/controller/ProductController.java` — added `POST /api/products` and `PUT /api/products/{id}/stock`
- `src/main/java/com/cne_project/harnessdemo/controller/GlobalExceptionHandler.java` — added handler for `DuplicateProductNameException → 409`

## Final Test Count

Tests run: 22, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS

## Decisions and Trade-offs

- Used `existsByName` (boolean) in ProductRepository instead of `findByName` (Optional) — sufficient for the duplicate-check and avoids unnecessary object hydration.
- `assertNoDuplicateName` and `buildProduct` extracted as private helpers in ProductService following the compose-method principle.
- `buildLocationUri` extracted in ProductController to keep the handler method clean.
- Integration test class annotated with `@Transactional` so each test rolls back automatically — avoids needing manual cleanup beyond the `@BeforeEach deleteAll()`.
