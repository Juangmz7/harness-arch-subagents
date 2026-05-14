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