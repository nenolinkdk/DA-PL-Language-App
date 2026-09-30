package dk.nenoling.dapl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dk.nenoling.dapl.data.ContentRepository
import dk.nenoling.dapl.progress.ProgressStore
import dk.nenoling.dapl.tts.TtsHelper
import dk.nenoling.dapl.ui.screens.*
import dk.nenoling.dapl.ui.theme.DaplTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DaplTheme {
                DaplApp()
            }
        }
    }
}

@Composable
fun DaplApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val repository = remember { ContentRepository(context) }
    val progress = remember { ProgressStore(context) }
    val tts = remember { TtsHelper(context) }
    var progressTick by remember { mutableStateOf(0) }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val modules = remember { repository.loadManifest().modules }
            HomeScreen(
                modules = modules,
                completedKeys = progress.completedKeys().also { progressTick },
                onModule = { module ->
                    when (module.type) {
                        "level" -> navController.navigate("level/${module.id}")
                        "conversation" -> navController.navigate("conversation")
                        "grammar" -> navController.navigate("grammar")
                        "children" -> navController.navigate("children")
                        else -> navController.navigate("about")
                    }
                }
            )
        }
        composable(
            route = "level/{moduleId}",
            arguments = listOf(navArgument("moduleId") { type = NavType.StringType })
        ) { entry ->
            val moduleId = entry.arguments?.getString("moduleId") ?: "level1"
            val lessons = remember { repository.loadLessons(moduleId) }
            LevelScreen(
                lessons = lessons,
                progress = progress,
                onLesson = { lesson ->
                    navController.navigate("lesson/${lesson.id}")
                }
            )
        }
        composable(
            route = "lesson/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { entry ->
            val lessonId = entry.arguments?.getString("lessonId") ?: return@composable
            val lesson = remember { repository.loadLesson(lessonId) } ?: return@composable
            LessonScreen(
                lesson = lesson,
                tts = tts,
                onQuiz = { navController.navigate("quiz/$lessonId") },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "quiz/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { entry ->
            val lessonId = entry.arguments?.getString("lessonId") ?: return@composable
            val lesson = remember { repository.loadLesson(lessonId) } ?: return@composable
            QuizScreen(
                lesson = lesson,
                onFinished = { passed ->
                    if (passed) {
                        progress.setCompleted(lessonId)
                        progressTick++
                    }
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("conversation") {
            val dialogues = remember { repository.loadDialogueIds().map { it to it } }
            ConversationScreen(
                dialogues = dialogues,
                onDialogue = { id -> navController.navigate("dialogue/$id") },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = "dialogue/{dialogueId}",
            arguments = listOf(navArgument("dialogueId") { type = NavType.StringType })
        ) { entry ->
            val dialogueId = entry.arguments?.getString("dialogueId") ?: return@composable
            val scenario = remember { repository.loadDialogue(dialogueId) }
            DialogueScreen(
                scenario = scenario,
                tts = tts,
                onCompleted = {
                    progress.setCompleted(dialogueId)
                    progressTick++
                    navController.popBackStack()
                },
                onExit = { navController.popBackStack() }
            )
        }
        composable("grammar") {
            val sheets = remember { repository.loadGrammarSheets() }
            GrammarScreen(sheets = sheets, onBack = { navController.popBackStack() })
        }
        composable("children") {
            ChildrenScreen(onBack = { navController.popBackStack() })
        }
        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }

    LaunchedEffect(Unit) {
        // engangs-initialisering; TTS ryddes op med app-processen
    }
}
