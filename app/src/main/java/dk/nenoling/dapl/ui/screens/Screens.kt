package dk.nenoling.dapl.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.nenoling.dapl.data.*
import dk.nenoling.dapl.dialogue.DialogueMachine
import dk.nenoling.dapl.progress.ProgressStore
import dk.nenoling.dapl.tts.TtsHelper

@Composable
fun HomeScreen(
    modules: List<ModuleInfo>,
    completedKeys: List<String>,
    onModule: (ModuleInfo) -> Unit
) {
    ScreenScaffold(title = "DA-PL - Nenoling") { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Lær dansk! ${completedKeys.size} gennemførte enheder.",
                style = MaterialTheme.typography.bodyMedium
            )
            modules.sortedBy { it.order }.forEach { module ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(module.title) },
                        supportingContent = { Text(module.type) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        trailingContent = {
                            TextButton(onClick = { onModule(module) }) { Text("Åbn") }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LevelScreen(
    lessons: List<Lesson>,
    progress: ProgressStore,
    onLesson: (Lesson) -> Unit
) {
    ScreenScaffold(title = "Niveau 1") { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            lessons.forEach { lesson ->
                val done = progress.isCompleted(lesson.id)
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(lesson.title) },
                        supportingContent = {
                            Text(
                                if (!lesson.released) "Kommer snart"
                                else "${lesson.items.size} øvelser, ${lesson.quiz.size} quiz"
                            )
                        },
                        trailingContent = {
                            if (lesson.released) {
                                TextButton(
                                    onClick = { onLesson(lesson) },
                                    enabled = lesson.released
                                ) { Text(if (done) "Gentag" else "Start") }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LessonScreen(
    lesson: Lesson,
    tts: TtsHelper?,
    onQuiz: () -> Unit,
    onBack: () -> Unit
) {
    ScreenScaffold(title = lesson.title, onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            lesson.notes.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
            HorizontalDivider()
            lesson.items.forEach { item ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(item.danish, style = MaterialTheme.typography.titleMedium)
                        Text(item.polish, style = MaterialTheme.typography.bodyLarge)
                        item.hint?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { tts?.speakDanish(item.danish) }) { Text("da") }
                            TextButton(onClick = { tts?.speakPolish(item.polish) }) { Text("pl") }
                        }
                    }
                }
            }
            Button(onClick = onQuiz, modifier = Modifier.fillMaxWidth()) {
                Text("Tag quizzen")
            }
        }
    }
}

@Composable
fun QuizScreen(
    lesson: Lesson,
    onFinished: (passed: Boolean) -> Unit,
    onBack: () -> Unit
) {
    var index by remember { mutableIntStateOf(0) }
    var correct by remember { mutableIntStateOf(0) }
    val questions = lesson.quiz

    ScreenScaffold(title = "Quiz - ${lesson.title}", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (index >= questions.size) {
                val passed = questions.isNotEmpty() && correct * 2 >= questions.size
                Text("Resultat: $correct / ${questions.size}")
                Text(if (passed) "Gennemført!" else "Prøv igen - du klarer det!")
                Button(onClick = { onFinished(passed) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Tilbage til lektionen")
                }
            } else {
                val q = questions[index]
                Text(q.question, style = MaterialTheme.typography.titleLarge)
                q.options.forEachIndexed { i, option ->
                    OutlinedButton(
                        onClick = {
                            if (i == q.answerIndex) correct++
                            index++
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(option) }
                }
            }
        }
    }
}

@Composable
fun GrammarScreen(sheets: List<GrammarSheet>, onBack: () -> Unit) {
    ScreenScaffold(title = "Grammatik", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            sheets.forEach { sheet ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(sheet.title, style = MaterialTheme.typography.titleMedium)
                        sheet.sections.forEach { section ->
                            Text(section.heading, style = MaterialTheme.typography.titleSmall)
                            Text(section.body)
                            section.examples.forEach { ex ->
                                Text("${ex.danish}  ->  ${ex.polish}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConversationScreen(
    dialogues: List<Pair<String, String>>,
    onDialogue: (String) -> Unit,
    onBack: () -> Unit
) {
    ScreenScaffold(title = "Samtaletræning", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dialogues.forEach { (id, title) ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(title) },
                        trailingContent = {
                            TextButton(onClick = { onDialogue(id) }) { Text("Start") }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DialogueScreen(
    scenario: DialogueScenario,
    tts: TtsHelper?,
    onCompleted: () -> Unit,
    onExit: () -> Unit
) {
    val machine = remember { DialogueMachine(scenario) }
    var stateKey by remember { mutableStateOf(machine.currentStateId) }

    ScreenScaffold(title = scenario.title, onBack = onExit) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(machine.state.prompt)
                    Row {
                        TextButton(onClick = { tts?.speakPolish(machine.state.prompt) }) {
                            Text("Hør")
                        }
                    }
                }
            }
            if (machine.isTerminal) {
                Text("Samtalen er færdig!", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { machine.reset(); stateKey = machine.currentStateId }) {
                        Text("Kør igen")
                    }
                    Button(onClick = onCompleted) { Text("Gem og luk") }
                }
            } else {
                machine.state.options.forEachIndexed { i, option ->
                    OutlinedButton(
                        onClick = { machine.chooseOption(i); stateKey = machine.currentStateId },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(option.text) }
                }
            }
        }
    }
}

@Composable
fun ChildrenScreen(onBack: () -> Unit) {
    ScreenScaffold(title = "Børneside", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Børnelektioner kommer i næste version.", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun AboutScreen(onBack: () -> Unit) {
    ScreenScaffold(title = "Om DA-PL", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("DA-PL Language App")
            Text("Del af Nenoling-familien (nenolink.com).")
            Text("Dansk -> polsk: lektioner, quiz, grammatik og FSM-baseret samtaletræning.")
        }
    }
}

@Composable
private fun ScreenScaffold(
    title: String,
    onBack: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (onBack != null) {
                        TextButton(onClick = onBack) { Text("< Tilbage") }
                    }
                }
            )
        },
        content = { content(it) }
    )
}
