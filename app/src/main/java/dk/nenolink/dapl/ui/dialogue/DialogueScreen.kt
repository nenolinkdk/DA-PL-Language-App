package dk.nenolink.dapl.ui.dialogue

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.nenolink.dapl.domain.dialogue.DialogueMachine
import dk.nenolink.dapl.domain.dialogue.DialogueScenario
import dk.nenolink.dapl.tts.SpeechLocale
import dk.nenolink.dapl.tts.SpeechRequest
import dk.nenolink.dapl.tts.TextToSpeechGateway

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogueScreen(
    scenario: DialogueScenario,
    speech: TextToSpeechGateway,
    onCompleted: () -> Unit,
    onExit: () -> Unit
) {
    val machine = remember(scenario.id) { DialogueMachine(scenario) }
    var revision by remember(scenario.id) { mutableIntStateOf(0) }
    val state = machine.state.also { revision }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(scenario.title) },
                navigationIcon = { TextButton(onClick = onExit) { Text("Tilbage") } }
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
            if (scenario.description.isNotBlank()) {
                Text(scenario.description, style = MaterialTheme.typography.bodyMedium)
            }
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(state.prompt)
                    TextButton(onClick = {
                        speech.speak(
                            SpeechRequest(
                                text = state.prompt,
                                locale = SpeechLocale.POLISH,
                                requestedByUser = true
                            )
                        )
                    }) { Text("Hør") }
                }
            }
            if (machine.isTerminal) {
                Text("Samtalen er færdig.", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        machine.reset()
                        revision++
                    }) { Text("Kør igen") }
                    Button(onClick = onCompleted) { Text("Gem og luk") }
                }
            } else {
                state.options.forEachIndexed { index, option ->
                    OutlinedButton(
                        onClick = {
                            machine.chooseOption(index)
                            revision++
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(option.text) }
                }
            }
        }
    }
}
