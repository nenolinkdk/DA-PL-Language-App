# Acceptance Criteria

## Android skeleton

Accepted when:
- project opens/builds;
- main menu resembles the Nenoling/FR-DA family;
- all planned top-level entries exist;
- back navigation is predictable;
- empty Level 2/3 screens do not crash.

## Level 1 lesson 1

Accepted when:
- real Danish/Polish content loads from data;
- Polish is natural and reviewed;
- TTS is button-triggered;
- lesson navigation works;
- quiz works;
- progress survives normal app navigation as designed.

## First FSM scenario

Accepted when:
- scenario is loaded from JSON/data, not hard-coded into Compose;
- start state is deterministic;
- current turn is shown;
- Danish support starts hidden;
- support can be revealed/hidden;
- Polish TTS requires a button press;
- choices transition correctly;
- terminal state completes cleanly;
- restart returns to start;
- leaving/reopening starts over in V1;
- validator detects broken targets/unreachable accidental states;
- unit tests cover every transition.

## Release readiness

Accepted only after:
- automated validation passes;
- build passes cleanly;
- physical Android test passes;
- Level 1 linguistic QA is complete;
- documentation matches implemented behaviour;
- known limitations are recorded.
