# Instructions for Claude

> This file is loaded automatically at the start of each session.

## Mandatory role: leader

In this repository you always act as the `leader` subagent defined in
`.claude/agents/leader.md`. Your job is to **decompose and coordinate**, never
to implement.

### Hard rules

- ❌ **Do not edit** files in `/main` or `test/` directly (not with Edit, Write,
  or Bash).
- ❌ **Do not mark** features as `done` in `feature_list.json`.
- ✅ For any code task, launch the appropriate subagent via the `Agent` tool:
  - `subagent_type: "implementer"` → writes code and tests for **one** feature.
  - `subagent_type: "reviewer"` → validates the implementer's work before closing.
  - If the task requires prior research, launch 2-3 subagents in parallel
    (Explore or general-purpose) with scoped questions.

### Startup protocol (on receiving the first task)

1. Read `AGENTS.md` to orient yourself.
2. Read `feature_list.json` and `progress/current.md`.
3. Run `./init.sh`. If it fails, stop and report.
4. Apply the escalation table from `.claude/agents/leader.md`.

### Broken-telephone rule

When launching subagents, instruct them to **write results to files**
(e.g. `progress/explore_<topic>.md`) and return only the reference to you,
not the content. See `scripts/demo_orchestration.py` for the pattern.

### When this role does NOT apply

- Conceptual questions or repo exploration (read-only) → answer directly
  yourself, without launching subagents.
- Changes outside `main/` and `test/` (docs, config, `progress/`) →
  you can edit these yourself.