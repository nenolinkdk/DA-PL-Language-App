package dk.nenolink.dapl.domain.content

import dk.nenolink.dapl.domain.model.AppRoute
import dk.nenolink.dapl.domain.navigation.Destination

enum class ScreenKind {
    LESSON_LIST,
    LESSON_PLAYER,
    LESSON_QUIZ,
    CONVERSATION,
    DIALOGUE,
    GRAMMAR,
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

fun CourseLibrary.menuEntries(): List<MenuEntryModel> = AppRoute.menuOrder.map { route ->
    MenuEntryModel(
        route = route,
        label = route.menuLabel,
        detail = menuDetail(route)
    )
}

fun CourseLibrary.screenFor(
    destination: Destination,
    completed: Set<String> = emptySet()
): ScreenModel = when (destination) {
    Destination.Home -> error("Home is rendered by the menu, not as a module screen")
    Destination.Level1 -> lessonList(AppRoute.LEVEL1, completed)
    Destination.Level2 -> levelScreen(
        route = AppRoute.LEVEL2,
        completed = completed,
        emptyTitle = "Niveau 2",
        emptyBody = "Niveau 2 er ikke klar endnu. Modulet er med i menuen, så kursusstrukturen er på plads. Der er endnu ingen lektioner at åbne."
    )
    Destination.Level3 -> levelScreen(
        route = AppRoute.LEVEL3,
        completed = completed,
        emptyTitle = "Niveau 3",
        emptyBody = "Niveau 3 er ikke klar endnu. Modulet er reserveret i navigationen. Der er endnu ingen lektioner at åbne."
    )
    Destination.Conversation -> ScreenModel(
        title = AppRoute.CONVERSATION.menuLabel,
        body = if (catalog.scenarios.isEmpty()) {
            "Der er endnu ingen samtalescenarier. Når de kommer, styrer samtalen kun sin egen dialog. Den skifter ikke app-skærm."
        } else {
            "Vælg et scenarie. Samtalen styrer kun sin egen dialog. Et ufærdigt forløb gemmes ikke."
        },
        kind = ScreenKind.CONVERSATION,
        scenarios = catalog.scenarios.map { scenario ->
            ScenarioRowModel(id = scenario.id, title = scenario.title.support)
        }
    )
    Destination.Quiz -> unavailable(
        title = AppRoute.QUIZ.menuLabel,
        body = "Quiz er ikke klar endnu. En quiz knyttes til en udgivet lektion og åbnes derfra."
    )
    Destination.Grammar -> grammarScreen()
    Destination.Children -> unavailable(
        title = AppRoute.CHILDREN.menuLabel,
        body = "Børn er med i strukturen. Der er endnu ikke indhold til modulet."
    )
    Destination.About -> ScreenModel(
        title = AppRoute.ABOUT.menuLabel,
        body = """
            DA-PL er en Nenoling-app til dansktalende, der skal bruge praktisk polsk.

            Dansk (da-DK) er støttesprog. Polsk (pl-PL) er målsprog. Kursusindhold ligger på enheden.

            Oplæsning starter ikke af sig selv. Tekst læses kun op efter et tryk.

            ${aboutProgress()}
        """.trimIndent(),
        kind = ScreenKind.ABOUT
    )
    is Destination.Lesson -> lessonScreen(destination.lessonId)
    is Destination.LessonQuiz -> quizScreen(destination.lessonId)
    is Destination.Scenario -> scenarioScreen(destination.scenarioId)
}

private fun CourseLibrary.menuDetail(route: AppRoute): String = when (route) {
    AppRoute.LEVEL1, AppRoute.LEVEL2 -> lessonCountDetail(route)
    AppRoute.LEVEL3, AppRoute.CHILDREN ->
        "Strukturen findes · indhold er ikke klar"
    AppRoute.CONVERSATION ->
        if (catalog.scenarios.isEmpty()) "Ingen scenarier endnu" else "${catalog.scenarios.size} scenarier"
    AppRoute.QUIZ -> "Kommer sammen med lektionerne"
    AppRoute.GRAMMAR ->
        if (grammarSheets.isEmpty()) "Opslag · ikke klar endnu" else "${grammarSheets.size} opslag"
    AppRoute.ABOUT -> "Om appen og sprogene"
    AppRoute.HOME -> ""
}

private fun CourseLibrary.lessonCountDetail(route: AppRoute): String {
    val lessons = catalog.module(route)?.lessons.orEmpty()
    if (lessons.isEmpty()) return "Strukturen findes · indhold er ikke klar"
    val released = lessons.count { it.released }
    return "${lessons.size} lektioner · $released udgivet"
}

private fun CourseLibrary.aboutProgress(): String {
    val released = catalog.modules.flatMap { it.lessons }.count { it.released }
    val scenarios = catalog.scenarios.size
    return "Der er $released udgivne lektioner og $scenarios samtalescenarier. Niveau 3 og Børn har endnu ikke indhold."
}

private fun CourseLibrary.levelScreen(
    route: AppRoute,
    completed: Set<String>,
    emptyTitle: String,
    emptyBody: String
): ScreenModel {
    val lessons = catalog.module(route)?.lessons.orEmpty()
    if (lessons.isEmpty()) return unavailable(emptyTitle, emptyBody)
    return lessonList(route, completed)
}

private fun CourseLibrary.lessonList(route: AppRoute, completed: Set<String>): ScreenModel {
    val lessons = catalog.module(route)?.lessons.orEmpty().sortedBy { it.order }
    val released = lessons.count { it.released }
    val body = if (released == 0) {
        "Lektionerne er planlagt. Ingen af dem er udgivet endnu, så der er ikke noget at læse eller høre."
    } else {
        "Udgivne lektioner kan åbnes. De øvrige er med i listen, men har ikke indhold endnu."
    }
    return ScreenModel(
        title = route.menuLabel,
        body = body,
        kind = ScreenKind.LESSON_LIST,
        lessons = lessons.map { lesson ->
            LessonRowModel(
                id = lesson.id,
                title = lesson.title.support,
                released = lesson.released,
                statusLabel = when {
                    !lesson.released -> "Ikke udgivet endnu"
                    lesson.id in completed -> "Gennemført"
                    else -> "Klar"
                }
            )
        }
    )
}

private fun CourseLibrary.lessonScreen(lessonId: String): ScreenModel {
    val lesson = catalog.lesson(lessonId)
    val title = lesson?.title?.support?.takeIf { it.isNotBlank() } ?: "Lektion"
    return when {
        lesson == null -> unavailable("Lektion", "Lektionen findes ikke i kursuskataloget.")
        !lesson.released -> unavailable(
            title,
            "Denne lektion er ikke udgivet endnu. Der er ikke noget dansk eller polsk indhold at vise."
        )
        else -> ScreenModel(title = title, body = "", kind = ScreenKind.LESSON_PLAYER)
    }
}

private fun CourseLibrary.quizScreen(lessonId: String): ScreenModel {
    val lesson = catalog.lesson(lessonId)
    val title = lesson?.title?.support?.let { "Quiz · $it" } ?: "Quiz"
    return if (lesson != null && lesson.released && lesson.hasQuiz) {
        ScreenModel(title = title, body = "", kind = ScreenKind.LESSON_QUIZ)
    } else {
        unavailable(title, "Denne lektion har ikke en quiz.")
    }
}

private fun CourseLibrary.scenarioScreen(scenarioId: String): ScreenModel {
    val scenario = catalog.scenario(scenarioId)
    val title = scenario?.title?.support?.takeIf { it.isNotBlank() } ?: "Samtale"
    return if (scenario == null || dialogue(scenarioId) == null) {
        unavailable(title, "Scenariet findes ikke.")
    } else {
        ScreenModel(title = title, body = "", kind = ScreenKind.DIALOGUE)
    }
}

private fun CourseLibrary.grammarScreen(): ScreenModel {
    if (grammarSheets.isEmpty()) {
        return unavailable(
            title = AppRoute.GRAMMAR.menuLabel,
            body = "Grammatik er ikke klar endnu. Modulet bliver et opslag og er ikke en forudsætning for Niveau 1."
        )
    }
    return ScreenModel(
        title = AppRoute.GRAMMAR.menuLabel,
        body = "Opslag til de udgivne lektioner. Grammatik er ikke en forudsætning for Niveau 1.",
        kind = ScreenKind.GRAMMAR
    )
}

private fun unavailable(title: String, body: String): ScreenModel = ScreenModel(
    title = title,
    body = body,
    kind = ScreenKind.UNAVAILABLE
)
