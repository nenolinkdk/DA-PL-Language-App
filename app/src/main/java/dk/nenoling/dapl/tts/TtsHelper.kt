package dk.nenoling.dapl.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Knap-trigget TTS. da-DK for dansk, pl-PL for polsk.
 * Hvis stemmen mangler, fejler speak() stille (brugeren ser teksten alligevel).
 */
class TtsHelper(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var ready = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
    }

    fun speakPolish(text: String) = speak(text, polish = true)

    fun speakDanish(text: String) = speak(text, polish = false)

    private fun speak(text: String, polish: Boolean) {
        val engine = tts ?: return
        if (!ready) return
        val locale = if (polish) Locale("pl", "PL") else Locale("da", "DK")
        if (engine.isLanguageAvailable(locale) == TextToSpeech.LANG_MISSING_DATA) {
            engine.language = Locale.US // fald tilbage - tekst vises i UI uanset
        } else {
            engine.language = locale
        }
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "dapl-$locale")
    }

    fun dispose() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        ready = false
    }
}
