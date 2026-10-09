package dk.nenolink.dapl.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dk.nenolink.dapl.domain.content.CourseLibrary
import dk.nenolink.dapl.domain.content.ScreenKind
import dk.nenolink.dapl.domain.content.menuEntries
import dk.nenolink.dapl.domain.content.screenFor
import dk.nenolink.dapl.domain.model.AppRoute
import dk.nenolink.dapl.domain.navigation.AppNavigator
import dk.nenolink.dapl.domain.navigation.Destination
import dk.nenolink.dapl.domain.navigation.NavEvent
import dk.nenolink.dapl.domain.navigation.NavigationState
import dk.nenolink.dapl.domain.navigation.toDestination
import dk.nenolink.dapl.domain.progress.CourseProgress
import dk.nenolink.dapl.tts.TextToSpeechGateway
import dk.nenolink.dapl.ui.common.ModuleScreen
import dk.nenolink.dapl.ui.dialogue.DialogueScreen
import dk.nenolink.dapl.ui.grammar.GrammarScreen
import dk.nenolink.dapl.ui.lesson.LessonScreen
import dk.nenolink.dapl.ui.menu.MainMenuScreen
import dk.nenolink.dapl.ui.quiz.QuizScreen

object NavRoutes {
    const val HOME = "home"
    const val LESSON = "lesson/{lessonId}"
    const val LESSON_QUIZ = "lesson/{lessonId}/quiz"
    const val SCENARIO = "scenario/{scenarioId}"

    fun lesson(lessonId: String): String = "lesson/$lessonId"

    fun lessonQuiz(lessonId: String): String = "lesson/$lessonId/quiz"

    fun scenario(scenarioId: String): String = "scenario/$scenarioId"
}

@Composable
fun DaplApp(
    library: CourseLibrary,
    speech: TextToSpeechGateway,
    progress: CourseProgress
) {
    val navController = rememberNavController()
    var progressTick by remember { mutableIntStateOf(0) }
    val completed = remember(progressTick) { progress.completedKeys().toSet() }
    val catalog = library.catalog

    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        composable(NavRoutes.HOME) {
            MainMenuScreen(
                entries = library.menuEntries(),
                onOpen = { route ->
                    openIfAllowed(
                        navController = navController,
                        catalog = catalog,
                        from = NavigationState.home(),
                        event = NavEvent.OpenModule(route),
                        route = route.wire
                    )
                }
            )
        }
        AppRoute.menuOrder.forEach { route ->
            composable(route.wire) {
                val destination = route.toDestination()
                val model = library.screenFor(destination, completed)
                if (model.kind == ScreenKind.GRAMMAR) {
                    GrammarScreen(
                        model = model,
                        sheets = library.grammarSheets,
                        onBack = { navController.popBackStack() }
                    )
                } else {
                    ModuleScreen(
                        model = model,
                        onBack = { navController.popBackStack() },
                        onLesson = { lessonId ->
                            openIfAllowed(
                                navController = navController,
                                catalog = catalog,
                                from = NavigationState.home().push(destination),
                                event = NavEvent.OpenLesson(lessonId),
                                route = NavRoutes.lesson(lessonId)
                            )
                        },
                        onScenario = { scenarioId ->
                            openIfAllowed(
                                navController = navController,
                                catalog = catalog,
                                from = NavigationState.home().push(destination),
                                event = NavEvent.OpenScenario(scenarioId),
                                route = NavRoutes.scenario(scenarioId)
                            )
                        }
                    )
                }
            }
        }
        composable(
            route = NavRoutes.LESSON_QUIZ,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { entry ->
            val lessonId = entry.arguments?.getString("lessonId").orEmpty()
            val document = library.lesson(lessonId)
            val model = library.screenFor(Destination.LessonQuiz(lessonId), completed)
            if (document != null && model.kind == ScreenKind.LESSON_QUIZ) {
                QuizScreen(
                    lesson = document,
                    onFinished = { passed ->
                        if (passed) {
                            progress.setCompleted(lessonId)
                            progressTick++
                        }
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            } else {
                ModuleScreen(model = model, onBack = { navController.popBackStack() })
            }
        }
        composable(
            route = NavRoutes.LESSON,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { entry ->
            val lessonId = entry.arguments?.getString("lessonId").orEmpty()
            val document = library.lesson(lessonId)
            val model = library.screenFor(Destination.Lesson(lessonId), completed)
            if (document != null && model.kind == ScreenKind.LESSON_PLAYER) {
                LessonScreen(
                    lesson = document,
                    speech = speech,
                    onQuiz = {
                        openIfAllowed(
                            navController = navController,
                            catalog = catalog,
                            from = NavigationState.home()
                                .push(Destination.Level1)
                                .push(Destination.Lesson(lessonId)),
                            event = NavEvent.OpenQuiz(lessonId),
                            route = NavRoutes.lessonQuiz(lessonId)
                        )
                    },
                    onBack = { navController.popBackStack() }
                )
            } else {
                ModuleScreen(model = model, onBack = { navController.popBackStack() })
            }
        }
        composable(
            route = NavRoutes.SCENARIO,
            arguments = listOf(navArgument("scenarioId") { type = NavType.StringType })
        ) { entry ->
            val scenarioId = entry.arguments?.getString("scenarioId").orEmpty()
            val scenario = library.dialogue(scenarioId)
            val model = library.screenFor(Destination.Scenario(scenarioId), completed)
            if (scenario != null && model.kind == ScreenKind.DIALOGUE) {
                DialogueScreen(
                    scenario = scenario,
                    speech = speech,
                    onCompleted = {
                        progress.setCompleted(scenarioId)
                        progressTick++
                        navController.popBackStack()
                    },
                    onExit = { navController.popBackStack() }
                )
            } else {
                ModuleScreen(model = model, onBack = { navController.popBackStack() })
            }
        }
    }
}

private fun openIfAllowed(
    navController: NavHostController,
    catalog: dk.nenolink.dapl.domain.model.CourseCatalog,
    from: NavigationState,
    event: NavEvent,
    route: String
) {
    val next = AppNavigator.reduce(from, event, catalog)
    if (next != from) {
        navController.navigate(route)
    }
}
