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

`origin/main` still contains the earlier Android tree (`dk.nenoling.dapl`). This branch does not delete it. The reconciled app lives here, under `dk.nenolink.dapl`.

## 2026-10-09 — Phase 1B reconciliation

The canonical app is the Phase 1A codebase plus the lesson, grammar and dialogue files imported from `main` at `b846beb`. Those files are copied unchanged.

| Area | Decision | Status |
|---|---|---|
| Package | `dk.nenolink.dapl` remains the application ID. `dk.nenoling.dapl` is not used. | V1 |
| Content files | `content/course/level1`, `content/course/grammar` and `content/dialogues` keep the imported Danish and Polish text. | V1 |
| Lesson index | The catalog uses the imported ids and titles (`lesson-01` “Basale hilsner” through `lesson-10`). The course-plan titles in COURSE_STRUCTURE.md stay documentation until the content is rewritten. | V1 |
| File module id | Lesson files keep `"moduleId": "level1"`. The catalog module id remains `module-level1`. | V1 |
| Dialogue | `DialogueMachine` and `DialogueValidator` live in `:core`. They are deterministic and have no weights. Leaving a scenario drops the machine. | V1 |
| Progress | Completed lesson and dialogue ids are stored in SharedPreferences. An unfinished dialogue turn is not stored. | V1 |
| TTS | Speech still requires `TtsPolicy`. Buttons pass `requestedByUser = true`. If da-DK or pl-PL is missing, the gateway falls back to US English only after that call. | V1 |
| Empty modules | Level 2, Level 3, Children and the top-level Quiz route stay unavailable. Lessons 02–10 stay unreleased. | V1 |
| Preserved manifest | `content/config/manifest.json` is kept and is not the menu source. | V1 |

## 2026-10-11 — Phase 1C levels 1 and 2

Level 1 follows the course-plan titles. Level 2 is released as ten everyday lessons. Café and ticket remain, and four travel scenarios were added. Polish in this phase is draft text and still needs linguistic review.

| Area | Decision | Status |
|---|---|---|
| Level 1 | `lesson-01` through `lesson-10` are released. Each has 12 phrases, a short word list, Danish notes and six quiz questions. | V1 content, linguistic review required |
| Level 2 | `lesson-l2-01-routine` through `lesson-l2-10-plans` are released. Grammar appears in examples: pronouns, present tense, gender, accusative, `do`/`na`/`w`, requests, and a few past and future phrases. | V1 content, linguistic review required |
| File module id | Lesson files use `level1` or `level2`. Catalog module ids stay `module-level1` and `module-level2`. | V1 |
| Empty modules | Level 3, Children and the top-level Quiz route stay unavailable. | V1 |
| Dialogue | Six deterministic scenarios. No weights. `polish` is the spoken line. `support` is Danish and stays hidden until the learner taps. The choice lasts for that visit and is dropped on exit. | V1 |
| Ticket closing line | The imported terminal `Udowa życzenia!` is replaced by `Miłej podróży!`. | V1 content, linguistic review required |
| Progress | A passed lesson quiz stores the lesson id. `Gem og luk` stores the scenario id. An unfinished turn is not stored. | V1 |

## 2026-10-12 — Phase 1D visual alignment

The screens follow the Learn FR-DA layout: cream background, dark blue headings, large rounded light-blue actions, and a muted red quiz action. Colours, type and spacing live in `ui/theme`. Shared controls live in `FamilyUi`.

| Area | Decision | Status |
|---|---|---|
| Navigation | `AppNavigator` is unchanged. The lesson overview and the one-phrase player are two states of the existing lesson route. | V1 |
| Progress | Level 1 and Level 2 show `done/total` from the existing completion flags. Marking a lesson complete stores the same lesson id as a passed quiz. | V1 |
| Lesson text | Polish is the large line. Danish sits under it. Catalogue target titles are still empty, so the overview title stays the Danish lesson title. | V1 |
| Dialogue | Danish support stays hidden until the learner taps. Speech still requires a tap. | V1 |
| Palette | The family palette stays on the cream theme, including when the device is in dark mode, so the screens match the reference. | V1 |

## Still open
- exact Level 2 case sequence after linguistic review;
- final scope of Children for first public release;
- final Level 3 content scope;
- whether later releases add more FSM scenarios.
