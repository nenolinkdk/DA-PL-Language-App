package dk.nenolink.dapl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dk.nenolink.dapl.data.content.AssetContentRepository
import dk.nenolink.dapl.progress.ProgressStore
import dk.nenolink.dapl.tts.AndroidTextToSpeechGateway
import dk.nenolink.dapl.ui.navigation.DaplApp
import dk.nenolink.dapl.ui.theme.DaplTheme

class MainActivity : ComponentActivity() {
    private var speech: AndroidTextToSpeechGateway? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val library = AssetContentRepository(this).loadLibrary()
        val progress = ProgressStore(this)
        val gateway = AndroidTextToSpeechGateway(this)
        speech = gateway
        enableEdgeToEdge()
        setContent {
            DaplTheme {
                DaplApp(library = library, speech = gateway, progress = progress)
            }
        }
    }

    override fun onDestroy() {
        speech?.shutdown()
        speech = null
        super.onDestroy()
    }
}
