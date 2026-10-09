package dk.nenolink.dapl.domain.content

import dk.nenolink.dapl.domain.model.AppRoute
import dk.nenolink.dapl.domain.model.CourseCatalog
import dk.nenolink.dapl.domain.navigation.Destination

enum class ScreenKind {
    LESSON_LIST,
    CONVERSATION,
    ABOUT,
    UNAVAILABLE
}

data class MenuEntryModel(
    val route: AppRoute,
    val label: String,
    val detail: String
)

data class LessonRowModel(
    val id: String,
    val title: String,
    val released: Boolean,
    val statusLabel: String
)

data class ScenarioRowModel(
    val id: String,
    val title: String
)

data class ScreenModel(
    val title: String,
    val body: String,
    val kind: ScreenKind,
    val lessons: List<LessonRowModel> = emptyList(),
    val scenarios: List<ScenarioRowModel> = emptyList()
)

fun CourseCatalog.menuEntries(): List<MenuEntryModel> = AppRoute.menuOrder.map { route ->
    MenuEntryModel(
        route = route,
        label = route.menuLabel,
        detail = menuDetail(route)
    )
}

fun CourseCatalog.screenFor(destination: Destination): ScreenModel = when (destination) {
    Destination.Home -> error("Home is rendered by the menu, not as a module screen")
    Destination.Level1 -> lessonList(AppRoute.LEVEL1)
    Destination.Level2 -> unavailable(
        title = "Niveau 2",
        body = "Niveau 2 er ikke klar endnu. Modulet er med i menuen, så kursusstrukturen er på plads. Der er endnu ingen lektioner at åbne."
    )
    Destination.Level3 -> unavailable(
        title = "Niveau 3",
        body = "Niveau 3 er ikke klar endnu. Modulet er reserveret i navigationen. Der er endnu ingen lektioner at åbne."
    )
    Destination.Conversation -> ScreenModel(
        title = AppRoute.CONVERSATION.menuLabel,
        body = if (scenarios.isEmpty()) {
            "Der er endnu ingen samtalescenarier. Når de kommer, styrer samtalen kun sin egen dialog. Den skifter ikke app-skærm."
        } else {
            "Vælg et scenarie. Selve samtalen er ikke en del af denne version."
        },
        kind = ScreenKind.CONVERSATION,
        scenarios = scenarios.map { scenario ->
            ScenarioRowModel(id = scenario.id, title = scenario.title.support)
        }
    )
    Destination.Quiz -> unavailable(
        title = AppRoute.QUIZ.menuLabel,
        body = "Quiz er ikke klar endnu. En quiz knyttes til en lektion, når lektionen er udgivet."
    )
    Destination.Grammar -> unavailable(
        title = AppRoute.GRAMMAR.menuLabel,
        body = "Grammatik er ikke klar endnu. Modulet bliver et opslag og er ikke en forudsætning for Niveau 1."
    )
    Destination.Children -> unavailable(
        title = AppRoute.CHILDREN.menuLabel,
        body = "Børn er med i strukturen. Der er endnu ikke indhold til modulet."
    )
    Destination.About -> ScreenModel(
        title = AppRoute.ABOUT.menuLabel,
        body = """
            DA-PL er en Nenoling-app til dansktalende, der skal bruge praktisk polsk.

            Dansk (da-DK) er støttesprog. Polsk (pl-PL) er målsprog. Kursusindhold ligger på enheden.

            Oplæsning starter ikke af sig selv. Når der er tekst at læse op, sker det kun efter et tryk.

            Denne version er skelettet: menu, navigation og tomme moduler. Der er endnu ingen lektioner eller samtaler.
        """.trimIndent(),
        kind = ScreenKind.ABOUT
    )
    is Destination.Lesson -> lessonPlaceholder(destination.lessonId)
    is Destination.Scenario -> scenarioPlaceholder(destination.scenarioId)
}

private fun CourseCatalog.menuDetail(route: AppRoute): String = when (route) {
    AppRoute.LEVEL1 -> {
        val count = module(route)?.lessons?.size ?: 0
        "$count lektioner · indhold er ikke udgivet endnu"
    }
    AppRoute.LEVEL2, AppRoute.LEVEL3, AppRoute.CHILDREN ->
        "Strukturen findes · indhold er ikke klar"
    AppRoute.CONVERSATION ->
        if (scenarios.isEmpty()) "Ingen scenarier endnu" else "${scenarios.size} scenarier"
    AppRoute.QUIZ -> "Kommer sammen med lektionerne"
    AppRoute.GRAMMAR -> "Opslag · ikke klar endnu"
    AppRoute.ABOUT -> "Om appen og sprogene"
    AppRoute.HOME -> ""
}

private fun CourseCatalog.lessonList(route: AppRoute): ScreenModel {
    val module = module(route)
    val lessons = module?.lessons.orEmpty().sortedBy { it.order }
    return ScreenModel(
        title = route.menuLabel,
        body = "Lektionerne er planlagt. Ingen af dem er udgivet endnu, så der er ikke noget at læse eller høre.",
        kind = ScreenKind.LESSON_LIST,
        lessons = lessons.map { lesson ->
            LessonRowModel(
                id = lesson.id,
                title = lesson.title.support,
                released = lesson.released,
                statusLabel = if (lesson.released) "Klar" else "Ikke udgivet endnu"
            )
        }
    )
}

private fun CourseCatalog.lessonPlaceholder(lessonId: String): ScreenModel {
    val lesson = lesson(lessonId)
    val title = lesson?.title?.support?.takeIf { it.isNotBlank() } ?: "Lektion"
    val body = if (lesson == null) {
        "Lektionen findes ikke i kursuskataloget."
    } else if (!lesson.released) {
        "Denne lektion er ikke udgivet endnu. Der er ikke noget dansk eller polsk indhold at vise."
    } else {
        "Lektionsvisningen kommer senere. Indholdet åbnes ikke i denne version."
    }
    return unavailable(title, body)
}

private fun CourseCatalog.scenarioPlaceholder(scenarioId: String): ScreenModel {
    val scenario = scenario(scenarioId)
    val title = scenario?.title?.support?.takeIf { it.isNotBlank() } ?: "Samtale"
    val body = if (scenario == null) {
        "Scenariet findes ikke."
    } else {
        "Samtalen er ikke en del af denne version. Der er ingen dialog at starte, og intet forløb gemmes."
    }
    return unavailable(title, body)
}

private fun unavailable(title: String, body: String): ScreenModel = ScreenModel(
    title = title,
    body = body,
    kind = ScreenKind.UNAVAILABLE
)
