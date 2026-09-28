# Open Questions / Decisions Not Yet Frozen

Items marked here should remain explicit until prototype or linguistic review resolves them.

## Product/UI

1. **FSM placement:** separate main-menu module or an activity inside lessons?
2. **Menu name:** “Dialoger”, “Interaktive dialoger”, “Samtaletræning” or another Danish label?
3. **Support visibility:** always show Danish translation, or allow hide/show?
4. **TTS:** automatically play Polish NPC utterances, or only on button press?
5. **Dialogue history:** show previous turns on screen or only the current turn?

## Technology

6. **Implementation baseline:** start a new Kotlin/Compose app from the Nenoling template, or port an existing Learn-FR-DA Android codebase first?
7. **Persistence:** completed flag only or resumable FSM session?
8. **FSM schema location:** one JSON file per scenario or grouped by lesson/module?
9. **Validation:** Kotlin-only validation, build-time script validation, or both?
10. **Mermaid:** keep diagrams as documentation only; do not make Android depend on Mermaid.

## Pedagogy

11. **Response evaluation:** preferred / acceptable / repair / incorrect — which categories should be learner-visible?
12. **Incorrect choices:** allow realistic wrong paths, or immediately give feedback and retry?
13. **Grammar correction:** explain immediately, after scenario completion, or both?
14. **Number of choices:** normally 2, 3 or variable?
15. **Branch depth:** how long should one scenario be before it becomes cumbersome on a phone?

## Polish linguistic design

16. Exact order of Polish cases.
17. How early verbal aspect should be introduced.
18. How much formal/informal address distinction belongs in Level 1.
19. Whether pronunciation notes should use IPA, simplified Danish guidance, audio/TTS only, or a combination.
20. Which Polish regional/cultural variants need explicit treatment.

## Scope

21. First content target: 1 complete Level-1 lesson + 1 FSM, or a broader content skeleton?
22. Children module in first public release or later?
23. Level 3 business content in first public release or later?
24. Offline-only V1, except external resources, or optional online enrichment later?

These are not blockers for creating the Android skeleton. They should be resolved incrementally and recorded as decisions.
