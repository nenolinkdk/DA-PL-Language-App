package dk.nenoling.dapl.dialogue

import dk.nenoling.dapl.data.*
import org.junit.Assert.*
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
        val m = DialogueMachine(cafeScenario())
        assertEquals("start", m.currentStateId)
        assertFalse(m.isTerminal)
    }

    @Test
    fun chooseOptionMovesToTarget() {
        val m = DialogueMachine(cafeScenario())
        assertTrue(m.chooseOption(0))
        assertEquals("next", m.currentStateId)
    }

    @Test
    fun invalidIndexIsRejected() {
        val m = DialogueMachine(cafeScenario())
        assertFalse(m.chooseOption(5))
        assertEquals("start", m.currentStateId)
    }

    @Test
    fun terminalReachedAfterPath() {
        val m = DialogueMachine(cafeScenario())
        m.chooseOption(1)
        m.chooseOption(0)
        m.chooseOption(0)
        assertTrue(m.isTerminal)
        assertEquals("done", m.currentStateId)
    }

    @Test
    fun resetReturnsToStart() {
        val m = DialogueMachine(cafeScenario())
        m.chooseOption(0)
        m.reset()
        assertEquals("start", m.currentStateId)
    }
}
