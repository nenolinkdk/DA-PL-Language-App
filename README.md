# DA-PL-Language-App (Nenoling)

Lær dansk til polsk - en Android-app i Nenoling-familien.
Dansk -> polsk sprogtræning med lektioner, quiz, grammatik og FSM-baseret samtaletræning.

## Struktur
- `app/src/main/java/dk/nenoling/dapl/` - Kotlin/Compose-kode
- `app/src/main/assets/course/level1/` - lektions-JSON (lektion 1 udført, 2-10 som skelet, `"released": false`)
- `app/src/main/assets/dialogues/` - FSM-samtale scenarier (valideret: ingen dead-ends, præcis én terminal)
- `app/src/test/` - enhedstests for DialogueMachine og DialogueValidator

## Byg og kør
1. Åbn projektmappen i Android Studio (Koala eller nyere).
2. Lad Android Studio generere Gradle-wrapperen (Settings/Project sættes op automatisk).
3. `Build > Make Project`, derefter `Run`.

## Arkitektur-beslutninger
Se `docs/DECISIONS.md`.
