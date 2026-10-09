package dk.nenolink.dapl.ui.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dk.nenolink.dapl.domain.content.MenuEntryModel
import dk.nenolink.dapl.domain.model.AppRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    entries: List<MenuEntryModel>,
    onOpen: (AppRoute) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("DA-PL") })
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Text(
                        text = "Praktisk polsk for dansktalende",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Dansk er støttesprog · polsk er målsprog",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            items(entries, key = { it.route.wire }) { entry ->
                ListItem(
                    headlineContent = { Text(entry.label) },
                    supportingContent = { Text(entry.detail) },
                    modifier = Modifier.clickable { onOpen(entry.route) }
                )
                HorizontalDivider()
            }
        }
    }
}
