# Review — feature 2 (order_checkout)

**Verdict:** CHANGES_REQUESTED

## Checkpoints

- C1: [x] — 4 base files exist; 3 docs exist; `mvn clean verify` exits with BUILD SUCCESS.
- C2: [x] — Exactly one feature `in_progress` (`order_checkout`); every `done` feature (product_catalog) has passing tests; `progress/current.md` reflects the active session only.
- C3: [ ] — See violations below. Unused import and import-ordering violations in `OrderController.java` and `GlobalExceptionHandler.java`. No unapproved external deps; no `System.out` or TODOs.
- C4: [x] — 17 tests, 0 failures. One test class per component. Integration tests use `@SpringBootTest` + real H2 database.
- C5: [ ] — `progress/history.md` has no entry for this session; `feature_list.json` still shows `order_checkout` as `in_progress`. (Expected pre-close state — not a blocker if addressed at session close.)

## Required Changes

1. **`src/main/java/com/cne_project/harnessdemo/controller/OrderController.java`, line 5** — Remove the unused import `org.springframework.validation.annotation.Validated`. It is imported but never applied anywhere in the file. Unused imports are forbidden.

2. **`src/main/java/com/cne_project/harnessdemo/controller/OrderController.java`, lines 4-13** — Fix import ordering. `jakarta.validation.Valid` (line 11) must appear before Spring framework imports (lines 4-9). Required order: `java.*`/`jakarta.*` first, then framework/library imports (Spring, Lombok), then local project imports. After removing the unused `Validated` import and reordering, the block should read:
   ```
   import jakarta.validation.Valid;
   
   import lombok.RequiredArgsConstructor;
   import lombok.extern.slf4j.Slf4j;
   import org.springframework.http.HttpStatus;
   import org.springframework.http.ResponseEntity;
   import org.springframework.web.bind.annotation.PostMapping;
   import org.springframework.web.bind.annotation.RequestBody;
   import org.springframework.web.bind.annotation.RequestMapping;
   import org.springframework.web.bind.annotation.RestController;
   ```

3. **`src/main/java/com/cne_project/harnessdemo/config/exception/GlobalExceptionHandler.java`, lines 8-13** — Fix import ordering. `jakarta.servlet.http.HttpServletRequest` (line 13) must appear before the Spring imports (lines 8-11). Required order: `java.*`/`jakarta.*` first (which here means `java.time.Instant`, `java.util.stream.Collectors`, and `jakarta.servlet.http.HttpServletRequest` together), then Spring framework imports, then local project imports.

4. **`src/test/java/com/cne_project/harnessdemo/controller/OrderControllerIntegrationTest.java`** — Rename the file and class to `OrderControllerIT` to match the naming convention defined in `docs/conventions.md`: "Controller slice tests ... Named `<Controller>IT.java`."
