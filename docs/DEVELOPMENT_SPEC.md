# Development Specification — DA-PL Language App

## 1. Product objective

Build an Android app for a Danish-speaking absolute/near-absolute beginner who needs practical Polish, initially with travel use as a primary real-world scenario.

The app belongs to the Nenoling family and should visually follow Learn-FR-DA where practical.

## 2. V1 decisions

### Main navigation
Samtaletræning (conversation practice) is a separate top-level menu item.

Proposed structure:
1. Niveau 1
2. Niveau 2
3. Niveau 3
4. Samtaletræning
5. Quiz / repetition
6. Grammatik
7. Børn
8. Dokumentation / Om

### Beginner-first policy
Niveau 1 must assume very little prior Polish.

Priority:
- greetings and courtesy;
- yes/no/basic questions;
- numbers and prices;
- café/restaurant;
- shopping;
- hotel/accommodation;
- public transport;
- tickets;
- directions;
- simple problems and asking for help;
- essential travel phrases.

Grammar is introduced only where it helps the learner perform a task.

### Danish support
Danish support text is hidden by default in Samtaletræning and revealed by a tap/click.

The Polish text remains primary.

### TTS
No automatic playback in V1.
The learner explicitly presses a TTS button.

### Dialogue display
V1 shows the current turn.

Full dialogue history is NICE-TO-HAVE.

### FSM persistence
Do not persist an unfinished FSM session in V1.
Restarting/reopening a scenario starts it again.

Normal course progress may still be persisted separately.

### Choice classification
V1 has no learner-visible preferred/acceptable/repair/incorrect classification.

Classification/scoring is NICE-TO-HAVE and must not be required by the first schema unless later needed internally.

## 3. Course-content implementation strategy

Build the complete structural skeleton first:
- all top-level modules;
- Level 1, 2 and 3 containers;
- Children;
- Grammar;
- Samtaletræning;
- quiz/review hooks;
- documentation/about;
- stable IDs and empty/placeholder content containers.

Then fully populate Level 1 first.

Do not fabricate full Level 2/3 production content merely to make the app look complete. Empty/not-yet-released modules must be handled cleanly by the UI.

## 4. Grammar progression

### Level 1
Practical phrase-first approach.

Allow small grammatical notes where necessary, but avoid making Polish case paradigms a prerequisite for using the first lessons.

### Level 2
Cases become an explicit learning objective, introduced gradually and in context.

Initial principle:
- introduce one practical function at a time;
- connect forms to phrases already known;
- avoid presenting the entire Polish case system at once;
- recycle the same forms across several situations.

Exact sequence remains subject to Polish linguistic review.

### Level 3
Can consolidate case use, aspect, more complex sentence structures and professional communication.

## 5. FSM V1 contract

FSM is a deterministic dialogue engine.

For V1:
- one start state;
- stable state IDs;
- 2–3 choices where useful;
- deterministic next state per choice;
- explicit terminal state(s);
- repair/repetition branches allowed;
- no random transitions;
- no weighted transitions;
- no numeric score required;
- no mid-dialogue persistence required.

The FSM controls conversation state only. Android screen navigation remains outside the FSM engine.

## 6. Android responsibilities

Suggested components:
- ContentRepository
- DialogueRepository
- DialogueMachine
- Course/lesson ViewModels
- DialogueViewModel
- TTS service/helper using da-DK and pl-PL
- progress store
- JSON validators
- Compose screens/components

Dialogue UI state should contain at minimum:
- scenario ID;
- current state ID;
- Polish utterance;
- Danish support utterance;
- supportVisible boolean;
- choices;
- terminal boolean.

## 7. First implementation milestone

The first meaningful Android milestone is not a full course.

It should prove:
- app starts;
- main menu works;
- complete module skeleton exists;
- Level 1 opens;
- one Level-1 lesson is fully populated;
- Polish and Danish TTS buttons work on demand;
- Samtaletræning opens separately;
- one FSM scenario runs from start to terminal state;
- Danish support can be revealed/hidden;
- restart works;
- unit tests validate FSM transitions.

## 8. Nice-to-have backlog

Not required for V1:
- full conversation history;
- response classification;
- weighted transitions;
- random conversational variants;
- scoring;
- adaptive difficulty;
- saved mid-dialogue session;
- statistics about chosen branches;
- spoken-answer recognition;
- automatic TTS;
- AI-generated live dialogue.

These must remain separable from the deterministic core.
