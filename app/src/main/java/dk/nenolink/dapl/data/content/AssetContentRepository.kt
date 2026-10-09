package dk.nenolink.dapl.data.content

import android.content.Context
import dk.nenolink.dapl.domain.content.CourseLibrary
import dk.nenolink.dapl.domain.content.CourseLibraryLoader

/**
 * Reads the course index and the imported lesson, grammar and dialogue files.
 * Phrase text is not rewritten here.
 */
class AssetContentRepository(context: Context) {
    private val assets = context.applicationContext.assets

    fun loadLibrary(): CourseLibrary {
        return CourseLibraryLoader.load(
            catalogJson = read("course/catalog.json"),
            lessonJson = readDirectory("course/level1"),
            grammarJson = readDirectory("course/grammar"),
            dialogueJson = readDirectory("dialogues")
        )
    }

    private fun readDirectory(path: String): List<String> {
        val names = assets.list(path)?.filter { it.endsWith(".json") }?.sorted().orEmpty()
        return names.map { read("$path/$it") }
    }

    private fun read(path: String): String =
        assets.open(path).bufferedReader(Charsets.UTF_8).use { it.readText() }
}
