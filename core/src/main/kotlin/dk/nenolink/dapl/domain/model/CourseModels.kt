package dk.nenolink.dapl.domain.model

/**
 * Top-level app routes. These identify screens, not dialogue states.
 */
enum class AppRoute(val wire: String, val menuLabel: String) {
    HOME("home", "Hjem"),
    LEVEL1("level1", "Niveau 1"),
    LEVEL2("level2", "Niveau 2"),
    LEVEL3("level3", "Niveau 3"),
    CONVERSATION("conversation", "Samtaletræning"),
    QUIZ("quiz", "Quiz / repetition"),
    GRAMMAR("grammar", "Grammatik"),
    CHILDREN("children", "Børn"),
    ABOUT("about", "Dokumentation / Om");

    companion object {
        val menuOrder: List<AppRoute> = listOf(
            LEVEL1,
            LEVEL2,
            LEVEL3,
            CONVERSATION,
            QUIZ,
            GRAMMAR,
            CHILDREN,
            ABOUT
        )

        fun fromWire(wire: String): AppRoute? = entries.firstOrNull { it.wire == wire }
    }
}

enum class ModuleAvailability {
    STRUCTURE_READY,
    NOT_YET_FILLED
}

/** Support is Danish. Target is Polish. */
data class BilingualText(
    val support: String,
    val target: String
)

data class LessonIndexEntry(
    val id: String,
    val moduleId: String,
    val order: Int,
    val title: BilingualText,
    val released: Boolean
)

data class ScenarioIndexEntry(
    val id: String,
    val title: BilingualText
)

data class ModuleIndexEntry(
    val id: String,
    val route: AppRoute,
    val order: Int,
    val availability: ModuleAvailability,
    val lessons: List<LessonIndexEntry>
)

data class CourseCatalog(
    val schemaVersion: Int,
    val modules: List<ModuleIndexEntry>,
    val scenarios: List<ScenarioIndexEntry>
) {
    fun module(route: AppRoute): ModuleIndexEntry? = modules.firstOrNull { it.route == route }

    fun lesson(id: String): LessonIndexEntry? = modules
        .asSequence()
        .flatMap { it.lessons.asSequence() }
        .firstOrNull { it.id == id }

    fun scenario(id: String): ScenarioIndexEntry? = scenarios.firstOrNull { it.id == id }
}

object AppIdentity {
    const val APPLICATION_ID = "dk.nenolink.dapl"
    const val SUPPORT_LOCALE = "da-DK"
    const val TARGET_LOCALE = "pl-PL"
}
