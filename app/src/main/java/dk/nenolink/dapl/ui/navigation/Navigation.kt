package dk.nenolink.dapl.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dk.nenolink.dapl.ui.menu.MainMenuScreen
import dk.nenolink.dapl.ui.module.ModulePlaceholderScreen

object Routes {
    const val HOME = "home"
    const val LEVEL1 = "level1"
    const val LEVEL2 = "level2"
    const val LEVEL3 = "level3"
    const val CONVERSATION = "conversation"
    const val QUIZ = "quiz"
    const val GRAMMAR = "grammar"
    const val CHILDREN = "children"
    const val ABOUT = "about"
}

@Composable
fun DaplApp() {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.HOME
        ) {
            composable(Routes.HOME) {
                MainMenuScreen(
                    onModuleSelected = { navController.navigate(it) }
                )
            }
            composable(Routes.LEVEL1) {
                ModulePlaceholderScreen(
                    title = "Niveau 1",
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.LEVEL2) {
                ModulePlaceholderScreen(
                    title = "Niveau 2",
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.LEVEL3) {
                ModulePlaceholderScreen(
                    title = "Niveau 3",
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.CONVERSATION) {
                ModulePlaceholderScreen(
                    title = "Samtaletræning",
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.QUIZ) {
                ModulePlaceholderScreen(
                    title = "Quiz / repetition",
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.GRAMMAR) {
                ModulePlaceholderScreen(
                    title = "Grammatik",
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.CHILDREN) {
                ModulePlaceholderScreen(
                    title = "Børn",
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.ABOUT) {
                ModulePlaceholderScreen(
                    title = "Dokumentation / Om",
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
