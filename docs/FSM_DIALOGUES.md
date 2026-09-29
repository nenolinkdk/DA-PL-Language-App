# FSM Dialogue Design

Status markers follow the shared vocabulary in [OPEN_QUESTIONS.md](OPEN_QUESTIONS.md) (V1 / NICE-TO-HAVE / OPEN / LINGUISTIC REVIEW). **UNCERTAIN** is no longer used as a status marker.

### State-ID convention

Dialogue **state IDs** are stable string identifiers scoped to their scenario. They are **not** required to use the `state-...` prefixed form from DATA_MODEL.md's examples: short IDs such as `GREETING`, `ORDER`, `SIZE` are valid, as long as they are unique within the scenario and referenced consistently by `startStateId` and `nextStateId`. Scenario and choice IDs continue to follow the prefixed forms from DATA_MODEL.md (e.g. `dlg-cafe-001`, `choice-cafe-order`).

## 1. Purpose

Finite-state-machine dialogues allow a learner to take part in controlled branching conversations.

Example situation: ordering coffee in Polish.

Instead of displaying one fixed ten-line dialogue, the learner chooses a response. The selected response triggers a transition to the next dialogue state.

## 2. Conceptual state diagram

```mermaid
stateDiagram-v2
    [*] --> GREETING

    GREETING --> ORDER: learner chooses "I would like a coffee"
    GREETING --> HELP: learner chooses "I don't understand"

    ORDER --> SIZE: clerk asks about size
    HELP --> GREETING_SLOW: clerk repeats more simply

    GREETING_SLOW --> ORDER
    SIZE --> PAYMENT: learner chooses size
    PAYMENT --> SUCCESS: payment accepted

    SUCCESS --> [*]
```

This is a documentation model. Android should load equivalent data from JSON.

## 3. Example state table

| Current state | Actor/input | Choice/condition | Next state | UI/action |
|---|---|---|---|---|
| GREETING | system/shop assistant | dialogue starts | GREETING | Show Polish greeting + optional Danish support |
| GREETING | learner | ORDER_COFFEE | ORDER | Play/offer learner phrase |
| GREETING | learner | DONT_UNDERSTAND | HELP | Move to repair branch |
| HELP | shop assistant | automatic | GREETING_SLOW | Show simpler/repeated phrase |
| GREETING_SLOW | learner | ORDER_COFFEE | ORDER | Return to main task |
| ORDER | shop assistant | automatic | SIZE | Ask size question |
| SIZE | learner | SMALL / LARGE | PAYMENT | Continue order |
| PAYMENT | learner | PAY_CARD | SUCCESS | Complete scenario |
| SUCCESS | system | terminal | — | Show completion/restart |

## 4. Proposed JSON model

```json
{
  "id": "dlg-cafe-001",
  "title": {
    "support": "På café",
    "target": "W kawiarni"
  },
  "startStateId": "GREETING",
  "states": [
    {
      "id": "GREETING",
      "speaker": "npc",
      "utterance": {
        "support": "Goddag. Hvad skulle det være?",
        "target": "Dzień dobry. Co podać?"
      },
      "choices": [
        {
          "id": "ORDER_COFFEE",
          "text": {
            "support": "Jeg vil gerne have en kaffe.",
            "target": "Poproszę kawę."
          },
          "nextStateId": "ORDER"
        },
        {
          "id": "DONT_UNDERSTAND",
          "text": {
            "support": "Jeg forstår ikke.",
            "target": "Nie rozumiem."
          },
          "nextStateId": "HELP"
        }
      ]
    },
    {
      "id": "SUCCESS",
      "terminal": true,
      "utterance": {
        "support": "Bestillingen er gennemført.",
        "target": "Zamówienie zakończone."
      }
    }
  ]
}
```

## 5. Android model proposal

Possible domain model:

```text
DialogueScenario
  id
  title
  startStateId
  states: Map<StateId, DialogueState>

DialogueState
  id
  speaker
  utterance
  choices
  terminal
  hint?
  tags?

DialogueChoice
  id
  text
  nextStateId
  feedback?
```

Runtime state:

```text
DialogueSession
  scenarioId
  currentStateId
  visitedStateIds
  selectedChoiceIds
  completed
```

## 6. Transition function

Core engine behaviour should be conceptually simple:

```text
transition(currentStateId, choiceId) -> nextStateId
```

No random behaviour is required for version 1.

The same state + same choice should always produce the same next state.

## 7. Error and repair states

FSM is especially useful for language learning because misunderstandings can be modelled explicitly.

Examples:

- learner does not understand;
- learner chooses grammatically weak wording;
- learner asks the other person to repeat;
- learner gives an unsuitable answer;
- learner returns to the main conversation after a repair sequence.

A repair branch should usually rejoin the main task instead of ending the dialogue immediately.

## 8. Correctness versus natural variation

Not every choice needs to be labelled simply “right” or “wrong”.

Suggested categories:

- `preferred`
- `acceptable`
- `repair`
- `incorrect`

**OPEN:** Whether these categories should be visible to the learner in the first version. Frozen V1 decision: no learner-visible classification. Whether/when to add it remains OPEN (see OPEN_QUESTIONS.md).

## 9. Weighted transitions — later extension

A future version could attach weights to variants:

```json
{
  "next": [
    {"stateId": "A", "weight": 70},
    {"stateId": "B", "weight": 30}
  ]
}
```

This could create more natural replay variation.

**OPEN / NOT FOR V1:** Weighted/random transitions complicate reproducibility, testing, progress and pedagogical explanation. Start with deterministic FSMs.

## 10. Scoring — later extension

Possible scoring dimensions:

- task completion;
- number of repair branches;
- preferred/acceptable response ratio;
- vocabulary coverage;
- grammar objective coverage.

**OPEN / NOT FOR FIRST PROTOTYPE:** Do not introduce a global numeric score until the pedagogical meaning is defined.

## 11. Persistence

Frozen V1 decision (see DECISIONS.md): **no mid-dialogue persistence.** Option A applies in V1 — restarting or leaving a dialogue always starts over from the start state. Normal course progress may still be persisted separately.

Options, for reference:

A. No persistence: restarting a dialogue always starts over. **(V1)**

B. Save only completed/not completed.

C. Save the full current FSM state and history.

**OPEN:** whether a completed flag (option B) or resumable sessions (option C) are added after V1.
## 12. Relation to standard dialogues

Standard linear dialogues remain useful for:

- listening;
- reading;
- repetition;
- predictable phrase review.

FSM dialogues are better for:

- decision-making;
- conversational repair;
- practical response selection;
- replayability;
- modelling alternate conversational paths.

Both models should coexist.
