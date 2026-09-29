package dk.nenolink.dapl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dk.nenolink.dapl.ui.navigation.DaplApp
import dk.nenolink.dapl.ui.theme.DaplTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DaplTheme {
                DaplApp()
            }
        }
    }
}
