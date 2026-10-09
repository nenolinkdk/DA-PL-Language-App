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

1. **FSM schema location:** one JSON file per scenario or grouped by lesson/module? The two imported scenarios are one file each. Whether that stays the rule for later scenarios is still open.
2. **Validation:** Kotlin-only validation, build-time script validation, or both?
3. **Mermaid:** keep diagrams as documentation only; do not make Android depend on Mermaid. (Currently treated as settled practice; kept here until recorded in DECISIONS.md.)
4. **Content-source split:** Imported lessons, grammar and dialogues now live under `content/` and are packaged as app assets from that directory. A separate `content/production` generation step is still open (see REPOSITORY_STRUCTURE.md).

## Pedagogy

5. **Response evaluation:** preferred / acceptable / repair / incorrect — which categories should be learner-visible? (V1 has no learner-visible classification; the question is whether/when to add one.)
6. **Incorrect choices:** allow realistic wrong paths, or immediately give feedback and retry?
7. **Grammar correction:** explain immediately, after scenario completion, or both?
8. **Number of choices:** normally 2, 3 or variable?
9. **Branch depth:** how long should one scenario be before it becomes cumbersome on a phone?
10. **Support-reveal scope:** whether Danish support reveal is per turn or remembered during the current scenario.

## Polish linguistic design

11. Exact order of Polish cases.
12. How early verbal aspect should be introduced.
13. How much formal/informal address distinction belongs in Level 1.
14. Whether pronunciation notes should use IPA, simplified Danish guidance, audio/TTS only, or a combination.
15. Which Polish regional/cultural variants need explicit treatment.

## Scope

16. Children module in first public release or later?
17. Level 3 business content in first public release or later?
18. Offline-only V1, except external resources, or optional online enrichment later?

These are not blockers for creating the Android skeleton. They should be resolved incrementally and recorded as decisions.
