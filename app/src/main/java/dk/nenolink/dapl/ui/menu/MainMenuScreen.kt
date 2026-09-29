package dk.nenolink.dapl.ui.menu

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dk.nenolink.dapl.ui.navigation.Routes

data class MenuEntry(
    val label: String,
    val route: String
)

private val menuEntries = listOf(
    MenuEntry("Niveau 1", Routes.LEVEL1),
    MenuEntry("Niveau 2", Routes.LEVEL2),
    MenuEntry("Niveau 3", Routes.LEVEL3),
    MenuEntry("Samtaletræning", Routes.CONVERSATION),
    MenuEntry("Quiz / repetition", Routes.QUIZ),
    MenuEntry("Grammatik", Routes.GRAMMAR),
    MenuEntry("Børn", Routes.CHILDREN),
    MenuEntry("Dokumentation / Om", Routes.ABOUT)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onModuleSelected: (String) -> Unit
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
            items(menuEntries) { entry ->
                ListItem(
                    headlineContent = { Text(entry.label) },
                    modifier = Modifier.clickable { onModuleSelected(entry.route) }
                )
            }
        }
    }
}
