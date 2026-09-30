package dk.nenoling.dapl.data

import kotlinx.serialization.Serializable

@Serializable
data class Manifest(val modules: List<ModuleInfo> = emptyList())

@Serializable
data class ModuleInfo(
    val id: String,
    val title: String,
    val type: String,
    val order: Int = 0
)

@Serializable
data class Lesson(
    val id: String,
    val moduleId: String,
    val title: String,
    val order: Int = 0,
    val released: Boolean = true,
    val notes: List<String> = emptyList(),
    val items: List<LessonItem> = emptyList(),
    val quiz: List<QuizQuestion> = emptyList()
)

@Serializable
data class LessonItem(
    val id: String,
    val type: String = "phrase",
    val danish: String,
    val polish: String,
    val hint: String? = null
)

@Serializable
data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val answerIndex: Int = 0
)

@Serializable
data class GrammarSheet(
    val id: String,
    val title: String,
    val sections: List<GrammarSection> = emptyList()
)

@Serializable
data class GrammarSection(
    val heading: String,
    val body: String,
    val examples: List<Example> = emptyList()
)

@Serializable
data class Example(val danish: String, val polish: String)

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
