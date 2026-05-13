# Verification — How to prove the work functions

> Golden rule: **the agent does not say "it works", it proves it**.
> Every feature ends with executable evidence, not assertions.

## Verification Levels

### Level 1 — Unit tests (mandatory)

Every public function in `src/` has at least one test in `tests/` that:

1. Covers the happy path.
2. Covers at least one error path if the function can fail.

Command:
```bash
python3 -m unittest discover -s tests -v
```

### Level 2 — CLI integration test (mandatory for UI features)

Features that add commands to the CLI are verified by running the real CLI
against a temporary file:

```python
import subprocess, tempfile, os
with tempfile.TemporaryDirectory() as d:
    env = {**os.environ, "NOTES_FILE": os.path.join(d, "notes.json")}
    out = subprocess.check_output(
        ["python3", "-m", "src.cli", "add", "hello", "--body", "world"],
        env=env, text=True,
    )
    assert "id=" in out
```

### Level 3 — Manual smoke test (optional but recommended)

Before closing the session, run an end-to-end flow with a temporary file
in `/tmp`:

```bash
NOTES_FILE=/tmp/notes_demo.json python3 -m src.cli add "test" --body "x"
NOTES_FILE=/tmp/notes_demo.json python3 -m src.cli list
rm /tmp/notes_demo.json
```

## Anti-patterns (do not do)

- ❌ "I added the command, it should work." → missing executable test.
- ❌ Test that only checks the function doesn't raise an exception. → it must
  verify the concrete result.
- ❌ `mock` the filesystem. → use a real `tempfile.TemporaryDirectory()`.
- ❌ Mark the feature as `done` without running `./init.sh`.

## Final check before closing

```bash
./init.sh           # must finish with [OK] Environment ready
```

If `./init.sh` is red, do **not** mark anything as `done`. Log the blocker
in `progress/current.md` with status `blocked` in `feature_list.json`.