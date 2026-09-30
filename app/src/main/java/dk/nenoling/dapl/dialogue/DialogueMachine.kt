package dk.nenoling.dapl.dialogue

import dk.nenoling.dapl.data.DialogueScenario

/**
 * Deterministisk FSM for samtaletræning.
 * Hver tilstand tilbyder et fast antal svarmuligheder; valget flytter
 * maskinen til target-tilstanden. Terminaler afslutter samtalen.
 */
class DialogueMachine(private val scenario: DialogueScenario) {

    var currentStateId: String = scenario.startState
        private set

    val state: dk.nenoling.dapl.data.DialogueState
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
