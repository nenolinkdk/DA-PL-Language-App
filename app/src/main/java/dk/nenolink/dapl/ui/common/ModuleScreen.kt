package dk.nenolink.dapl.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dk.nenolink.dapl.domain.content.ScreenModel
import dk.nenolink.dapl.ui.theme.DaplSpace
import dk.nenolink.dapl.ui.theme.Navy

@Composable
fun ModuleScreen(
    model: ScreenModel,
    onBack: () -> Unit,
    onLesson: (String) -> Unit = {},
    onScenario: (String) -> Unit = {}
) {
    val released = model.lessons.filter { it.released }
    val done = released.count { it.completed }
    FamilyScaffold(onBack = onBack) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = DaplSpace.screen, vertical = DaplSpace.section),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DaplSpace.moduleGap)
        ) {
            CenteredHeading(model.title)
            if (released.isNotEmpty()) {
                ProgressReadout(
                    label = "$done/${released.size} gennemført",
                    fraction = done.toFloat() / released.size.toFloat()
                )
            }
            if (model.body.isNotBlank()) {
                WhitePanel {
                    Text(
                        text = model.body,
                        color = Navy,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            model.lessons.forEach { lesson ->
                ModuleButton(
                    label = lesson.title,
                    detail = lesson.statusLabel,
                    onClick = { onLesson(lesson.id) }
                )
            }
            model.scenarios.forEach { scenario ->
                ModuleButton(
                    label = scenario.title,
                    detail = "Samtale",
                    onClick = { onScenario(scenario.id) }
                )
            }
        }
    }
}
