# Open Questions / Decisions Not Yet Frozen

Items marked here should remain explicit until prototype or linguistic review resolves them.

## Resolution rule

Questions already answered by frozen V1 decisions are **not** listed here. See [DECISIONS.md](DECISIONS.md) and [DEVELOPMENT_SPEC.md](DEVELOPMENT_SPEC.md) for the frozen decisions. When an OPEN item below is resolved, record the decision in DECISIONS.md and remove it from this list.

## Status vocabulary

Use exactly these markers everywhere:

- **V1** — required for the first usable implementation.
- **NICE-TO-HAVE** — explicitly deferred.
- **OPEN** — decision still required.
- **LINGUISTIC REVIEW** — must be reviewed by a Danish/Polish language reviewer before content or design is frozen.

Do not use **UNCERTAIN** as a status marker; replace it with the closest marker above (usually OPEN).

## Technology

1. **Implementation baseline:** Phase 1A on `vibe/phase1a-skeleton` is a new Kotlin/Compose app (`dk.nenolink.dapl`). `origin/main` still contains a different Android tree. The product baseline is not frozen until those trees are reconciled.
2. **FSM schema location:** one JSON file per scenario or grouped by lesson/module?
3. **Validation:** Kotlin-only validation, build-time script validation, or both?
4. **Mermaid:** keep diagrams as documentation only; do not make Android depend on Mermaid. (Currently treated as settled practice; kept here until recorded in DECISIONS.md.)
5. **Content-source split:** Phase 1A keeps one catalog index at `content/course/catalog.json`, read by core tests and app assets. The `content/production` pipeline for real lessons is still deferred until the first vertical slice (see REPOSITORY_STRUCTURE.md).

## Pedagogy

6. **Response evaluation:** preferred / acceptable / repair / incorrect — which categories should be learner-visible? (V1 has no learner-visible classification; the question is whether/when to add one.)
7. **Incorrect choices:** allow realistic wrong paths, or immediately give feedback and retry?
8. **Grammar correction:** explain immediately, after scenario completion, or both?
9. **Number of choices:** normally 2, 3 or variable?
10. **Branch depth:** how long should one scenario be before it becomes cumbersome on a phone?
11. **Support-reveal scope:** whether Danish support reveal is per turn or remembered during the current scenario.

## Polish linguistic design

12. Exact order of Polish cases.
13. How early verbal aspect should be introduced.
14. How much formal/informal address distinction belongs in Level 1.
15. Whether pronunciation notes should use IPA, simplified Danish guidance, audio/TTS only, or a combination.
16. Which Polish regional/cultural variants need explicit treatment.

## Scope

17. Children module in first public release or later?
18. Level 3 business content in first public release or later?
19. Offline-only V1, except external resources, or optional online enrichment later?

These are not blockers for creating the Android skeleton. They should be resolved incrementally and recorded as decisions.
