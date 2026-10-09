# Planned Repository Structure

This is the target structure for implementation. Directories are created when their first real file is added; Git does not retain empty directories.

```text
DA-PL-Language-App/
├─ README.md
├─ content/
│  ├─ config/
│  │  └─ manifest.json
│  ├─ course/
│  │  ├─ catalog.json
│  │  ├─ level1/
│  │  └─ grammar/
│  └─ dialogues/
├─ core/
│  └─ src/
├─ docs/
│  ├─ README.md
│  ├─ DEVELOPMENT_SPEC.md
│  ├─ ARCHITECTURE.md
│  ├─ NAVIGATION_FSM.md
│  ├─ COURSE_STRUCTURE.md
│  ├─ LEVEL1_CONTENT_PLAN.md
│  ├─ FSM_DIALOGUES.md
│  ├─ WEIGHTED_FSM.md
│  ├─ DATA_MODEL.md
│  ├─ TESTING_QA.md
│  ├─ DEVELOPMENT_WORKFLOW.md
│  ├─ ACCEPTANCE_CRITERIA.md
│  ├─ ROADMAP.md
│  ├─ DECISIONS.md
│  ├─ OPEN_QUESTIONS.md
│  ├─ PROJECT_PLAN.md
│  └─ RISKS.md

Document status vocabulary (V1 / NICE-TO-HAVE / OPEN / LINGUISTIC REVIEW) is defined in OPEN_QUESTIONS.md. The authoritative sources are DECISIONS.md and DEVELOPMENT_SPEC.md for frozen V1 decisions, ROADMAP.md for the implementation roadmap.
├─ app/
│  └─ src/main/
│     ├─ java/.../
│     │  ├─ data/
│     │  ├─ domain/
│     │  ├─ ui/
│     │  ├─ tts/
│     │  └─ validation/
│     └─ assets/
│        ├─ config/
│        ├─ course/
│        │  ├─ level1/
│        │  ├─ level2/
│        │  ├─ level3/
│        │  ├─ children/
│        │  └─ grammar/
│        └─ dialogues/
├─ tools/
│  └─ validation/
└─ tests/
   └─ content/
```

## Content-source rule

Before production grows, decide whether human-edited production JSON lives directly under Android assets or in a separate source folder and is generated/copied into assets.

Preferred long-term pattern:

```text
content/production -> validation/build step -> app/src/main/assets
```

This reduces the risk of manually correcting generated assets only.

**OPEN:** implement this split immediately or after the first vertical slice.
