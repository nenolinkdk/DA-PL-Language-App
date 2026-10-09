package dk.nenolink.dapl.progress

import android.content.Context
import dk.nenolink.dapl.domain.progress.CourseProgress

/**
 * Completed flags for lessons and finished dialogues.
 * An unfinished dialogue turn is not stored.
 */
class ProgressStore(context: Context) : CourseProgress {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    override fun setCompleted(key: String, completed: Boolean) {
        prefs.edit().putBoolean(storageKey(key), completed).apply()
    }

    override fun isCompleted(key: String): Boolean = prefs.getBoolean(storageKey(key), false)

    override fun completedKeys(): List<String> =
        prefs.all.keys
            .filter { it.startsWith(PREFIX) }
            .map { it.removePrefix(PREFIX) }
            .filter { key -> prefs.getBoolean(storageKey(key), false) }
            .sorted()

    private fun storageKey(key: String): String = PREFIX + key

    private companion object {
        const val PREFS = "dapl_progress"
        const val PREFIX = "done_"
    }
}
