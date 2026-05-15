````markdown
# harness-arch-subagents — E-commerce Spring Boot API

Example project demonstrating **Harness Engineering** principles
applied to a minimalist e-commerce REST API in Java + Spring Boot.

> The application code is deliberately simple. What matters about
> this repo is not **what** it does, but **how** it is structured so that an
> AI agent can work on it autonomously and verifiably.

## How the harness is organized

| Pillar | Manifestation in this repo |
|--------|----------------------------|
| **1. The repository IS the system** | `AGENTS.md`, `init.sh`, `feature_list.json`, `progress/`, `docs/` |
| **2. Multi-agent orchestration** | `.claude/agents/leader.md`, `implementer.md`, `qa-reviewer.md` |
| **3. Supervision and improvement** | `CHECKPOINTS.md`, hooks in `CLAUDE.md`, `src/test/java/` |

## Getting started

> **Windows note:** use Git Bash to run `.sh` scripts. Use PowerShell for Maven, Git, and general commands.

```bash
./init.sh
```

If everything is green, open `AGENTS.md` and follow from there.

## Using the app (humans)

```bash
# Build and run
./mvnw spring-boot:run

# Run tests
./mvnw test

# Full clean build
./mvnw clean compile
```

The API is available at `http://localhost:8080`. Key endpoints:

```
GET  /api/products          # List all products
GET  /api/products/{id}     # Get product by id
POST /api/orders            # Create an order
```

## Try it yourself with Claude Code

If you clone the repo and open Claude Code at the root, you are already inside the
harness: `CLAUDE.md` forces the model to act as `leader` (orchestrates, does not
edit code).

Quick recipe:

1. `./init.sh` — must finish green.
2. Open `feature_list.json` and leave at least one feature with `status: "pending"`.
   If all are `done`, add a new one at the end of the array or change an existing
   status to reopen it.
3. Launch Claude Code at the repo root: `claude`.
4. Tell it literally: **"implement the next pending feature"**.

What you will see in chat:

- The **leader** announces the plan, launches an `implementer`, then a `qa-reviewer`.
- **No code passes through chat** — only references like
  `done -> progress/impl_<feature>.md`. That is the broken-telephone rule.

Where each subagent's trace lives (this is the persistent "visualization"):

| File | Written by | Contains |
|------|------------|----------|
| `progress/current.md` | leader | Live session plan |
| `progress/impl_<feature>.md` | implementer | Files touched + test output |
| `progress/qa-review_<feature>.md` | qa-reviewer | Checklist against `docs/` and `CHECKPOINTS.md` |
| `progress/explore_<topic>.md` | explorer | Research results for scoped questions |
| `feature_list.json` | implementer | `pending` → `in_progress` → `done` |
| `progress/history.md` | leader | Append-only summary on session close |

Open `progress/` in your editor while Claude works: each report appears
as soon as the subagent finishes. This lets you audit step by step who decided
what — the content does not flow through chat, it lives on disk and stays versioned.

## Structure

```
.
├── AGENTS.md                    # Map for agents (progressive disclosure)
├── CHECKPOINTS.md               # Criteria for "correct final state"
├── CLAUDE.md                    # Forces Claude to act as leader on session start
├── feature_list.json            # Scope: one feature at a time
├── init.sh                      # Verification and initialization
├── pom.xml                      # Maven build descriptor
├── progress/
│   ├── current.md               # Active session (live state)
│   ├── history.md               # Append-only log
│   ├── impl_<feature>.md        # Implementer traces
│   ├── qa-review_<feature>.md   # Reviewer traces
│   └── explore_<topic>.md       # Explorer research traces
├── docs/
│   ├── architecture.md          # What "good work" means
│   ├── conventions.md           # Style, naming, structure
│   └── verification.md          # How to prove it works
├── .claude/
│   └── agents/                  # Leader, implementer, qa-reviewer definitions
├── scripts/
│   └── demo_orchestration.py    # Demo of the Leader-Worker pattern with disk writes
└── src/
    ├── main/java/               # Spring Boot application (Controller/Service/Repository)
    └── test/java/               # JUnit 5 + MockMvc integration tests
```

## What this project illustrates

- **Progressive disclosure** in `AGENTS.md`: the agent does not receive all the
  rules at once — it gets a map to look them up on demand.
- **One feature at a time** enforced by `init.sh` (rejects more than one
  `in_progress` in `feature_list.json`).
- **State on disk**, not in chat: `progress/current.md` and `history.md`
  survive restarts and blown context windows.
- **Executable verification**: `init.sh` runs the real Maven tests, it does not trust
  what the agent says.
- **Leader-Worker-Reviewer pattern**: the leader does not implement, the
  implementer does not self-approve, the reviewer does not edit code.
- **Anti broken-telephone**: subagents write their results to files and only
  return a lightweight reference.
- **Strict 3-layer architecture**: Controller → Service → Repository; no business
  logic leaks across layers, enforced by `docs/architecture.md` and reviewed by
  the `qa-reviewer` against `CHECKPOINTS.md`.
````
