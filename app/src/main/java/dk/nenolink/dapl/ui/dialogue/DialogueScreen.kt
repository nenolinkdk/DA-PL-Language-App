package dk.nenolink.dapl.ui.dialogue

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dk.nenolink.dapl.domain.dialogue.DialogueMachine
import dk.nenolink.dapl.domain.dialogue.DialogueScenario
import dk.nenolink.dapl.tts.SpeechLocale
import dk.nenolink.dapl.tts.SpeechRequest
import dk.nenolink.dapl.tts.TextToSpeechGateway
import dk.nenolink.dapl.ui.common.CenteredHeading
import dk.nenolink.dapl.ui.common.FamilyActionButton
import dk.nenolink.dapl.ui.common.FamilyScaffold
import dk.nenolink.dapl.ui.common.WhitePanel
import dk.nenolink.dapl.ui.theme.ButtonBlue
import dk.nenolink.dapl.ui.theme.DaplSpace
import dk.nenolink.dapl.ui.theme.Navy
import dk.nenolink.dapl.ui.theme.NavyMuted

@Composable
fun DialogueScreen(
    scenario: DialogueScenario,
    speech: TextToSpeechGateway,
    onCompleted: () -> Unit,
    onExit: () -> Unit
) {
    val machine = remember(scenario.id) { DialogueMachine(scenario) }
    var revision by remember(scenario.id) { mutableIntStateOf(0) }
    var showSupport by remember(scenario.id) { mutableStateOf(false) }
    val state = machine.state.also { revision }
    val polishLine = state.spokenPolish()

    FamilyScaffold(onBack = onExit) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = DaplSpace.screen, vertical = DaplSpace.section),
            verticalArrangement = Arrangement.spacedBy(DaplSpace.moduleGap)
        ) {
            CenteredHeading(scenario.title)
            if (scenario.description.isNotBlank()) {
                WhitePanel {
                    Text(
                        text = scenario.description,
                        color = Navy,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            WhitePanel {
                Text(
                    text = polishLine,
                    color = Navy,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showSupport && state.support.isNotBlank()) {
                    Text(
                        text = state.support,
                        color = NavyMuted,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            FamilyActionButton(
                text = "Hør polsk",
                container = ButtonBlue,
                onClick = {
                    speech.speak(
                        SpeechRequest(
                            text = polishLine,
                            locale = SpeechLocale.POLISH,
                            requestedByUser = true
                        )
                    )
                }
            )
            if (state.support.isNotBlank()) {
                FamilyActionButton(
                    text = if (showSupport) "Skjul dansk" else "Vis dansk",
                    container = ButtonBlue,
                    onClick = { showSupport = !showSupport }
                )
            }
            if (showSupport && state.support.isNotBlank()) {
                FamilyActionButton(
                    text = "Hør dansk",
                    container = ButtonBlue,
                    onClick = {
                        speech.speak(
                            SpeechRequest(
                                text = state.support,
                                locale = SpeechLocale.DANISH,
                                requestedByUser = true
                            )
                        )
                    }
                )
            }
            if (machine.isTerminal) {
                Text(
                    text = "Samtalen er færdig.",
                    color = Navy,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                FamilyActionButton(
                    text = "Kør igen",
                    container = ButtonBlue,
                    onClick = {
                        machine.reset()
                        revision++
                    }
                )
                FamilyActionButton(
                    text = "Gem og luk",
                    container = ButtonBlue,
                    onClick = onCompleted
                )
            } else {
                state.options.forEachIndexed { index, option ->
                    FamilyActionButton(
                        text = option.text,
                        container = ButtonBlue,
                        onClick = {
                            machine.chooseOption(index)
                            revision++
                        }
                    )
                    if (showSupport && option.support.isNotBlank()) {
                        Text(
                            text = option.support,
                            color = NavyMuted,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
