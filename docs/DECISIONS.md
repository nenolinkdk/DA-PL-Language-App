# Decision Log

## 2026-09-28 — Initial V1 decisions

| Area | Decision | Status |
|---|---|---|
| Samtaletræning | Separate top-level menu item | V1 |
| Target learner | Danish-speaking absolute/near-absolute beginner; practical Poland/travel use | V1 |
| Danish support in FSM | Hidden initially; reveal/hide on tap | V1 |
| TTS | User presses button; no autoplay | V1 |
| Dialogue display | Current turn only | V1 |
| Full dialogue history | Optional later | Nice-to-have |
| Mid-dialogue persistence | No | V1 |
| Choice classification | No learner-visible classification | V1 |
| Classification/scoring | Possible later | Nice-to-have |
| Polish cases | Systematic introduction begins in Level 2, in small practical steps | V1 content rule |
| Course build | Build complete structural skeleton, fully populate Level 1 first | V1 |
| FSM transitions | Deterministic | V1 |
| Weighted transitions | Documented future option, not implemented initially | Nice-to-have |

## 2026-09-29 — Phase 1A skeleton decisions

| Area | Decision | Status |
|---|---|---|
| Application/package ID | `dk.nenolink.dapl` (frozen at release; approved by product owner) | V1 |
| Language/toolchain | Kotlin 2.0 + Jetpack Compose (Material 3), AGP 8.5, JDK 17 | Superseded 2026-10-09 |
| SDK levels | minSdk 26, target/compile SDK 34 | Superseded 2026-10-09 |
| UI framework | Jetpack Compose with Material 3; compact menu aligned with Learn-FR-DA pattern | V1 |
| Navigation | Navigation-Compose routes for all 8 top-level modules; placeholder empty states for unfilled modules | V1 |

## 2026-10-09 — Phase 1A toolchain and boundaries

Supersedes the toolchain and SDK rows from 2026-09-29. Application ID `dk.nenolink.dapl` is unchanged.

| Area | Decision | Status |
|---|---|---|
| Toolchain | AGP 9.0.1, Gradle 9.1, Kotlin 2.2.10, Compose Material 3. Required so the project can run on JDK 25. | V1 |
| SDK levels | minSdk 26, compileSdk/targetSdk 36. Platform 34 is not installed in the current SDK. | V1 |
| Course index | `content/course/catalog.json` names modules and Level 1 lessons. It is not lesson or dialogue content. | V1 |
| Navigation | `AppNavigator` owns screen changes. It is separate from any future dialogue FSM. Navigation-Compose renders the same routes. | V1 |
| Empty modules | Level 2, Level 3 and Children stay not-yet-filled. Unreleased lessons open a safe placeholder. | V1 |
| TTS | `TtsPolicy` allows speech only after an explicit request. No screen autoplays. | V1 |
| Dialogue engine | Not in Phase 1A. No weighted transitions. | V1 |

`origin/main` contains a separate Android tree (`dk.nenoling.dapl`, lessons and dialogue code). This branch does not replace that tree. Which tree is the product baseline remains open until the two are reconciled.

## Still open

- final Android code baseline: this branch is a new Kotlin/Compose skeleton; `origin/main` has a different Android tree that has not been chosen or discarded;
- exact Level 2 case sequence;
- final scope of Children for first public release;
- final Level 3 content scope;
- exact number/length of FSM scenarios for first release;
- whether Danish support reveal is per turn or remembered during the current scenario.
