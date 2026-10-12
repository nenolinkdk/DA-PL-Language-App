package dk.nenolink.dapl.ui.grammar

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dk.nenolink.dapl.domain.content.ScreenModel
import dk.nenolink.dapl.domain.model.GrammarSheet
import dk.nenolink.dapl.ui.common.CenteredHeading
import dk.nenolink.dapl.ui.common.FamilyScaffold
import dk.nenolink.dapl.ui.common.WhitePanel
import dk.nenolink.dapl.ui.theme.DaplSpace
import dk.nenolink.dapl.ui.theme.Navy
import dk.nenolink.dapl.ui.theme.NavyMuted

@Composable
fun GrammarScreen(
    model: ScreenModel,
    sheets: List<GrammarSheet>,
    onBack: () -> Unit
) {
    FamilyScaffold(onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = DaplSpace.screen, vertical = DaplSpace.section),
            verticalArrangement = Arrangement.spacedBy(DaplSpace.moduleGap)
        ) {
            CenteredHeading(model.title)
            if (model.body.isNotBlank()) {
                WhitePanel {
                    Text(
                        text = model.body,
                        color = Navy,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            sheets.forEach { sheet ->
                WhitePanel {
                    Text(
                        text = sheet.title,
                        color = Navy,
                        style = MaterialTheme.typography.titleMedium
                    )
                    sheet.sections.forEach { section ->
                        Text(
                            text = section.heading,
                            color = Navy,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = section.body,
                            color = Navy,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        section.examples.forEach { example ->
                            Text(
                                text = example.polish,
                                color = Navy,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = example.danish,
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
}
