package dk.nenolink.dapl.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dk.nenolink.dapl.ui.theme.ButtonBlue
import dk.nenolink.dapl.ui.theme.Cream
import dk.nenolink.dapl.ui.theme.DaplSpace
import dk.nenolink.dapl.ui.theme.Navy
import dk.nenolink.dapl.ui.theme.NavyMuted
import dk.nenolink.dapl.ui.theme.PanelWhite
import dk.nenolink.dapl.ui.theme.ProgressTrack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyScaffold(
    onBack: (() -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = Cream,
        topBar = {
            if (onBack != null) {
                TopAppBar(
                    title = {},
                    navigationIcon = { BackButton(onClick = onBack) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Cream,
                        navigationIconContentColor = Navy
                    )
                )
            }
        },
        content = content
    )
}

@Composable
fun BackButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.defaultMinSize(minWidth = DaplSpace.minTouch, minHeight = DaplSpace.minTouch),
        colors = ButtonDefaults.textButtonColors(contentColor = Navy)
    ) {
        Text("Tilbage", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun CenteredHeading(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    color: Color = Navy
) {
    Text(
        text = text,
        modifier = modifier.fillMaxWidth(),
        color = color,
        textAlign = TextAlign.Center,
        style = style
    )
}

@Composable
fun ModuleButton(
    label: String,
    detail: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    container: Color = ButtonBlue
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = DaplSpace.buttonHeight),
        shape = RoundedCornerShape(DaplSpace.corner),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = Navy
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = Navy
            )
            if (detail.isNotBlank()) {
                Text(
                    text = detail,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = NavyMuted
                )
            }
        }
    }
}

@Composable
fun FamilyActionButton(
    text: String,
    container: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    expand: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .then(if (expand) Modifier.fillMaxWidth() else Modifier)
            .heightIn(min = if (expand) DaplSpace.buttonHeight else DaplSpace.minTouch),
        shape = RoundedCornerShape(DaplSpace.corner),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = Navy,
            disabledContainerColor = container.copy(alpha = 0.45f),
            disabledContentColor = Navy.copy(alpha = 0.55f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WhitePanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(DaplSpace.panelCorner))
            .background(PanelWhite)
            .padding(DaplSpace.section),
        verticalArrangement = Arrangement.spacedBy(DaplSpace.tight),
        content = content
    )
}

@Composable
fun ProgressReadout(label: String, fraction: Float, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(DaplSpace.tight),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth(),
            color = Navy,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = { fraction.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(DaplSpace.progressHeight)
                .clip(RoundedCornerShape(DaplSpace.progressHeight / 2)),
            color = Navy,
            trackColor = ProgressTrack,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun CircleStepButton(
    label: String,
    symbol: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val fill = if (enabled) ButtonBlue else ButtonBlue.copy(alpha = 0.4f)
    Box(
        modifier = Modifier
            .size(DaplSpace.circle)
            .clip(CircleShape)
            .background(fill)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics {
                role = Role.Button
                contentDescription = label
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            modifier = Modifier.clearAndSetSemantics {},
            color = if (enabled) Navy else Navy.copy(alpha = 0.45f),
            fontSize = 28.sp
        )
    }
}
