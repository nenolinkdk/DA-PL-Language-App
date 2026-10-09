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

Phase 1B reconciliation is on branch `vibe/phase1b-reconcile`.

The app keeps the eight-route menu and `AppNavigator`. Niveau 1 opens the imported lessons: lesson 1 is playable, with Danish and Polish speech buttons and a quiz. Lessons 2–10 stay unavailable. Samtaletræning opens two imported scenarios through `DialogueMachine`. Grammatik shows the two imported sheets. Niveau 2, Niveau 3, Børn and the top-level Quiz route stay empty. Speech never starts by itself.

Open the project in Android Studio and run the `app` configuration. From the repository root:

```text
gradlew.bat :core:test :app:assembleDebug :app:testDebugUnitTest
```

Create `local.properties` with `sdk.dir` pointing at the Android SDK. JDK 17 or newer is required; this skeleton is set up for Gradle 9.1 and AGP 9.0.1 so it can run on JDK 25.
