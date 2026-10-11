package dk.nenolink.dapl.domain.dialogue

import dk.nenolink.dapl.domain.content.CourseLibraryLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DialogueCoverageTest {
    private val dialogues = CourseLibraryLoader.loadBundled().dialogues

    @Test
    fun everyOptionMovesToItsTargetAndATerminalIsReachable() {
        assertEquals(6, dialogues.size)
        dialogues.values.forEach { scenario ->
            val report = DialogueValidator.validate(scenario)
            assertTrue(scenario.id + ": " + report.errors, report.errors.isEmpty())
            val paths = pathsFromStart(scenario)
            assertEquals(scenario.states.keys, paths.keys)
            scenario.states.forEach { (id, state) ->
                state.options.forEachIndexed { index, option ->
                    val machine = DialogueMachine(scenario)
                    paths.getValue(id).forEach { step ->
                        assertTrue(machine.chooseOption(step))
                    }
                    assertEquals(id, machine.currentStateId)
                    assertTrue(machine.chooseOption(index))
                    assertEquals(option.target, machine.currentStateId)
                }
            }
            val machine = DialogueMachine(scenario)
            machine.reset()
            assertEquals(scenario.startState, machine.currentStateId)
        }
    }

    private fun pathsFromStart(scenario: DialogueScenario): Map<String, List<Int>> {
        val paths = mutableMapOf(scenario.startState to emptyList<Int>())
        val queue = ArrayDeque<String>()
        queue.add(scenario.startState)
        while (queue.isNotEmpty()) {
            val id = queue.removeFirst()
            val path = paths.getValue(id)
            scenario.states.getValue(id).options.forEachIndexed { index, option ->
                if (option.target !in paths) {
                    paths[option.target] = path + index
                    queue.add(option.target)
                }
            }
        }
        return paths
    }
}
