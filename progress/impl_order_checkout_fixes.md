# Order Checkout Fixes — Implementation Summary

## Fix 1 — AC10: Add @Transactional to OrderControllerIntegrationTest

**File:** `src/test/java/com/cne_project/harnessdemo/controller/OrderControllerIntegrationTest.java`

- Added `import org.springframework.transaction.annotation.Transactional`
- Added `@Transactional` at class level (webEnvironment is MOCK, so class-level rollback works)
- Also updated the 422 test assertion from `"Unprocessable Entity"` to `"Unprocessable Content"` (the actual phrase returned by Spring's `HttpStatus.valueOf(422).getReasonPhrase()`)

## Fix 2 — V1: Removed unjustified Javadoc

Removed `/** ... */` class-level Javadoc blocks from:

- `src/main/java/com/cne_project/harnessdemo/model/entity/OrderStatus.java`
- `src/main/java/com/cne_project/harnessdemo/model/exception/DomainException.java`
- `src/main/java/com/cne_project/harnessdemo/model/exception/ResourceNotFoundException.java`
- `src/main/java/com/cne_project/harnessdemo/model/dto/ErrorResponseDTO.java`
- `src/main/java/com/cne_project/harnessdemo/model/dto/OrderItemDTO.java`
- `src/main/java/com/cne_project/harnessdemo/controller/OrderController.java`
- `src/main/java/com/cne_project/harnessdemo/model/entity/Order.java`
- `src/main/java/com/cne_project/harnessdemo/model/entity/OrderItem.java`
- `src/main/java/com/cne_project/harnessdemo/controller/GlobalExceptionHandler.java`

## Fix 3 — V2: Replace deprecated HttpStatus.UNPROCESSABLE_ENTITY

**File:** `src/main/java/com/cne_project/harnessdemo/controller/GlobalExceptionHandler.java`

- Replaced all occurrences of `HttpStatus.UNPROCESSABLE_ENTITY` with `HttpStatus.valueOf(422)`
- No new import needed (`HttpStatus` was already imported)
- Note: `HttpStatus.valueOf(422).getReasonPhrase()` returns `"Unprocessable Content"` (RFC 9110), so the test assertion was updated accordingly

## Verification

```
mvn clean test
Tests run: 17, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

All 17 tests pass.
