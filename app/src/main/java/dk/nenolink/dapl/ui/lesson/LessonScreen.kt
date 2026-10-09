package dk.nenolink.dapl.ui.lesson

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.nenolink.dapl.domain.model.LessonDocument
import dk.nenolink.dapl.tts.SpeechLocale
import dk.nenolink.dapl.tts.SpeechRequest
import dk.nenolink.dapl.tts.TextToSpeechGateway

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    lesson: LessonDocument,
    speech: TextToSpeechGateway,
    onQuiz: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(lesson.title) },
                navigationIcon = { TextButton(onClick = onBack) { Text("Tilbage") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            lesson.notes.forEach { note ->
                Text(note, style = MaterialTheme.typography.bodySmall)
            }
            if (lesson.notes.isNotEmpty()) {
                HorizontalDivider()
            }
            lesson.items.forEach { item ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(item.danish, style = MaterialTheme.typography.titleMedium)
                        Text(item.polish, style = MaterialTheme.typography.bodyLarge)
                        item.hint?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = {
                                speech.speak(
                                    SpeechRequest(
                                        text = item.danish,
                                        locale = SpeechLocale.DANISH,
                                        requestedByUser = true
                                    )
                                )
                            }) { Text("Dansk") }
                            TextButton(onClick = {
                                speech.speak(
                                    SpeechRequest(
                                        text = item.polish,
                                        locale = SpeechLocale.POLISH,
                                        requestedByUser = true
                                    )
                                )
                            }) { Text("Polsk") }
                        }
                    }
                }
            }
            if (lesson.quiz.isNotEmpty()) {
                Button(onClick = onQuiz, modifier = Modifier.fillMaxWidth()) {
                    Text("Tag quizzen")
                }
            }
        }
    }
}
