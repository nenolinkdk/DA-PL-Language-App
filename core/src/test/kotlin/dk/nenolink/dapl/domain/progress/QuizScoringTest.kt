package dk.nenolink.dapl.domain.progress

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuizScoringTest {
    @Test
    fun halfOrMoreIsAPass() {
        assertTrue(QuizScoring.passed(correct = 3, questionCount = 6))
        assertTrue(QuizScoring.passed(correct = 4, questionCount = 6))
        assertFalse(QuizScoring.passed(correct = 2, questionCount = 6))
        assertFalse(QuizScoring.passed(correct = 0, questionCount = 0))
    }
}
