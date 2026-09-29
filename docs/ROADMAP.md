# Roadmap

This is the authoritative implementation roadmap. `PROJECT_PLAN.md` is retained as background; where the two disagree, this document wins.

## Phase 0 — Documentation baseline
Status: COMPLETED

- architecture;
- decisions;
- course structure;
- FSM specification;
- navigation model;
- data contracts;
- QA/testing;
- development workflow.

Exit condition met: the Phase 0 documents exist, are internally reconciled, and frozen V1 decisions are recorded in DECISIONS.md. Remaining OPEN items are tracked in OPEN_QUESTIONS.md and do not block Phase 1.

## Phase 1 — Android skeleton
Status: IN PROGRESS

- Gradle/project setup;
- package identity;
- Compose theme;
- FR-DA-inspired menu;
- all top-level module routes;
- clean empty-state handling.

Exit: app installs and every structural menu route is safe.

## Phase 2 — Level 1 vertical slice
- Lesson 1 fully populated;
- bilingual lesson UI;
- DA/PL TTS on button;
- progress;
- quiz;
- validation.

Exit: one complete lesson works end-to-end.

## Phase 3 — FSM vertical slice
- Dialogue JSON;
- validator;
- DialogueMachine;
- Samtaletræning list;
- one scenario;
- hidden Danish support;
- explicit TTS;
- restart;
- FSM unit tests.

Exit: one deterministic scenario works start-to-finish.

## Phase 4 — Complete Level 1
Populate and review all 10 beginner/travel lessons plus selected conversation scenarios.

## Phase 5 — Level 2
Introduce broader everyday content and Polish cases gradually in practical contexts.

**LINGUISTIC REVIEW** required for final progression.

## Phase 6 — Level 3 / Children / broader grammar
Scope refined after Level 1/2 experience.

## Post-V1 candidates
- dialogue history;
- classifications/scoring;
- deterministic difficulty paths;
- weighted transitions;
- adaptive difficulty;
- saved FSM sessions;
- statistics;
- speech recognition.
