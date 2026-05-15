# Inventory Fixes Implementation

## Fixes Applied

### Fix 1 — BLOCKING (C7): Include field names in validation error messages
File: `src/main/java/com/cne_project/harnessdemo/controller/GlobalExceptionHandler.java`
Changed `.map(FieldError::getDefaultMessage)` to `.map(fe -> fe.getField() + ": " + fe.getDefaultMessage())` in `handleValidationErrors`.

### Fix 2 — BLOCKING (C7 test): Update integration test assertion
File: `src/test/java/com/cne_project/harnessdemo/controller/ProductControllerInventoryIntegrationTest.java`
Updated `createProduct_shouldReturn400_whenInvalidFields` to assert `$.message` contains "name" using `org.hamcrest.Matchers.containsString("name")`.

### Fix 3 — MINOR (V1): Fix import ordering in CreateProductRequest.java
File: `src/main/java/com/cne_project/harnessdemo/model/dto/CreateProductRequest.java`
Moved `import java.math.BigDecimal;` before `jakarta.*` imports.

### Fix 4 — MINOR (V2): Remove unjustified class-level Javadoc
- `src/main/java/com/cne_project/harnessdemo/service/ProductService.java` — removed Javadoc block
- `src/main/java/com/cne_project/harnessdemo/controller/ProductController.java` — removed Javadoc block
- `src/main/java/com/cne_project/harnessdemo/repository/ProductRepository.java` — removed Javadoc block

## Final mvn clean test result

Tests run: 22, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS
