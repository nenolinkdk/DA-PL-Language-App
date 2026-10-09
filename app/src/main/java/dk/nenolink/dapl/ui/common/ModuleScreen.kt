package dk.nenolink.dapl.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.nenolink.dapl.domain.content.ScreenModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleScreen(
    model: ScreenModel,
    onBack: () -> Unit,
    onLesson: (String) -> Unit = {},
    onScenario: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(model.title) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Tilbage") }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (model.body.isNotBlank()) {
                Text(text = model.body, style = MaterialTheme.typography.bodyLarge)
            }
            model.lessons.forEach { lesson ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLesson(lesson.id) }
                ) {
                    ListItem(
                        headlineContent = { Text(lesson.title) },
                        supportingContent = { Text(lesson.statusLabel) }
                    )
                }
            }
            model.scenarios.forEach { scenario ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onScenario(scenario.id) }
                ) {
                    ListItem(headlineContent = { Text(scenario.title) })
                }
            }
        }
    }
}
