package dk.nenoling.dapl.dialogue

import dk.nenoling.dapl.data.DialogueScenario

/**
 * Statisk validering af dialog-scenarier:
 * - startState findes
 * - alle option-targets findes
 * - ingen dead-ends (ikke-terminal uden options)
 * - ingen utilgængelige tilstande
 * - terminaler: ingen er en fejl, flere end én er en advarsel
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
            for (opt in state.options) {
                if (opt.target !in states) {
                    errors.add("Tilstand '$id': option peger på manglende tilstand '${opt.target}'")
                }
            }
            if (!state.terminal && state.options.isEmpty()) {
                errors.add("Tilstand '$id' er en dead-end (ikke-terminal uden options)")
            }
        }

        // reachability fra startState (breadth-first)
        val reachable = mutableSetOf<String>()
        if (scenario.startState in states) {
            val queue = ArrayDeque<String>()
            queue.add(scenario.startState)
            while (queue.isNotEmpty()) {
                val id = queue.removeFirst()
                if (id in reachable) continue
                reachable.add(id)
                for (opt in states[id]?.options ?: emptyList()) {
                    if (opt.target in states) queue.add(opt.target)
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
