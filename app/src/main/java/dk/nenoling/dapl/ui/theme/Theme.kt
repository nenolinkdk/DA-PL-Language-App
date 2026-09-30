package dk.nenoling.dapl.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NenoRed = Color(0xFFB3261E)
val NenoRedDark = Color(0xFF7F1D16)
val NenoCream = Color(0xFFFDF6EC)
val NenoCreamDark = Color(0xFF2A2620)

private val LightColors = lightColorScheme(
    primary = NenoRed,
    secondary = NenoRedDark,
    background = NenoCream,
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE57368),
    secondary = NenoRed,
    background = NenoCreamDark,
    surface = Color(0xFF3A342B)
)

@Composable
fun DaplTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
