package dk.nenolink.dapl.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Speaks only when [speak] is called and [TtsPolicy] allows the request.
 * Nothing in Phase 1A calls this during composition, so opening a screen stays silent.
 */
class AndroidTextToSpeechGateway(
    context: Context
) : TextToSpeechGateway, TextToSpeech.OnInitListener {
    private val appContext = context.applicationContext
    private var engine: TextToSpeech? = null
    private var ready = false
    private val pending = ArrayDeque<SpeechRequest>()

    override fun speak(request: SpeechRequest) {
        if (!TtsPolicy.maySpeak(request)) return
        val current = engine
        if (current == null) {
            pending.add(request)
            engine = TextToSpeech(appContext, this)
            return
        }
        if (!ready) {
            pending.add(request)
            return
        }
        speakNow(request)
    }

    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        if (!ready) {
            pending.clear()
            return
        }
        val queued = pending.toList()
        pending.clear()
        queued.forEach(::speakNow)
    }

    fun shutdown() {
        pending.clear()
        engine?.shutdown()
        engine = null
        ready = false
    }

    private fun speakNow(request: SpeechRequest) {
        val tts = engine ?: return
        val locale = when (request.locale) {
            SpeechLocale.DANISH -> Locale.forLanguageTag(SpeechLocale.DANISH.tag)
            SpeechLocale.POLISH -> Locale.forLanguageTag(SpeechLocale.POLISH.tag)
        }
        val available = tts.isLanguageAvailable(locale)
        if (available == TextToSpeech.LANG_MISSING_DATA || available == TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.language = Locale.US
        } else {
            tts.language = locale
        }
        tts.speak(request.text, TextToSpeech.QUEUE_FLUSH, null, REQUEST_ID)
    }

    private companion object {
        const val REQUEST_ID = "dapl-explicit-tts"
    }
}
