package com.maxjth.tracememoire.ui.tracejour.components.screen.percent

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun PercentBadge(
    percent: Int,
    baseAlpha: Float = 0.92f,
    fontSizeSp: Int = 17
) {
    val pct = percent.coerceIn(0, 100)

    val t = rememberInfiniteTransition(label = "percent_breath")
    val a by t.animateFloat(
        initialValue = (baseAlpha - 0.06f).coerceIn(0f, 1f),
        targetValue = (baseAlpha + 0.04f).coerceIn(0f, 1f),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "percent_alpha"
    )

    Text(
        text = "$pct%",
        color = Color.White.copy(alpha = a),
        fontSize = fontSizeSp.sp,
        fontWeight = FontWeight.SemiBold
    )
}