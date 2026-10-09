package dk.nenolink.dapl.ui.grammar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.nenolink.dapl.domain.content.ScreenModel
import dk.nenolink.dapl.domain.model.GrammarSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarScreen(
    model: ScreenModel,
    sheets: List<GrammarSheet>,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(model.title) },
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
            if (model.body.isNotBlank()) {
                Text(model.body, style = MaterialTheme.typography.bodyLarge)
            }
            sheets.forEach { sheet ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(sheet.title, style = MaterialTheme.typography.titleMedium)
                        sheet.sections.forEach { section ->
                            Text(section.heading, style = MaterialTheme.typography.titleSmall)
                            Text(section.body)
                            section.examples.forEach { example ->
                                Text("${example.danish}  ->  ${example.polish}")
                            }
                        }
                    }
                }
            }
        }
    }
}
