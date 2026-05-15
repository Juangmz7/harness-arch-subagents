# AGENTS.md — Navigation map for AI agents

> This file is the **entry point** for any agent working in this
> repository. It is NOT a rulebook: it is a **map**. Read only what you
> need when you need it (progressive disclosure).

---

## 1. Before you start (mandatory)

📍 Environment Note (Windows):
For .sh scripts (like init.sh): Strictly use Git Bash to execute them. Do not use PowerShell or WSL for .sh files.
For everything else: Continue using PowerShell normally for Maven (mvn), Git, and general system commands.

1. Run `./init.sh` and verify it finishes without errors. If it fails, **stop**
   and fix the environment before touching code.
2. Read `progress/current.md` to understand the state the last session left off at.
3. Read `feature_list.json` and pick **one** task with status `pending`. Do not
   work on more than one at a time.

## 2. Repository map

| File / folder                   | What it contains                                                  | When to read it |
|---------------------------------|-------------------------------------------------------------------|-----------------|
| `feature_list.json`             | Task list with status (pending / in_progress / done)              | Always, at the start |
| `progress/current.md`           | State of the current session                                      | Always, at the start |
| `progress/history.md`           | Append-only log of previous sessions                              | If you need historical context |
| `docs/architecture.md`          | What "doing good work" means in this project                      | Before implementing |
| `docs/conventions.md`           | Style rules, naming, structure                                    | Before writing code |
| `docs/verification.md`          | How to verify your work functions                                 | Before declaring a task `done` |
| `CHECKPOINTS.md`                | Objective criteria for "correct final state"                      | For self-evaluation |
| `.claude/agents/`               | Subagent definitions (leader, implementer, qa-reviewer, explorer) | If orchestrating work |
| `scripts/demo_orchestration.py` | Demo of the Leader-Worker pattern with disk writes                | To understand the broken-telephone rule |
| `src/main/java`                 | Application code                                                  | To implement |
| `src/test/java`                 | Automated tests                                                   | To verify |

## 3. Hard rules (non-negotiable)

- **One feature at a time.** Do not mix changes from multiple tasks in the same session.
- **Do not declare a task `done` without green tests.** Run `./init.sh` and
  make sure the test block passes 100%.
- **Document what you do** in `progress/current.md` as you work, not at the end.
- **Leave the repository clean** before closing the session (see §5).
- **If you don't know something, look in `docs/`** before making it up.

## 4. How to pick a task

```
1. Open feature_list.json
2. Filter by status == "pending"
3. Take the one with the lowest "id"
4. Change its status to "in_progress" and save
5. Note in progress/current.md: feature, start time, brief plan
```

## 5. Session close (lifecycle)

Before finishing:

1. Run `./init.sh` — all green.
2. If the task is done: mark `status: "done"` in `feature_list.json`.
3. Move the summary from `progress/current.md` to the end of `progress/history.md`.
4. Empty `progress/current.md` leaving only the template.
5. Leave no temp files, no debug `print()` calls, no TODOs without context.

## 6. If you get stuck

- Reread the relevant section of `docs/`.
- If the tool does not do what you expect, **do not invent a workaround**:
  document the blocker in `progress/current.md` and stop the session.