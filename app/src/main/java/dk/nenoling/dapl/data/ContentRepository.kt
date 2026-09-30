package dk.nenoling.dapl.data

import android.content.Context
import kotlinx.serialization.json.Json

/**
 * Læser alt indhold fra app-assets. Dialog-scenarier valideres med
 * DialogueValidator ved indlæsning - ugyldige scenarier fejler hårdt,
 * sådan at indholdsfejl fanges ved byggetid/testtid, ikke hos brugeren.
 */
class ContentRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    private fun readAsset(path: String): String =
        context.assets.open(path).bufferedReader().use { it.readText() }

    fun loadManifest(): Manifest =
        json.decodeFromString(readAsset("config/manifest.json"))

    fun loadLessons(moduleId: String): List<Lesson> {
        val dir = "course/$moduleId"
        val names = context.assets.list(dir)?.sorted() ?: emptyList()
        return names
            .filter { it.endsWith(".json") }
            .map { json.decodeFromString<Lesson>(readAsset("$dir/$it")) }
            .sortedBy { it.order }
    }

    fun loadLesson(lessonId: String): Lesson? {
        val result = loadLessons("level1").firstOrNull { it.id == lessonId }
        if (result == null) {
            // fald tilbage til direkte fil-læsning (fx andre moduler)
            val dir = "course/level1"
            val names = context.assets.list(dir)?.filter { it.endsWith(".json") } ?: emptyList()
            for (name in names) {
                val lesson = json.decodeFromString<Lesson>(readAsset("$dir/$name"))
                if (lesson.id == lessonId) return lesson
            }
        }
        return result
    }

    fun loadGrammarSheets(): List<GrammarSheet> {
        val dir = "course/grammar"
        val names = context.assets.list(dir)?.sorted() ?: emptyList()
        return names
            .filter { it.endsWith(".json") }
            .map { json.decodeFromString<GrammarSheet>(readAsset("$dir/$it")) }
    }

    fun loadDialogueIds(): List<String> =
        context.assets.list("dialogues")
            ?.filter { it.endsWith(".json") }
            ?.map { it.removeSuffix(".json") }
            ?.sorted()
            ?: emptyList()

    fun loadDialogue(id: String): DialogueScenario {
        val scenario = json.decodeFromString<DialogueScenario>(
            readAsset("dialogues/$id.json")
        )
        val report = dk.nenoling.dapl.dialogue.DialogueValidator.validate(scenario)
        require(report.errors.isEmpty()) {
            "Dialog-scenarie '$id' er ugyldigt: ${report.errors.joinToString("; ")}"
        }
        return scenario
    }
}
