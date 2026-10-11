package dk.nenolink.dapl.domain.progress

/** A lesson quiz is passed when at least half of the answers are correct. */
object QuizScoring {
    fun passed(correct: Int, questionCount: Int): Boolean =
        questionCount > 0 && correct * 2 >= questionCount
}
