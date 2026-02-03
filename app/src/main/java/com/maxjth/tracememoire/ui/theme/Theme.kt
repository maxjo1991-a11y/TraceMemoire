package com.maxjth.tracememoire.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TraceDarkColors = darkColorScheme(
    primary = TURQUOISE,
    onPrimary = Color.White,

    secondary = MAUVE,
    onSecondary = Color.White,

    background = BG_DEEP,
    onBackground = WHITE_SOFT,

    surface = BG_SOFT,
    onSurface = WHITE_SOFT,

    surfaceVariant = Color.White.copy(alpha = 0.06f),
    onSurfaceVariant = WHITE_SOFT.copy(alpha = 0.85f),

    outline = Color.White.copy(alpha = 0.12f)
)

@Composable
fun TraceMemoireTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TraceDarkColors,
        typography = Typography,
        content = content
    )
}