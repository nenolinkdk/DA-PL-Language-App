package dk.nenolink.dapl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dk.nenolink.dapl.data.content.AssetContentRepository
import dk.nenolink.dapl.tts.AndroidTextToSpeechGateway
import dk.nenolink.dapl.ui.navigation.DaplApp
import dk.nenolink.dapl.ui.theme.DaplTheme

class MainActivity : ComponentActivity() {
    private var speech: AndroidTextToSpeechGateway? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val catalog = AssetContentRepository(this).loadCatalog()
        speech = AndroidTextToSpeechGateway(this)
        enableEdgeToEdge()
        setContent {
            DaplTheme {
                DaplApp(catalog = catalog)
            }
        }
    }

    override fun onDestroy() {
        speech?.shutdown()
        speech = null
        super.onDestroy()
    }
}
