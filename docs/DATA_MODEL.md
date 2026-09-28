# Data Model and JSON Contracts

## Stable identity

Never use array position as persistent identity. IDs survive reordering.

Suggested ID forms:

```text
module-level1
lesson-l1-01-greetings
item-l1-01-001
quiz-l1-01
question-l1-01-q01
dlg-cafe-001
state-cafe-greeting
choice-cafe-order
```

## Bilingual text

```json
{
  "support": "Tak",
  "target": "Dziękuję"
}
```

Support = Danish. Target = Polish.

## Course skeleton

```text
Course
 ├─ Level 1
 │   ├─ Lesson 01
 │   └─ ...
 ├─ Level 2
 ├─ Level 3
 ├─ Samtaletræning
 ├─ Grammar
 ├─ Children
 └─ About/resources
```

## Lesson minimum model

| Field | Type | V1 | Note |
|---|---|---:|---|
| id | string | yes | stable |
| moduleId | string | yes | parent |
| order | integer | yes | display order |
| title | bilingual | yes | DA/PL |
| situation | bilingual | yes | short context |
| items | array | yes | may be empty only in unreleased modules |
| quiz | object | when released | lesson-specific |
| tags | array | optional | search/grouping later |

## Dialogue minimum model

| Field | Type | V1 | Note |
|---|---|---:|---|
| id | string | yes | scenario identity |
| title | bilingual | yes | menu title |
| startStateId | string | yes | exactly one |
| states | array/map | yes | all targets resolvable |
| speaker | enum/string | yes | learner/npc/system |
| utterance | bilingual | yes | Polish primary |
| choices | array | non-terminal | normally 2–3 |
| nextStateId | string | per choice | deterministic V1 |
| terminal | boolean | yes | explicit ending |

## Validation invariants

- IDs unique in their scope.
- All references resolve.
- Released lessons contain real content.
- Exactly one FSM start state reference.
- Terminal states need no outgoing choice.
- Non-terminal states have at least one transition.
- Unreachable FSM states fail validation unless explicitly allowed for development.
- Locale mapping is da-DK / pl-PL.
- Production assets contain no remnants from another language pair.
