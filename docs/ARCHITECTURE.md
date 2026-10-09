# Architecture

## Status legend

- **V1** — required for first usable implementation.
- **NICE-TO-HAVE** — explicitly deferred.
- **OPEN** — implementation decision still required.
- **LINGUISTIC REVIEW** — requires Danish/Polish language review before freezing content.

## System boundary

The app is offline-first. Course content and FSM scenarios are local assets. External resources, if added, are optional and must never be required to open lessons.

```mermaid
flowchart TD
  A[Production JSON] --> B[Validation]
  B --> C[Android assets]
  C --> D[Repositories]
  D --> E[Domain models]
  E --> F[ViewModels]
  F --> G[Compose UI]
  G --> H[TTS da-DK / pl-PL]
  F --> I[Progress store]

  J[FSM scenario JSON] --> K[FSM validation]
  K --> L[DialogueRepository]
  L --> M[DialogueMachine]
  M --> N[DialogueViewModel]
  N --> G
```

## Separation of concerns

| Layer | Responsibility | Must not do |
|---|---|---|
| Content data | Danish/Polish linguistic material | Android navigation |
| Repository | Load/validate/map assets | Render UI |
| Domain model | Stable app concepts | Know Compose widgets |
| DialogueMachine | FSM state + transitions | Navigate app screens |
| ViewModel | Screen state/actions | Contain production linguistic data |
| Compose UI | Display and input | Decide pedagogical transitions |
| TTS | Speak requested text/locale | Autoplay in V1 |
| Progress | Save normal course progress | Resume unfinished FSM in V1 |

## Suggested package structure

Phase 1A splits testable code from Android:

```text
core/src/main/kotlin/.../
  domain/model/          course index and identity
  domain/content/        catalog loader and empty-state copy
  domain/navigation/     app-screen navigator (not the dialogue FSM)
  tts/                   speech policy and gateway interface
content/course/          catalog.json shared by core tests and app assets
app/src/main/java/.../
  data/content/          asset repository
  tts/                   Android TTS adapter
  ui/navigation/         Navigation-Compose host
  ui/menu/
  ui/common/
```

Application ID for this skeleton: `dk.nenolink.dapl`.

Dialogue loading, progress and validation packages are intentionally absent until a later phase.

## Core architectural rule

FSM state and Android navigation state are different state machines. Do not merge them. A user can leave a dialogue screen via Android navigation; inside that screen, DialogueMachine controls only the conversational state.
