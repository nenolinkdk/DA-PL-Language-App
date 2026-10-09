package dk.nenolink.dapl.domain.dialogue

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DialogueValidatorTest {

    private fun validScenario() = DialogueScenario(
        id = "ok",
        title = "Ok",
        startState = "a",
        states = mapOf(
            "a" to DialogueState("A", options = listOf(DialogueOption("x", "b"))),
            "b" to DialogueState("B", terminal = true)
        )
    )

    @Test
    fun validScenarioHasNoErrors() {
        val report = DialogueValidator.validate(validScenario())
        assertTrue(report.errors.isEmpty())
        assertTrue(report.warnings.isEmpty())
    }

    @Test
    fun missingStartStateIsError() {
        val report = DialogueValidator.validate(validScenario().copy(startState = "nope"))
        assertFalse(report.isValid)
        assertTrue(report.errors.any { it.contains("startState") })
    }

    @Test
    fun danglingTargetIsError() {
        val states = validScenario().states.toMutableMap()
        states["c"] = DialogueState("C", options = listOf(DialogueOption("x", "missing")))
        val report = DialogueValidator.validate(validScenario().copy(states = states))
        assertTrue(report.errors.any { it.contains("manglende tilstand") })
    }

    @Test
    fun nonTerminalWithoutOptionsIsDeadEnd() {
        val scenario = validScenario().copy(
            states = validScenario().states.mapValues { (_, state) ->
                if (state.terminal) DialogueState("B") else state
            }
        )
        val report = DialogueValidator.validate(scenario)
        assertTrue(report.errors.any { it.contains("dead-end") || it.contains("ingen terminal") })
    }

    @Test
    fun unreachableStateIsWarning() {
        val states = validScenario().states.toMutableMap()
        states["orphan"] = DialogueState("Orphan", options = listOf(DialogueOption("x", "a")))
        val report = DialogueValidator.validate(validScenario().copy(states = states))
        assertTrue(report.errors.isEmpty())
        assertTrue(report.warnings.any { it.contains("utilgængelig") })
    }

    @Test
    fun multipleTerminalsIsWarning() {
        val states = validScenario().states.toMutableMap()
        states["t2"] = DialogueState("T2", terminal = true)
        states["a"] = DialogueState(
            "A",
            options = listOf(DialogueOption("x", "b"), DialogueOption("y", "t2"))
        )
        val report = DialogueValidator.validate(validScenario().copy(states = states))
        assertTrue(report.errors.isEmpty())
        assertTrue(report.warnings.any { it.contains("terminaler") })
    }
}
