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
| Language/toolchain | Kotlin 2.0 + Jetpack Compose (Material 3), AGP 8.5, JDK 17 | V1 |
| SDK levels | minSdk 26, target/compile SDK 34 | V1 |
| UI framework | Jetpack Compose with Material 3; compact menu aligned with Learn-FR-DA pattern | V1 |
| Navigation | Navigation-Compose routes for all 8 top-level modules; placeholder empty states for unfilled modules | V1 |

## Still open

- final Android code baseline: clean Kotlin/Compose skeleton vs direct reuse/port of reference implementation;
- exact Level 2 case sequence;
- final scope of Children for first public release;
- final Level 3 content scope;
- exact number/length of FSM scenarios for first release;
- whether Danish support reveal is per turn or remembered during the current scenario.
