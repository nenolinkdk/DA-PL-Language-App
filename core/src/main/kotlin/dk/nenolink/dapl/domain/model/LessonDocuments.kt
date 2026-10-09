package dk.nenolink.dapl.domain.model

import kotlinx.serialization.Serializable

/** Lesson document as stored in the imported course files. Fields are not renamed. */
@Serializable
data class LessonDocument(
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
    val examples: List<GrammarExample> = emptyList()
)

@Serializable
data class GrammarExample(
    val danish: String,
    val polish: String
)
