package dk.nenolink.dapl.tts

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TtsPolicyTest {
    @Test
    fun speechRequiresAnExplicitUserAction() {
        val request = SpeechRequest(
            text = "Dzień dobry",
            locale = SpeechLocale.POLISH,
            requestedByUser = false
        )
        assertFalse(TtsPolicy.maySpeak(request))
        assertTrue(TtsPolicy.maySpeak(request.copy(requestedByUser = true)))
    }

    @Test
    fun blankTextIsNeverSpoken() {
        assertFalse(
            TtsPolicy.maySpeak(
                SpeechRequest(text = "   ", locale = SpeechLocale.DANISH, requestedByUser = true)
            )
        )
    }

    @Test
    fun localesStayOnDanishAndPolish() {
        assertEquals("da-DK", SpeechLocale.DANISH.tag)
        assertEquals("pl-PL", SpeechLocale.POLISH.tag)
    }
}
