# DA-PL Language App

Android language-learning app for **Danish → Polish**, built on the reusable Nenoling architecture.

## Baseline

This project should follow the current Nenoling template and the Learn-FR-DA user-interface pattern rather than cloning one older app literally.

Planned baseline:

- Android, Kotlin/Jetpack Compose unless implementation review shows a stronger reason to reuse an existing Java layer;
- offline-first linguistic content in JSON;
- Danish as support language (`da-DK`);
- Polish as target language (`pl-PL`);
- support/target TTS;
- Levels 1–3;
- Children module;
- Grammar module;
- quizzes and progress;
- compact menus and navigation visually aligned with Learn-FR-DA;
- new optional FSM-based interactive dialogues.

## FSM dialogue experiment

DA-PL introduces a data-driven dialogue format based on finite-state machines (FSMs).

A dialogue contains:

- one start state;
- dialogue states;
- bilingual utterances;
- learner choices;
- transitions to the next state;
- one or more terminal states.

The FSM is content data, not Android screen-navigation logic. The Android engine renders the current dialogue state and applies the selected transition.

See:

- [Project plan](docs/PROJECT_PLAN.md)
- [FSM dialogue design](docs/FSM_DIALOGUES.md)
- [Open questions](docs/OPEN_QUESTIONS.md)

## Status

Initial documentation/planning stage. The repository was empty when this baseline was created.
