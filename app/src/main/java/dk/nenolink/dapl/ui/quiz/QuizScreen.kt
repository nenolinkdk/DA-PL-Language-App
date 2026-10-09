package dk.nenolink.dapl.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.nenolink.dapl.domain.model.LessonDocument

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    lesson: LessonDocument,
    onFinished: (passed: Boolean) -> Unit,
    onBack: () -> Unit
) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    var correct by rememberSaveable { mutableIntStateOf(0) }
    val questions = lesson.quiz

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz · ${lesson.title}") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Tilbage") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (questions.isEmpty()) {
                Text("Denne lektion har ikke en quiz.")
            } else if (index >= questions.size) {
                val passed = correct * 2 >= questions.size
                Text("Resultat: $correct / ${questions.size}")
                Text(if (passed) "Gennemført." else "Prøv igen.")
                Button(onClick = { onFinished(passed) }, modifier = Modifier.fillMaxWidth()) {
                    Text("Tilbage til lektionen")
                }
            } else {
                val question = questions[index]
                Text(question.question, style = MaterialTheme.typography.titleLarge)
                question.options.forEachIndexed { optionIndex, option ->
                    OutlinedButton(
                        onClick = {
                            if (optionIndex == question.answerIndex) correct++
                            index++
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(option) }
                }
            }
        }
    }
}
