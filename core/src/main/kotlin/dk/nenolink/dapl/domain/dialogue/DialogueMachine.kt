package dk.nenolink.dapl.domain.dialogue

/**
 * Deterministic dialogue FSM.
 *
 * A choice moves to that option's target state. The machine does not choose
 * app screens, and it does not keep a session after a new instance is created.
 */
class DialogueMachine(private val scenario: DialogueScenario) {

    var currentStateId: String = scenario.startState
        private set

    val state: DialogueState
        get() = requireNotNull(scenario.states[currentStateId]) {
            "Tilstand '$currentStateId' findes ikke i scenarie '${scenario.id}'"
        }

    val isTerminal: Boolean get() = state.terminal

    val scenarioTitle: String get() = scenario.title

    fun chooseOption(index: Int): Boolean {
        val options = state.options
        if (index !in options.indices) return false
        val target = options[index].target
        if (target !in scenario.states) return false
        currentStateId = target
        return true
    }

    fun reset() {
        currentStateId = scenario.startState
    }
}
