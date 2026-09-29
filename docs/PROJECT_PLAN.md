# DA-PL Language App — Project Plan

**Note:** This is the original project plan, kept for background. The authoritative sources are [DECISIONS.md](DECISIONS.md) and [DEVELOPMENT_SPEC.md](DECISIONS.md) for frozen V1 decisions, and [ROADMAP.md](ROADMAP.md) for the implementation roadmap and phase numbering. Where this document disagrees with them, they win.

## 1. Goal

Create a Danish → Polish Android language app using the same overall Nenoling product family as Learn Portuguese 2 and Learn-FR-DA, with the newer reusable Nenoling architecture as the preferred technical baseline.

The visual structure should resemble Learn-FR-DA: compact menus, clear lesson/module hierarchy, bilingual presentation, TTS controls, previous/next navigation and quiz flow.

## 2. Language roles

- Support language: Danish (`da-DK`)
- Target language: Polish (`pl-PL`)
- Primary learning direction: Danish-speaking learner learning Polish
- Target TTS: Polish
- Support TTS: Danish

## 3. Proposed modules

Reference structure:

1. Level 1 — travel and basic everyday situations
2. Level 2 — everyday life, services and work
3. Level 3 — professional/business communication
4. Children — short, age-appropriate situations
5. Grammar — practical Polish grammar for Danish speakers
6. Practical resources — separate external/reference content
7. Samtaletræning — FSM-driven dialogue practice

Resolved V1 decision (see DECISIONS.md): the interactive dialogue module is a **separate top-level menu item** named **Samtaletræning**, not an activity inside ordinary lessons.

## 4. Content baseline

Use stable IDs from the first production version.

Reference lesson pattern:

- approximately 10 lessons per main teaching level;
- approximately 10 bilingual teaching items per lesson;
- 3 quiz questions per lesson;
- lesson-specific vocabulary, phrases and grammar notes;
- no generic repeated AI-style dialogue fillers.

These are content targets, not engine constraints.

## 5. Polish-specific grammar areas

Initial candidates:

- Polish alphabet and pronunciation;
- grammatical gender;
- personal pronouns;
- present tense;
- past tense;
- future constructions;
- cases introduced progressively;
- nominative, accusative, genitive, instrumental and locative in practical contexts;
- adjective/noun agreement;
- numbers and quantity expressions;
- aspect introduced only when pedagogically useful;
- common prepositions and case government;
- polite forms and address;
- word order differences between Danish and Polish.

**OPEN:** Exact grammar progression requires linguistic review before content production is frozen.

## 6. Proposed Android architecture

Preferred layers:

```text
JSON production content
        ↓ validation
canonical Android assets
        ↓
ContentRepository
        ↓
domain models
        ↓
ViewModel/state holder
        ↓
Compose UI
```

FSM dialogues add:

```text
FSM dialogue JSON
        ↓ validation
DialogueRepository
        ↓
DialogueMachine
(current state + transition rules)
        ↓
DialogueViewModel
        ↓
Compose dialogue screen
```

The FSM must not own Android navigation between app screens. It controls only the internal state of an interactive dialogue.

## 7. UI proposal

Main menu inspired by Learn-FR-DA:

- Level 1
- Level 2
- Level 3
- Samtaletræning
- Quiz / review
- Grammar
- Children
- Documentation / About

Lesson screen:

- lesson title;
- Danish text;
- Polish text;
- Danish and Polish TTS buttons;
- previous/next;
- progress;
- quiz entry.

FSM dialogue screen:

- situation title;
- current speaker/reply;
- optional Danish support translation;
- Polish TTS;
- 2–3 response choices;
- optional hint;
- restart dialogue;
- progress shown as visited steps, not a misleading fixed percentage for branching dialogues.

## 8. Implementation phases

> The Phases A–F below were the original planning outline. Implementation now follows the phase numbering and exit criteria in [ROADMAP.md](ROADMAP.md) (Phases 0–6), which is authoritative.

### Phase A — repository baseline
- README and architecture docs;
- app skeleton;
- Gradle configuration;
- package/application ID;
- theme and main navigation.

### Phase B — standard course engine
- language manifest;
- JSON models;
- module and lesson loading;
- TTS;
- progress;
- quiz;
- Learn-FR-DA-style menus.

### Phase C — first Polish content
- Level 1 pilot;
- one grammar pilot;
- one quiz set;
- Danish/Polish linguistic QA.

### Phase D — FSM prototype
- JSON FSM schema;
- parser and validator;
- deterministic dialogue engine;
- one simple dialogue;
- unit tests;
- Android dialogue UI.

### Phase E — branching content
- 3–5 pilot FSM dialogues;
- error/recovery branches;
- optional hints;
- persistence decision;
- usability test.

### Phase F — full course and release
- Levels 1–3;
- Children;
- Grammar;
- full quiz validation;
- physical-device test;
- signed release.

## 9. Testing requirements

Standard tests:

- JSON/schema validation;
- stable-ID validation;
- Danish/Polish locale checks;
- TTS role checks;
- quiz integrity;
- no answer leakage;
- progress persistence;
- Android build;
- physical-device smoke test.

FSM-specific tests:

- exactly one valid start state;
- all transition targets exist;
- terminal states are explicit;
- no unreachable states unless deliberately marked;
- no accidental infinite loops;
- every non-terminal state has at least one valid transition;
- deterministic transition result for each choice ID;
- restart always returns to start state;
- malformed dialogue fails validation instead of crashing the UI.

## 10. Design principle

The ordinary course must remain useful without FSM dialogues.

FSM is an additional pedagogical interaction model, not a replacement for the existing Nenoling lesson structure.
