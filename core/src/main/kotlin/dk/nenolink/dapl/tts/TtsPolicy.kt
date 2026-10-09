package dk.nenolink.dapl.tts

import dk.nenolink.dapl.domain.model.AppIdentity

enum class SpeechLocale(val tag: String) {
    DANISH(AppIdentity.SUPPORT_LOCALE),
    POLISH(AppIdentity.TARGET_LOCALE)
}

data class SpeechRequest(
    val text: String,
    val locale: SpeechLocale,
    val requestedByUser: Boolean
)

/**
 * Speech is allowed only after an explicit learner action.
 * Callers must not autoplay when a screen opens.
 */
object TtsPolicy {
    fun maySpeak(request: SpeechRequest): Boolean {
        return request.requestedByUser && request.text.isNotBlank()
    }
}

interface TextToSpeechGateway {
    fun speak(request: SpeechRequest)
}
