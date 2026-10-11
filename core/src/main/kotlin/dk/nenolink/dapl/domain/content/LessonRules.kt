package dk.nenolink.dapl.domain.content

import dk.nenolink.dapl.domain.model.LessonDocument

/**
 * Bounds for a released lesson: practical phrases, a small word list,
 * a Danish note and a short quiz. Unreleased lessons stay empty.
 */
object LessonRules {
    fun errors(document: LessonDocument): List<String> {
        if (!document.released) {
            return if (document.items.isNotEmpty() || document.quiz.isNotEmpty()) {
                listOf("Unreleased lesson ${document.id} must not carry phrase or quiz content")
            } else {
                emptyList()
            }
        }

        val errors = mutableListOf<String>()
        val phrases = document.items.count { it.type == "phrase" }
        val words = document.items.count { it.type == "vocabulary" }
        if (phrases !in 10..15) {
            errors.add("Lesson ${document.id} has $phrases phrases, expected 10-15")
        }
        if (words !in 3..8) {
            errors.add("Lesson ${document.id} has $words vocabulary items, expected 3-8")
        }
        if (document.quiz.size !in 5..10) {
            errors.add("Lesson ${document.id} has ${document.quiz.size} quiz questions, expected 5-10")
        }
        if (document.notes.none { it.isNotBlank() }) {
            errors.add("Lesson ${document.id} needs a Danish note")
        }
        val unknown = document.items.filter { it.type != "phrase" && it.type != "vocabulary" }
        if (unknown.isNotEmpty()) {
            errors.add("Lesson ${document.id} has an unknown item type")
        }

        val ids = document.items.map { it.id } + document.quiz.map { it.id }
        if (ids.any { it.isBlank() } || ids.size != ids.toSet().size) {
            errors.add("Lesson ${document.id} needs unique non-blank item and quiz ids")
        }
        document.items.forEach { item ->
            if (item.danish.isBlank() || item.polish.isBlank()) {
                errors.add("Lesson ${document.id} item ${item.id} needs Danish and Polish")
            }
        }
        document.quiz.forEach { question ->
            if (question.question.isBlank() || question.options.any { it.isBlank() }) {
                errors.add("Lesson ${document.id} question ${question.id} has blank text")
            }
            if (question.options.size < 2 || question.answerIndex !in question.options.indices) {
                errors.add("Lesson ${document.id} question ${question.id} has an invalid answer")
            }
        }
        return errors
    }
}
