package dk.nenolink.dapl.domain.dialogue

/**
 * Static checks for a dialogue scenario: start state, targets, dead ends,
 * reachability, and terminal states. Unreachable states are warnings.
 * There are no weighted transitions.
 */
object DialogueValidator {

    data class Report(val errors: List<String>, val warnings: List<String>) {
        val isValid: Boolean get() = errors.isEmpty()
    }

    fun validate(scenario: DialogueScenario): Report {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()
        val states = scenario.states

        if (states.isEmpty()) {
            return Report(listOf("Scenarie har ingen tilstande"), emptyList())
        }
        if (scenario.startState !in states) {
            errors.add("startState '${scenario.startState}' findes ikke")
        }

        for ((id, state) in states) {
            for (option in state.options) {
                if (option.target !in states) {
                    errors.add("Tilstand '$id': option peger på manglende tilstand '${option.target}'")
                }
            }
            if (!state.terminal && state.options.isEmpty()) {
                errors.add("Tilstand '$id' er en dead-end (ikke-terminal uden options)")
            }
        }

        val reachable = mutableSetOf<String>()
        if (scenario.startState in states) {
            val queue = ArrayDeque<String>()
            queue.add(scenario.startState)
            while (queue.isNotEmpty()) {
                val id = queue.removeFirst()
                if (id in reachable) continue
                reachable.add(id)
                for (option in states[id]?.options ?: emptyList()) {
                    if (option.target in states) queue.add(option.target)
                }
            }
        }
        for (id in states.keys) {
            if (id !in reachable) warnings.add("Tilstand '$id' er utilgængelig fra start")
        }

        val terminals = states.filter { it.value.terminal }.keys
        when {
            terminals.isEmpty() -> errors.add("Scenarie har ingen terminal tilstand")
            terminals.size > 1 -> warnings.add("Scenarie har ${terminals.size} terminaler: ${terminals.sorted()}")
        }

        return Report(errors, warnings)
    }
}
