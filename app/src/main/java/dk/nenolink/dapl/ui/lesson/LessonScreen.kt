package dk.nenolink.dapl.ui.lesson

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dk.nenolink.dapl.domain.model.LessonDocument
import dk.nenolink.dapl.domain.model.LessonItem
import dk.nenolink.dapl.tts.SpeechLocale
import dk.nenolink.dapl.tts.SpeechRequest
import dk.nenolink.dapl.tts.TextToSpeechGateway
import dk.nenolink.dapl.ui.common.CenteredHeading
import dk.nenolink.dapl.ui.common.CircleStepButton
import dk.nenolink.dapl.ui.common.FamilyActionButton
import dk.nenolink.dapl.ui.common.FamilyScaffold
import dk.nenolink.dapl.ui.common.ProgressReadout
import dk.nenolink.dapl.ui.common.WhitePanel
import dk.nenolink.dapl.ui.theme.ButtonBlue
import dk.nenolink.dapl.ui.theme.DaplSpace
import dk.nenolink.dapl.ui.theme.Navy
import dk.nenolink.dapl.ui.theme.NavyMuted
import dk.nenolink.dapl.ui.theme.QuizRed

@Composable
fun LessonScreen(
    lesson: LessonDocument,
    targetTitle: String,
    completed: Boolean,
    speech: TextToSpeechGateway,
    onQuiz: () -> Unit,
    onMarkCompleted: () -> Unit,
    onBack: () -> Unit
) {
    var playing by rememberSaveable(lesson.id) { mutableStateOf(false) }
    var index by rememberSaveable(lesson.id) { mutableIntStateOf(0) }
    var seen by rememberSaveable(lesson.id) { mutableIntStateOf(0) }
    fun leavePlayer() {
        if (lesson.items.isNotEmpty()) {
            seen = (index + 1).coerceAtMost(lesson.items.size)
        }
        playing = false
    }
    BackHandler(enabled = playing) { leavePlayer() }

    FamilyScaffold(onBack = { if (playing) leavePlayer() else onBack() }) { padding ->
        if (playing && lesson.items.isNotEmpty()) {
            LessonPlayer(
                lesson = lesson,
                index = index.coerceIn(0, lesson.items.lastIndex),
                completed = completed,
                speech = speech,
                onIndex = { index = it },
                onMarkCompleted = onMarkCompleted,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )
        } else {
            LessonOverview(
                lesson = lesson,
                targetTitle = targetTitle,
                completed = completed,
                seen = seen,
                onStart = { playing = true },
                onQuiz = onQuiz,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun LessonOverview(
    lesson: LessonDocument,
    targetTitle: String,
    completed: Boolean,
    seen: Int,
    onStart: () -> Unit,
    onQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = DaplSpace.screen, vertical = DaplSpace.section),
        verticalArrangement = Arrangement.spacedBy(DaplSpace.section),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LessonTitle(supportTitle = lesson.title, targetTitle = targetTitle)
        WhitePanel {
            Text(
                text = "Beskrivelse",
                color = Navy,
                style = MaterialTheme.typography.titleMedium
            )
            if (lesson.notes.isEmpty()) {
                Text(
                    text = "Lektionen har ${lesson.items.size} sætninger.",
                    color = Navy,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                lesson.notes.forEach { note ->
                    Text(
                        text = note,
                        color = Navy,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        val shown = if (completed) lesson.items.size else seen.coerceIn(0, lesson.items.size)
        ProgressReadout(
            label = if (completed) "Gennemført" else "$shown / ${lesson.items.size}",
            fraction = if (lesson.items.isEmpty()) 0f else shown.toFloat() / lesson.items.size.toFloat()
        )
        FamilyActionButton(
            text = "Start lektion",
            container = ButtonBlue,
            enabled = lesson.items.isNotEmpty(),
            onClick = onStart
        )
        if (lesson.quiz.isNotEmpty()) {
            FamilyActionButton(
                text = "Åbn quiz",
                container = QuizRed,
                onClick = onQuiz
            )
        }
    }
}

@Composable
private fun LessonTitle(supportTitle: String, targetTitle: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DaplSpace.tight)
    ) {
        if (targetTitle.isNotBlank()) {
            CenteredHeading(targetTitle)
            CenteredHeading(
                text = supportTitle,
                style = MaterialTheme.typography.titleMedium,
                color = NavyMuted
            )
        } else {
            CenteredHeading(supportTitle)
        }
    }
}

@Composable
private fun LessonPlayer(
    lesson: LessonDocument,
    index: Int,
    completed: Boolean,
    speech: TextToSpeechGateway,
    onIndex: (Int) -> Unit,
    onMarkCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val item = lesson.items[index]
    val total = lesson.items.size
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = DaplSpace.screen, vertical = DaplSpace.section),
        verticalArrangement = Arrangement.spacedBy(DaplSpace.section),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProgressReadout(
            label = "${index + 1} af $total",
            fraction = (index + 1).toFloat() / total.toFloat()
        )
        PhraseBlock(item = item)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DaplSpace.moduleGap, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleStepButton(
                label = "Forrige",
                symbol = "←",
                enabled = index > 0,
                onClick = { onIndex(index - 1) }
            )
            CircleStepButton(
                label = "Næste",
                symbol = "→",
                enabled = index < lesson.items.lastIndex,
                onClick = { onIndex(index + 1) }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(DaplSpace.moduleGap, Alignment.CenterHorizontally)
        ) {
            FamilyActionButton(
                text = "Hør polsk",
                container = ButtonBlue,
                expand = false,
                onClick = {
                    speech.speak(
                        SpeechRequest(
                            text = item.polish,
                            locale = SpeechLocale.POLISH,
                            requestedByUser = true
                        )
                    )
                }
            )
            FamilyActionButton(
                text = "Hør dansk",
                container = ButtonBlue,
                expand = false,
                onClick = {
                    speech.speak(
                        SpeechRequest(
                            text = item.danish,
                            locale = SpeechLocale.DANISH,
                            requestedByUser = true
                        )
                    )
                }
            )
        }
        FamilyActionButton(
            text = if (completed) "Gennemført" else "Markér som gennemført",
            container = ButtonBlue,
            enabled = !completed,
            onClick = onMarkCompleted
        )
        ExplanationPanel(lesson = lesson, item = item)
    }
}

@Composable
private fun PhraseBlock(item: LessonItem) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(DaplSpace.tight)
    ) {
        Text(
            text = if (item.type == "vocabulary") "Ord" else "Sætning",
            modifier = Modifier.fillMaxWidth(),
            color = NavyMuted,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = item.polish,
            modifier = Modifier.fillMaxWidth(),
            color = Navy,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = item.danish,
            modifier = Modifier.fillMaxWidth(),
            color = NavyMuted,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun ExplanationPanel(lesson: LessonDocument, item: LessonItem) {
    WhitePanel {
        Text(
            text = "Grammatik",
            color = Navy,
            style = MaterialTheme.typography.titleMedium
        )
        item.hint?.takeIf { it.isNotBlank() }?.let { hint ->
            Text(
                text = hint,
                color = Navy,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        lesson.notes.forEach { note ->
            Text(
                text = note,
                color = Navy,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (lesson.notes.isEmpty() && item.hint.isNullOrBlank()) {
            Text(
                text = "Der er ingen særskilt forklaring til dette udtryk.",
                color = NavyMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
