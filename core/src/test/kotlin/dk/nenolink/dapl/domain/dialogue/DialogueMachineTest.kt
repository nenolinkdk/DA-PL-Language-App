package dk.nenolink.dapl.domain.dialogue

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DialogueMachineTest {

    private fun cafeScenario() = DialogueScenario(
        id = "test-cafe",
        title = "Test",
        startState = "start",
        states = mapOf(
            "start" to DialogueState(
                prompt = "Hej",
                options = listOf(
                    DialogueOption("høfligt svar", "next"),
                    DialogueOption("uhøfligt svar", "retry")
                )
            ),
            "retry" to DialogueState(
                prompt = "Prøv igen",
                options = listOf(DialogueOption("undskyld", "next"))
            ),
            "next" to DialogueState(
                prompt = "Vælg",
                options = listOf(DialogueOption("ok", "done"))
            ),
            "done" to DialogueState(prompt = "Slut", terminal = true)
        )
    )

    @Test
    fun startStateIsStart() {
        val machine = DialogueMachine(cafeScenario())
        assertEquals("start", machine.currentStateId)
        assertFalse(machine.isTerminal)
    }

    @Test
    fun chooseOptionMovesToTarget() {
        val machine = DialogueMachine(cafeScenario())
        assertTrue(machine.chooseOption(0))
        assertEquals("next", machine.currentStateId)
    }

    @Test
    fun invalidIndexIsRejected() {
        val machine = DialogueMachine(cafeScenario())
        assertFalse(machine.chooseOption(5))
        assertEquals("start", machine.currentStateId)
    }

    @Test
    fun terminalReachedAfterPath() {
        val machine = DialogueMachine(cafeScenario())
        machine.chooseOption(1)
        machine.chooseOption(0)
        machine.chooseOption(0)
        assertTrue(machine.isTerminal)
        assertEquals("done", machine.currentStateId)
    }

    @Test
    fun resetReturnsToStart() {
        val machine = DialogueMachine(cafeScenario())
        machine.chooseOption(0)
        machine.reset()
        assertEquals("start", machine.currentStateId)
    }

    @Test
    fun aNewMachineDoesNotResumeThePreviousOne() {
        val scenario = cafeScenario()
        val first = DialogueMachine(scenario)
        first.chooseOption(0)
        val reopened = DialogueMachine(scenario)
        assertEquals(scenario.startState, reopened.currentStateId)
        assertEquals("next", first.currentStateId)
    }
}
