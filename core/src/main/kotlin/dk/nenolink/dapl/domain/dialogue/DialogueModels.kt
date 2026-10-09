package dk.nenolink.dapl.domain.dialogue

import kotlinx.serialization.Serializable

/**
 * Dialogue document as stored in the imported scenario files.
 * This shape is not the app navigation model.
 */
@Serializable
data class DialogueScenario(
    val id: String,
    val title: String,
    val description: String = "",
    val startState: String,
    val states: Map<String, DialogueState> = emptyMap()
)

@Serializable
data class DialogueState(
    val prompt: String,
    val expectedPhrases: List<String> = emptyList(),
    val options: List<DialogueOption> = emptyList(),
    val terminal: Boolean = false
)

@Serializable
data class DialogueOption(
    val text: String,
    val target: String
)
