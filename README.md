# harness-arch-subagents — E-commerce Spring Boot API

A minimalist e-commerce REST API in Java + Spring Boot, structured so an AI agent can work on it autonomously and verifiably.

## Prerequisites

- Java 17+
- Maven 3.8+ (or use the included `./mvnw` wrapper)
- [Claude Code](https://docs.anthropic.com/en/docs/claude-code) (for the agentic workflow)
- **Windows users:** run `.sh` scripts in Git Bash; use PowerShell for Maven and Git

## Getting started

```bash
./init.sh
```

If everything is green, open `AGENTS.md` and follow from there.

## Using the app

```bash
# Build and run
./mvnw spring-boot:run

# Run tests
./mvnw test

# Full clean build
./mvnw clean compile
```

API available at `http://localhost:8080`:

```
GET  /api/products          # List all products
GET  /api/products/{id}     # Get product by id
POST /api/orders            # Create an order
```

## Harness structure

| Pillar | Manifestation |
|--------|---------------|
| **Repository IS the system** | `AGENTS.md`, `init.sh`, `feature_list.json`, `progress/`, `docs/` |
| **Multi-agent orchestration** | `.claude/agents/leader.md`, `implementer.md`, `qa-reviewer.md` |
| **Supervision and improvement** | `CHECKPOINTS.md`, hooks in `CLAUDE.md`, `src/test/java/` |

Key design decisions:

- **One feature at a time** — `init.sh` rejects more than one `in_progress` entry in `feature_list.json`
- **State on disk, not in chat** — `progress/` files survive restarts and blown context windows
- **Leader-Worker-Reviewer** — the leader does not implement, the implementer does not self-approve, the reviewer does not edit code
- **No broken telephone** — subagents write results to files and return only a lightweight reference
- **Strict 3-layer architecture** — Controller → Service → Repository, enforced by `docs/architecture.md` and reviewed against `CHECKPOINTS.md`
- **Progressive disclosure** — `AGENTS.md` gives agents a map to look up rules on demand, not all at once

## Using with Claude Code

1. `./init.sh` — must finish green
2. Open `feature_list.json` and ensure at least one feature has `status: "pending"` (add one or reopen an existing entry if all are `done`)
3. Launch Claude Code at the repo root: `claude`
4. Say: **"implement the next pending feature"**

What happens:

- The **leader** announces the plan, then launches an `implementer`, then a `qa-reviewer`
- No code passes through chat — only file references like `done -> progress/impl_<feature>.md`

## Progress files

| File | Written by | Contains |
|------|------------|----------|
| `progress/current.md` | leader | Live session plan |
| `progress/impl_<feature>.md` | implementer | Files touched + test output |
| `progress/qa-review_<feature>.md` | qa-reviewer | Checklist against `docs/` and `CHECKPOINTS.md` |
| `progress/explore_<topic>.md` | explorer | Research results for scoped questions |
| `feature_list.json` | implementer | `pending` → `in_progress` → `done` |
| `progress/history.md` | leader | Append-only summary on session close |

Open `progress/` in your editor while Claude works — each report appears as soon as a subagent finishes, and everything stays versioned.

## Repository structure

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
