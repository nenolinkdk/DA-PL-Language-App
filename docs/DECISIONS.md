# DECISIONS

## 2026-09-29 - implementering
- Ren Kotlin + Jetpack Compose (Material 3), ingen legacy-views.
- "Samtaletræning" er top-modul på hjemskærmen (FSM-dialog sættes direkte op mod exercises).
- Progress gemmes som completed-flags i SharedPreferences (ProgressStore).
- Dialog-scenarier er deterministiske FSM'er (DialogueMachine) og valideres af DialogueValidator
  (start-state, transitions, dead-ends, unreachable, terminaler). Scenarie-JSON valideres også
  af ContentRepository ved indlæsning.
- TTS er knap-trigget (ikke automatisk), lokaler da-DK og pl-PL.
- Lektioner 2-10 ligger som strukturskelet med "released": false - indhold fyldes ud separat.

## Åbne spørgsmål
- Skal quiz-score gemmes pr. lektion (nu gemmes kun gennemført/ikke)?
- Skal FSM-dialog få lyd-feedback (TTS af expectedPhrases)?
