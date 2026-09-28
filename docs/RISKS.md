# Risks and Known Uncertainties

| Risk | Impact | Mitigation | Status |
|---|---|---|---|
| Polish inflection errors | high | human linguistic review + small content batches | active |
| Over-complex grammar too early | high | phrase-first Level 1; cases from Level 2 | controlled |
| FSM mixed with Android navigation | high | separate DialogueMachine/navigation models | controlled |
| Too many branches on phone | medium | short V1 scenarios, 2–3 choices | monitor |
| Random FSM hard to test | medium | deterministic V1; weights deferred | controlled |
| Empty future modules look broken | medium | explicit empty-state UI | planned |
| TTS voice unavailable on device | medium | detect/report locale/engine availability gracefully | OPEN implementation |
| Copying legacy language-specific code | medium | use generic support/target roles | controlled |
| Content IDs change after release | medium/high | stable-ID contract from start | controlled |
| Documentation drifts from code | medium | Definition of Done includes docs | controlled |

## Technical uncertainty policy

Do not hide uncertainty in implementation. Mark it OPEN, test the smallest viable option, then record the decision in DECISIONS.md.
