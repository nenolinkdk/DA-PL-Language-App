package dk.nenolink.dapl.domain.dialogue

import org.junit.Assert.assertEquals
import org.junit.Test

class DialogueSpeechTest {
    @Test
    fun spokenLineUsesPolishWhenPresentAndPromptOtherwise() {
        val legacy = DialogueState(prompt = "Dzień dobry")
        assertEquals("Dzień dobry", legacy.spokenPolish())
        assertEquals("", legacy.support)

        val split = DialogueState(
            prompt = "Dzień dobry",
            polish = "Dzień dobry! Co podać?",
            support = "Goddag! Hvad må det være?"
        )
        assertEquals("Dzień dobry! Co podać?", split.spokenPolish())
    }
}
