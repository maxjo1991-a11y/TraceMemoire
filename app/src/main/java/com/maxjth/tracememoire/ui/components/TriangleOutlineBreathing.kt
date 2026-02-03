package com.maxjth.tracememoire.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.min
import androidx.compose.animation.core.animateFloat

@Composable
fun TriangleOutlineBreathing(
    percent: Int,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00B3A4) // turquoise doux par défaut
) {
    // (percent gardé pour plus tard si tu veux lier vitesse/couleur au %)
    percent.coerceIn(0, 100)


    val transition = rememberInfiniteTransition(label = "triangle-breath")

    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "triangle-phase"
    )

    // Amplitude très contrôlée (sacrée)
    val scale = 0.96f + (0.04f * phase)

    Canvas(modifier = modifier) {
        val sizeMin = min(size.width, size.height)
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = sizeMin * 0.42f * scale

        val path = Path().apply {
            moveTo(center.x, center.y - radius)
            lineTo(center.x - radius * 0.87f, center.y + radius * 0.5f)
            lineTo(center.x + radius * 0.87f, center.y + radius * 0.5f)
            close()
        }

        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}