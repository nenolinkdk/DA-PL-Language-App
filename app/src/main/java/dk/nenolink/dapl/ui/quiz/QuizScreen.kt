package dk.nenolink.dapl.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dk.nenolink.dapl.domain.model.LessonDocument
import dk.nenolink.dapl.domain.progress.QuizScoring
import dk.nenolink.dapl.ui.common.CenteredHeading
import dk.nenolink.dapl.ui.common.FamilyActionButton
import dk.nenolink.dapl.ui.common.FamilyScaffold
import dk.nenolink.dapl.ui.common.ProgressReadout
import dk.nenolink.dapl.ui.common.WhitePanel
import dk.nenolink.dapl.ui.theme.ButtonBlue
import dk.nenolink.dapl.ui.theme.DaplSpace
import dk.nenolink.dapl.ui.theme.Navy

@Composable
fun QuizScreen(
    lesson: LessonDocument,
    onFinished: (passed: Boolean) -> Unit,
    onBack: () -> Unit
) {
    var index by rememberSaveable { mutableIntStateOf(0) }
    var correct by rememberSaveable { mutableIntStateOf(0) }
    val questions = lesson.quiz

    FamilyScaffold(onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = DaplSpace.screen, vertical = DaplSpace.section),
            verticalArrangement = Arrangement.spacedBy(DaplSpace.moduleGap)
        ) {
            CenteredHeading("Quiz")
            when {
                questions.isEmpty() -> {
                    WhitePanel {
                        Text(
                            text = "Denne lektion har ikke en quiz.",
                            color = Navy,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                index >= questions.size -> {
                    val passed = QuizScoring.passed(correct, questions.size)
                    ProgressReadout(
                        label = "$correct / ${questions.size}",
                        fraction = if (questions.isEmpty()) 0f else correct.toFloat() / questions.size
                    )
                    WhitePanel {
                        Text(
                            text = if (passed) "Gennemført." else "Prøv igen.",
                            color = Navy,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    FamilyActionButton(
                        text = "Tilbage til lektionen",
                        container = ButtonBlue,
                        onClick = { onFinished(passed) }
                    )
                }
                else -> {
                    val question = questions[index]
                    ProgressReadout(
                        label = "${index + 1} / ${questions.size}",
                        fraction = (index + 1).toFloat() / questions.size.toFloat()
                    )
                    WhitePanel {
                        Text(
                            text = question.question,
                            color = Navy,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    question.options.forEachIndexed { optionIndex, option ->
                        FamilyActionButton(
                            text = option,
                            container = ButtonBlue,
                            onClick = {
                                if (optionIndex == question.answerIndex) correct++
                                index++
                            }
                        )
                    }
                }
            }
        }
    }
}
