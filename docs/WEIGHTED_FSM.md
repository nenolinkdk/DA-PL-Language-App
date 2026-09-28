# Weighted FSM Transitions — Future Option

## What a weighted transition means

A normal deterministic FSM has one fixed result:

```text
STATE A + CHOICE X -> STATE B
```

A weighted FSM can have several possible next states for the same choice:

```text
STATE A + CHOICE X
  -> STATE B (70)
  -> STATE C (30)
```

The numbers are relative weights. With weights 70 and 30, B is selected more often than C. They do not need to represent a pedagogical score.

Example: the learner says the same correct phrase in a café. On one run the waiter may ask "Small or large?", while on another the waiter may ask "Anything else?". This can make repeated practice less mechanical.

## Why it is not V1

Weighted transitions introduce randomness. This makes:
- tests less deterministic;
- bugs harder to reproduce;
- dialogue completion paths harder to reason about;
- progress/statistics more complicated;
- pedagogical sequencing less predictable.

For an absolute beginner, predictability is also useful.

## Nice-to-have design

If added later, keep weighting in content data rather than UI code.

Possible future representation:

```json
{
  "choiceId": "ORDER_COFFEE",
  "next": [
    {"stateId": "ASK_SIZE", "weight": 70},
    {"stateId": "ASK_EXTRA", "weight": 30}
  ]
}
```

The engine would:
1. validate all targets;
2. validate positive weights;
3. select one target using the weights;
4. record the selected path if statistics/debugging require it.

## Alternative before randomness

A safer intermediate feature is deterministic variation:
- beginner path;
- normal path;
- repeat/recovery path.

The learner or scenario configuration chooses the path explicitly. This provides variation while preserving reproducibility.

Status: **NICE-TO-HAVE, post-V1.**
