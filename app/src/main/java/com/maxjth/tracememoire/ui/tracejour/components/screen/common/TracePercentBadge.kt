package com.maxjth.tracememoire.ui.tracejour.components.screen.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TracePercentBadge(
    percent: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    sizeDp: Int = 52
) {
    val pct = percent.coerceIn(0, 100)

    val bg = BG_SOFT.copy(alpha = if (enabled) 0.34f else 0.22f)
    val ringWidth = if (enabled) 3.6.dp else 2.dp

    val ringBrush = Brush.linearGradient(
        colors = listOf(
            MAUVE.copy(alpha = if (enabled) 0.95f else 0.45f),
            TURQUOISE.copy(alpha = if (enabled) 0.18f else 0.06f),
            MAUVE.copy(alpha = if (enabled) 0.90f else 0.40f)
        )
    )

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(bg)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = if (enabled) 0.38f else 0.25f),
                shape = CircleShape
            )
            .border(
                width = ringWidth,
                brush = ringBrush,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$pct%",
            color = WHITE_SOFT.copy(alpha = if (enabled) 0.98f else 0.60f),
            fontSize = if (enabled) 20.sp else 18.sp,
            fontWeight = FontWeight.SemiBold,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.55f),
                    blurRadius = 12f,
                    offset = Offset(0f, 2f)
                )
            )
        )
    }
}