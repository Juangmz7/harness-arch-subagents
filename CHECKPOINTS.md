# CHECKPOINTS — Final state evaluation

> In multi-agent systems the path is not evaluated, the destination is.
> These are the objective checkpoints a judge (human or AI) can use
> to decide whether the project is healthy.

## C1 — The harness is complete

- [ ] The 4 base files exist: `AGENTS.md`, `init.sh` (or equivalent setup script), `feature_list.json`, `progress/current.md`.
- [ ] The 3 docs exist: `docs/architecture.md`, `docs/conventions.md`, `docs/verification.md`.
- [ ] `./mvnw clean compile` finishes with exit code 0 (BUILD SUCCESS).

## C2 — The state is consistent

- [ ] At most one feature `in_progress` in `feature_list.json`.
- [ ] Every `done` feature has associated JUnit 5 / MockMvc tests that pass.
- [ ] `progress/current.md` is empty or describes the active session
  (contains no leftover garbage from previous sessions).

## C3 — The code respects the architecture

- [ ] `src/main/java/...` only contains the layers specified in `docs/architecture.md` (Controller, Service, Repository, Model/Entity, DTO, Exception).
- [ ] No unapproved external dependencies in `pom.xml` or `build.gradle` (stick to standard Spring Boot starters, Lombok, MapStruct).
- [ ] No stray `System.out.println()` calls (must use SLF4J loggers), no TODOs without context.

## C4 — Verification is real

- [ ] `src/test/java/...` has at least one test class per component in the main source tree.
- [ ] API integration tests use the real in-memory H2 database context (`@SpringBootTest`), not database mocks.
- [ ] `./mvnw test` shows > 0 tests executed, all green (no failures, no errors).

## C5 — The session was closed properly

- [ ] No suspicious untracked files (`*.class` files, or `target/` / `build/` directories outside of `.gitignore`).
- [ ] `progress/history.md` has an entry for the last session.
- [ ] The last feature worked on is reflected in its correct status.

---

**How to use this file:** a reviewer agent (`qa-reviewer`)
goes through each checkbox, marks `[x]` or `[ ]`, and rejects the session
close if any boxes remain unchecked in C1-C5.