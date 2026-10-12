package dk.nenolink.dapl.ui.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dk.nenolink.dapl.domain.content.MenuEntryModel
import dk.nenolink.dapl.domain.model.AppRoute
import dk.nenolink.dapl.ui.common.CenteredHeading
import dk.nenolink.dapl.ui.common.FamilyScaffold
import dk.nenolink.dapl.ui.common.ModuleButton
import dk.nenolink.dapl.ui.theme.DaplSpace
import dk.nenolink.dapl.ui.theme.NavyMuted

@Composable
fun MainMenuScreen(
    entries: List<MenuEntryModel>,
    onOpen: (AppRoute) -> Unit
) {
    FamilyScaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = DaplSpace.screen, vertical = DaplSpace.section),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(DaplSpace.moduleGap)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(DaplSpace.tight)
            ) {
                CenteredHeading("DA-PL")
                CenteredHeading(
                    text = "Praktisk polsk for dansktalende",
                    style = MaterialTheme.typography.titleMedium
                )
                CenteredHeading(
                    text = "Dansk er støttesprog · polsk er målsprog",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NavyMuted
                )
            }
            entries.forEach { entry ->
                ModuleButton(
                    label = entry.label,
                    detail = entry.detail,
                    onClick = { onOpen(entry.route) }
                )
            }
        }
    }
}
