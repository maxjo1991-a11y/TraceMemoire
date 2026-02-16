package com.maxjth.tracememoire.ui.tracejour.components.screen.percent

import androidx.compose.animation.core.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun PercentBadge(
    percent: Int,
    baseAlpha: Float = 0.88f
) {
    val pct = percent.coerceIn(0, 100)

    val infiniteTransition = rememberInfiniteTransition(label = "percent_breath")

    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = baseAlpha - 0.06f,
        targetValue = baseAlpha + 0.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "percent_alpha"
    )

    Text(
        text = "$pct%",
        color = Color.White.copy(alpha = animatedAlpha),
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold
    )
}

