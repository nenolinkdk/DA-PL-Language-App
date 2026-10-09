package dk.nenolink.dapl.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dk.nenolink.dapl.domain.content.menuEntries
import dk.nenolink.dapl.domain.content.screenFor
import dk.nenolink.dapl.domain.model.AppRoute
import dk.nenolink.dapl.domain.model.CourseCatalog
import dk.nenolink.dapl.domain.navigation.AppNavigator
import dk.nenolink.dapl.domain.navigation.Destination
import dk.nenolink.dapl.domain.navigation.NavEvent
import dk.nenolink.dapl.domain.navigation.NavigationState
import dk.nenolink.dapl.domain.navigation.toDestination
import dk.nenolink.dapl.ui.common.ModuleScreen
import dk.nenolink.dapl.ui.menu.MainMenuScreen

object NavRoutes {
    const val HOME = "home"
    const val LESSON = "lesson/{lessonId}"
    const val SCENARIO = "scenario/{scenarioId}"

    fun lesson(lessonId: String): String = "lesson/$lessonId"

    fun scenario(scenarioId: String): String = "scenario/$scenarioId"
}

@Composable
fun DaplApp(catalog: CourseCatalog) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME
    ) {
        composable(NavRoutes.HOME) {
            MainMenuScreen(
                entries = catalog.menuEntries(),
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
                ModuleScreen(
                    model = catalog.screenFor(destination),
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
        composable(
            route = NavRoutes.LESSON,
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { entry ->
            val lessonId = entry.arguments?.getString("lessonId").orEmpty()
            ModuleScreen(
                model = remember(lessonId) { catalog.screenFor(Destination.Lesson(lessonId)) },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = NavRoutes.SCENARIO,
            arguments = listOf(navArgument("scenarioId") { type = NavType.StringType })
        ) { entry ->
            val scenarioId = entry.arguments?.getString("scenarioId").orEmpty()
            ModuleScreen(
                model = remember(scenarioId) { catalog.screenFor(Destination.Scenario(scenarioId)) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private fun openIfAllowed(
    navController: NavHostController,
    catalog: CourseCatalog,
    from: NavigationState,
    event: NavEvent,
    route: String
) {
    val next = AppNavigator.reduce(from, event, catalog)
    if (next != from) {
        navController.navigate(route)
    }
}
