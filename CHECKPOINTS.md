# CHECKPOINTS — Final state evaluation

> In multi-agent systems the path is not evaluated, the destination is.
> These are the objective checkpoints a judge (human or AI) can use
> to decide whether the project is healthy.

## C1 — The harness is complete

- [ ] The 4 base files exist: `AGENTS.md`, `init.sh`, `feature_list.json`,
  `progress/current.md`.
- [ ] The 3 docs exist: `docs/architecture.md`, `docs/conventions.md`,
  `docs/verification.md`.
- [ ] `./init.sh` finishes with exit code 0.

## C2 — The state is consistent

- [ ] At most one feature `in_progress` in `feature_list.json`.
- [ ] Every `done` feature has associated tests that pass.
- [ ] `progress/current.md` is empty or describes the active session
  (contains no leftover garbage from previous sessions).

## C3 — The code respects the architecture

- [ ] `src/` only contains the modules specified in `docs/architecture.md`.
- [ ] No external dependencies in `requirements.txt` (must be empty
  or not exist).
- [ ] No stray debug `print()` calls, no TODOs without context.

## C4 — Verification is real

- [ ] `tests/` has at least one test per module in `src/`.
- [ ] Tests use `tempfile.TemporaryDirectory()`, not fs mocks.
- [ ] `python3 -m unittest discover -s tests -v` shows > 0 tests,
  all green.

## C5 — The session was closed properly

- [ ] No suspicious untracked files (`*.tmp`, `__pycache__`
  outside `.gitignore`).
- [ ] `progress/history.md` has an entry for the last session.
- [ ] The last feature worked on is reflected in its correct status.

---

**How to use this file:** a reviewer agent (`.claude/agents/reviewer.md`)
goes through each checkbox, marks `[x]` or `[ ]`, and rejects the session
close if any boxes remain unchecked in C1-C5.