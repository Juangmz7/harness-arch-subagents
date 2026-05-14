# Implementation Summary — product_catalog (id: 1)

## Status
All tests pass: 9/9 (BUILD SUCCESS)

## Files Created

### Production Code
- `src/main/java/com/cne_project/harnessdemo/model/exception/DomainException.java` — Base unchecked exception for all domain errors
- `src/main/java/com/cne_project/harnessdemo/model/exception/ResourceNotFoundException.java` — Extends DomainException; thrown when a resource is not found by id
- `src/main/java/com/cne_project/harnessdemo/model/entity/Product.java` — JPA entity with `@Entity`, `@Table(name="products")`, and explicit `@Column` names on all fields
- `src/main/java/com/cne_project/harnessdemo/model/dto/ProductDTO.java` — Java Record with fields: `Long id, String name, String description, BigDecimal price, int stock`
- `src/main/java/com/cne_project/harnessdemo/model/dto/ErrorResponseDTO.java` — Java Record for structured error responses (timestamp, status, error, message, path)
- `src/main/java/com/cne_project/harnessdemo/repository/ProductRepository.java` — Extends `JpaRepository<Product, Long>`, no custom queries
- `src/main/java/com/cne_project/harnessdemo/service/ProductService.java` — `findAll()` and `findById()` both annotated `@Transactional(readOnly = true)`; manual entity-to-DTO mapping
- `src/main/java/com/cne_project/harnessdemo/controller/ProductController.java` — `GET /api/products` and `GET /api/products/{id}`; delegates entirely to ProductService
- `src/main/java/com/cne_project/harnessdemo/controller/GlobalExceptionHandler.java` — `@RestControllerAdvice` handling `ResourceNotFoundException` → HTTP 404 with structured JSON

### Test Code
- `src/test/java/com/cne_project/harnessdemo/service/ProductServiceTest.java` — 4 Mockito-only unit tests covering findAll (with and without results), findById (found), findById (not found → ResourceNotFoundException)
- `src/test/java/com/cne_project/harnessdemo/controller/ProductControllerIntegrationTest.java` — 4 integration tests using `@SpringBootTest` + `MockMvc` + H2 (empty list returns `[]`, list with products, get by id, get by missing id → 404 with JSON body)

## Key Decisions
- Spring Boot 4.0 changed `@AutoConfigureMockMvc` package from `org.springframework.boot.test.autoconfigure.web.servlet` to `org.springframework.boot.webmvc.test.autoconfigure`; corrected accordingly
- Manual mapping used instead of MapStruct (no additional dependency needed)
- `ErrorResponseDTO` placed in `model/dto` to keep the package structure clean
- `GlobalExceptionHandler` placed in `controller` package per conventions (it handles HTTP concerns)
- No `System.out.println` anywhere; SLF4J used for all logging
