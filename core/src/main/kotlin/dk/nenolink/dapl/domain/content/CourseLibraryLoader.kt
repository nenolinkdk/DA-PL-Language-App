package dk.nenolink.dapl.domain.content

import dk.nenolink.dapl.domain.dialogue.DialogueScenario
import dk.nenolink.dapl.domain.dialogue.DialogueValidator
import dk.nenolink.dapl.domain.model.CourseCatalog
import dk.nenolink.dapl.domain.model.GrammarSheet
import dk.nenolink.dapl.domain.model.LessonDocument
import dk.nenolink.dapl.domain.model.LessonIndexEntry
import dk.nenolink.dapl.domain.model.ModuleIndexEntry
import kotlinx.serialization.json.Json

data class CourseLibrary(
    val catalog: CourseCatalog,
    val lessons: Map<String, LessonDocument>,
    val grammarSheets: List<GrammarSheet>,
    val dialogues: Map<String, DialogueScenario>
) {
    fun lesson(id: String): LessonDocument? = lessons[id]

    fun dialogue(id: String): DialogueScenario? = dialogues[id]
}

/**
 * Joins the course index to the imported lesson, grammar and dialogue files.
 * The language text in those files is decoded and kept as written.
 */
object CourseLibraryLoader {
    private val json = Json { ignoreUnknownKeys = false }

    fun loadBundled(): CourseLibrary {
        val lessons = (1..10).map { index -> read("course/level1/lesson-${index.toString().padStart(2, '0')}.json") }
        val grammar = listOf(
            "course/grammar/grammar-hello-politeness.json",
            "course/grammar/grammar-you-formal.json"
        ).map { read(it) }
        val dialogues = listOf(
            "dialogues/dlg-cafe-001.json",
            "dialogues/dlg-ticket-001.json"
        ).map { read(it) }
        return load(
            catalogJson = read(CatalogLoader.RESOURCE_PATH),
            lessonJson = lessons,
            grammarJson = grammar,
            dialogueJson = dialogues
        )
    }

    fun load(
        catalogJson: String,
        lessonJson: List<String>,
        grammarJson: List<String>,
        dialogueJson: List<String>
    ): CourseLibrary {
        val catalog = CatalogLoader.load(catalogJson)
        val lessonDocuments = lessonJson.map { json.decodeFromString(LessonDocument.serializer(), it) }
        val grammarSheets = grammarJson.map { json.decodeFromString(GrammarSheet.serializer(), it) }
        val dialogueDocuments = dialogueJson.map { json.decodeFromString(DialogueScenario.serializer(), it) }
        val enriched = enrich(catalog, lessonDocuments, dialogueDocuments)
        return CourseLibrary(
            catalog = enriched,
            lessons = lessonDocuments.associateBy { it.id },
            grammarSheets = grammarSheets,
            dialogues = dialogueDocuments.associateBy { it.id }
        )
    }

    private fun enrich(
        catalog: CourseCatalog,
        lessonDocuments: List<LessonDocument>,
        dialogueDocuments: List<DialogueScenario>
    ): CourseCatalog {
        val documentsById = lessonDocuments.associateBy { it.id }
        require(documentsById.size == lessonDocuments.size) { "Lesson document ids must be unique" }
        val indexedIds = catalog.modules.flatMap { it.lessons }.map { it.id }.toSet()
        require(indexedIds == documentsById.keys) {
            "Catalog lesson ids $indexedIds do not match document ids ${documentsById.keys}"
        }

        val modules = catalog.modules.map { module ->
            module.copy(
                lessons = module.lessons.map { entry ->
                    bindLesson(module, entry, documentsById.getValue(entry.id))
                }
            )
        }

        val dialoguesById = dialogueDocuments.associateBy { it.id }
        require(dialoguesById.size == dialogueDocuments.size) { "Dialogue ids must be unique" }
        val scenarioIds = catalog.scenarios.map { it.id }.toSet()
        require(scenarioIds == dialoguesById.keys) {
            "Catalog scenario ids $scenarioIds do not match dialogue ids ${dialoguesById.keys}"
        }
        catalog.scenarios.forEach { scenario ->
            val document = dialoguesById.getValue(scenario.id)
            require(document.title == scenario.title.support) {
                "Dialogue ${scenario.id} title does not match the catalog"
            }
            val report = DialogueValidator.validate(document)
            require(report.errors.isEmpty()) {
                "Dialogue ${scenario.id} is invalid: ${report.errors.joinToString("; ")}"
            }
        }

        return catalog.copy(modules = modules)
    }

    private fun bindLesson(
        module: ModuleIndexEntry,
        entry: LessonIndexEntry,
        document: LessonDocument
    ): LessonIndexEntry {
        require(document.moduleId == LEVEL1_FILE_MODULE) {
            "Lesson ${document.id} uses module '${document.moduleId}', expected $LEVEL1_FILE_MODULE"
        }
        require(module.route.wire == LEVEL1_FILE_MODULE || document.moduleId == LEVEL1_FILE_MODULE)
        require(document.title == entry.title.support) {
            "Lesson ${document.id} title does not match the catalog"
        }
        require(document.order == entry.order) {
            "Lesson ${document.id} order does not match the catalog"
        }
        require(document.released == entry.released) {
            "Lesson ${document.id} released flag does not match the catalog"
        }
        if (!document.released) {
            require(document.items.isEmpty() && document.quiz.isEmpty()) {
                "Unreleased lesson ${document.id} must not carry phrase or quiz content"
            }
        }
        return entry.copy(hasQuiz = document.released && document.quiz.isNotEmpty())
    }

    private fun read(path: String): String {
        val stream = CourseLibraryLoader::class.java.classLoader?.getResourceAsStream(path)
            ?: error("Missing classpath resource $path")
        return stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    private const val LEVEL1_FILE_MODULE = "level1"
}
