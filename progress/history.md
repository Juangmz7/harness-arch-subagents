# Historical log (append-only)

> Each time a session is closed, its summary is appended here.
> Do not edit previous entries. Only add at the end.

---

## 2026-04-20 — Project bootstrap
- **Agent:** human (Juan)
- **Changes:** initial harness structure (AGENTS.md, feature_list.json, docs/).
- **Result:** environment ready. `./init.sh` green.

---

## 2026-05-15 — Feature 1: product_catalog (Product Catalog API)
- **Agent:** leader + implementer-domain + implementer-infra + qa-reviewer
- **Branch:** working-on-product_catalog
- **Changes:** Product entity, ProductRepository, DomainException/ResourceNotFoundException, ProductService + impl, ProductDTO record, ProductMapper (MapStruct), ProductController, GlobalExceptionHandler, ErrorResponse; ProductServiceTest (3 unit tests), ProductControllerIntegrationTest (4 integration tests). Added MapStruct 1.6.3 to pom.xml.
- **Tests:** 8 total, 0 failures
- **QA:** APPROVED — all 11 acceptance criteria verified
- **PR:** https://github.com/Juangmz7/Harness-subagent/pull/6
- **Result:** feature complete; `./init.sh` green.