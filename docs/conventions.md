# Code Conventions

> Extreme homogeneity. AI predicts better when the repository looks
> like itself everywhere.

## Python Style

- **Version:** Python 3.9+ (`list[str]` syntax allowed).
- **Formatting:** PEP 8. Maximum 100 characters per line.
- **Imports:** stdlib first, then local. One module per line.
- **Strings:** double quotes `"..."` always. Single quotes only
  to escape double quotes within a string.
- **f-strings** for interpolation. No `.format()` or `%`.

## Naming

| Type                    | Convention        | Example               |
|-------------------------|-------------------|-----------------------|
| Modules                 | `snake_case`      | `notes.py`            |
| Classes                 | `PascalCase`      | `Note`                |
| Functions / variables   | `snake_case`      | `load_notes`          |
| Constants               | `UPPER_SNAKE`     | `DEFAULT_NOTES_PATH`  |
| Private                 | `_` prefix        | `_atomic_write`       |

## File Structure

Every file in `src/` starts with:

```python
"""One line describing the module's purpose."""
from __future__ import annotations

# stdlib imports
import json
import os

# local imports
from src.notes import Note
```

## Tests

- One test file per module: `tests/test_<module>.py`.
- One `Test<Thing>(unittest.TestCase)` class per logical unit.
- Each test uses a `tempfile.TemporaryDirectory()` and cleans up after itself.
- Descriptive test names: `test_load_returns_empty_when_file_missing`.

## Error Handling

Domain exceptions in `src/notes.py`:

```python
class NoteError(Exception):
    """Base for domain errors."""

class NoteNotFound(NoteError):
    """Raised when a nonexistent note is looked up."""
```

The CLI catches domain exceptions, prints a message to `stderr`, and exits
with code 1. Never propagates stack traces to the user.

## Comments

By default, **not** written. Only allowed when explaining a non-obvious *why*
(e.g. documented workaround, subtle invariant). Names should do the rest.