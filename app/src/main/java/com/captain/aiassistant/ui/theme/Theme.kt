package com.captain.aiassistant.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Light scheme: screens sit on the colorful gradient, so on-screen text
// is white; white cards carry their own dark text explicitly.
private val DesignBColors = lightColorScheme(
    primary = GradientPurple,
    secondary = GradientPink,
    tertiary = GradientBlue,
    background = Color.White,
    surface = CardWhite,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = InkText
)

// Big rounded shapes everywhere — cheerful, modern, rounded.
private val AppShapes = Shapes(
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(24.dp),
    large = RoundedCornerShape(32.dp)
)

/** Design B look: vibrant gradient + white rounded cards. */
@Composable
fun AiAssistantTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DesignBColors,
        shapes = AppShapes,
        content = content
    )
}
