package dk.nenolink.dapl.domain.content

import dk.nenolink.dapl.domain.model.AppRoute
import dk.nenolink.dapl.domain.model.BilingualText
import dk.nenolink.dapl.domain.model.CourseCatalog
import dk.nenolink.dapl.domain.model.LessonIndexEntry
import dk.nenolink.dapl.domain.model.ModuleAvailability
import dk.nenolink.dapl.domain.model.ModuleIndexEntry
import dk.nenolink.dapl.domain.model.ScenarioIndexEntry
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Loads the Phase 1A course index.
 *
 * The index names modules, lessons and scenarios. It is not lesson content
 * and it does not contain dialogue states or weighted transitions.
 */
object CatalogLoader {
    private val json = Json { ignoreUnknownKeys = false }

    fun loadBundled(): CourseCatalog {
        val stream = CatalogLoader::class.java.classLoader
            ?.getResourceAsStream(RESOURCE_PATH)
            ?: error("Missing classpath resource $RESOURCE_PATH")
        return load(stream.bufferedReader(Charsets.UTF_8).use { it.readText() })
    }

    fun load(jsonText: String): CourseCatalog {
        val dto = json.decodeFromString(CatalogDto.serializer(), jsonText)
        val catalog = CourseCatalog(
            schemaVersion = dto.schemaVersion,
            modules = dto.modules.map { it.toDomain() },
            scenarios = dto.scenarios.map { it.toDomain() }
        )
        validate(catalog)
        return catalog
    }

    private fun validate(catalog: CourseCatalog) {
        require(catalog.schemaVersion == SUPPORTED_SCHEMA) {
            "Unsupported catalog schema ${catalog.schemaVersion}"
        }

        val moduleIds = catalog.modules.map { it.id }
        require(moduleIds.size == moduleIds.toSet().size) { "Module ids must be unique" }
        require(moduleIds.all { it.isNotBlank() }) { "Module ids must be non-blank" }

        val routes = catalog.modules.map { it.route }
        require(routes.size == routes.toSet().size) { "Module routes must be unique" }
        require(routes.toSet() == AppRoute.menuOrder.toSet()) {
            "Catalog must contain exactly the eight top-level modules"
        }

        catalog.modules.forEach { module ->
            if (module.availability == ModuleAvailability.NOT_YET_FILLED) {
                require(module.lessons.isEmpty()) {
                    "${module.id} is not yet filled and must not list lessons"
                }
            }
            val orders = module.lessons.map { it.order }
            require(orders.size == orders.toSet().size) {
                "${module.id} has duplicate lesson order values"
            }
        }

        val lessons = catalog.modules.flatMap { it.lessons }
        val lessonIds = lessons.map { it.id }
        require(lessonIds.size == lessonIds.toSet().size) { "Lesson ids must be unique" }
        lessons.forEach { lesson ->
            require(lesson.id.isNotBlank() && !lesson.id.any { it.isWhitespace() }) {
                "Lesson id must be a stable token: ${lesson.id}"
            }
            val parent = catalog.modules.first { module -> module.lessons.any { it.id == lesson.id } }
            require(lesson.moduleId == parent.id) {
                "Lesson ${lesson.id} moduleId does not match ${parent.id}"
            }
            require(lesson.title.support.isNotBlank()) {
                "Lesson ${lesson.id} needs a Danish title"
            }
            if (lesson.released) {
                require(lesson.title.target.isNotBlank()) {
                    "Released lesson ${lesson.id} needs a Polish title"
                }
            }
        }

        val scenarioIds = catalog.scenarios.map { it.id }
        require(scenarioIds.size == scenarioIds.toSet().size) { "Scenario ids must be unique" }
        catalog.scenarios.forEach { scenario ->
            require(scenario.id.isNotBlank() && !scenario.id.any { it.isWhitespace() }) {
                "Scenario id must be a stable token: ${scenario.id}"
            }
            require(scenario.title.support.isNotBlank()) {
                "Scenario ${scenario.id} needs a Danish title"
            }
        }
    }

    private const val SUPPORTED_SCHEMA = 1
    const val RESOURCE_PATH = "course/catalog.json"
}

@Serializable
private data class CatalogDto(
    val schemaVersion: Int,
    val modules: List<ModuleDto>,
    val scenarios: List<ScenarioDto> = emptyList()
)

@Serializable
private data class ModuleDto(
    val id: String,
    val route: String,
    val order: Int,
    val availability: String,
    val lessons: List<LessonDto> = emptyList()
) {
    fun toDomain(): ModuleIndexEntry {
        val parsedRoute = AppRoute.fromWire(route)
            ?: error("Unknown module route: $route")
        require(parsedRoute != AppRoute.HOME) { "Home is not a catalog module" }
        val parsedAvailability = when (availability) {
            "STRUCTURE_READY" -> ModuleAvailability.STRUCTURE_READY
            "NOT_YET_FILLED" -> ModuleAvailability.NOT_YET_FILLED
            else -> error("Unknown availability: $availability")
        }
        return ModuleIndexEntry(
            id = id,
            route = parsedRoute,
            order = order,
            availability = parsedAvailability,
            lessons = lessons.map { it.toDomain() }.sortedBy { it.order }
        )
    }
}

@Serializable
private data class LessonDto(
    val id: String,
    val moduleId: String,
    val order: Int,
    val title: BilingualDto,
    val released: Boolean
) {
    fun toDomain(): LessonIndexEntry = LessonIndexEntry(
        id = id,
        moduleId = moduleId,
        order = order,
        title = title.toDomain(),
        released = released
    )
}

@Serializable
private data class ScenarioDto(
    val id: String,
    val title: BilingualDto
) {
    fun toDomain(): ScenarioIndexEntry = ScenarioIndexEntry(
        id = id,
        title = title.toDomain()
    )
}

@Serializable
private data class BilingualDto(
    val support: String = "",
    val target: String = ""
) {
    fun toDomain(): BilingualText = BilingualText(support = support, target = target)
}
