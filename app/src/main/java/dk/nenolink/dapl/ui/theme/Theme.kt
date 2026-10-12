package dk.nenolink.dapl.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FamilyColors = lightColorScheme(
    primary = Navy,
    onPrimary = Cream,
    secondary = QuizRed,
    onSecondary = Navy,
    tertiary = ButtonBlue,
    onTertiary = Navy,
    background = Cream,
    onBackground = Navy,
    surface = PanelWhite,
    onSurface = Navy,
    surfaceVariant = ButtonBlue,
    onSurfaceVariant = NavyMuted
)

@Composable
fun DaplTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FamilyColors,
        typography = DaplTypography,
        content = content
    )
}
