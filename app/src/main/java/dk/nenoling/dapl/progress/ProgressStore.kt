package dk.nenoling.dapl.progress

import android.content.Context

/**
 * Simple completed-flags pr. lektion/dialog, gemt i SharedPreferences.
 */
class ProgressStore(context: Context) {

    private val prefs = context.getSharedPreferences("dapl_progress", Context.MODE_PRIVATE)

    fun setCompleted(key: String, completed: Boolean = true) {
        prefs.edit()
            .putBoolean("done_$key", completed)
            .apply()
    }

    fun isCompleted(key: String): Boolean = prefs.getBoolean("done_$key", false)

    fun completedKeys(): List<String> =
        prefs.all.keys
            .filter { it.startsWith("done_") }
            .map { it.removePrefix("done_") }
            .filter { key -> prefs.getBoolean("done_$key", false) }
            .sorted()
}
