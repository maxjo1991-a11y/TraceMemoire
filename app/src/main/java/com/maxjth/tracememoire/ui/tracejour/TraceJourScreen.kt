package com.maxjth.tracememoire.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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

@Composable
fun TriangleOutlineBreathing(
    percent: Int,
    isInteracting: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00B3A4)
) {
    val pct = percent.coerceIn(0, 100)
    val tPct = pct / 100f

    // Durée un peu liée au % (plus “posé” quand c’est haut)
    val baseDuration = (6200 - (tPct * 1400)).toInt().coerceIn(4200, 6200)

    val transition = rememberInfiniteTransition(label = "triangle-life")

    // Souffle principal
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = baseDuration,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "triangle-phase"
    )

    // Drift lent (effet “gravite” très doux)
    val drift by transition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (baseDuration * 2.4f).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "triangle-drift"
    )

    // Amplitudes (un peu + quand tu touches)
    val amp = if (isInteracting) 0.055f else 0.040f
    val baseScale = 0.96f + (amp * phase)

    // Micro-vie (très léger)
    val micro = 1f + (drift * (if (isInteracting) 0.0075f else 0.0055f))

    val finalScale = baseScale * micro

    // Épaisseur vivante (respire avec l’interaction)
    val strokeDp = (3.dp + (if (isInteracting) 1.4.dp else 0.8.dp) * phase)

    Canvas(modifier = modifier) {
        val sizeMin = min(size.width, size.height)

        // Petit flottement vertical (gravité)
        val floatY = (if (isInteracting) 3.5f else 2.2f) * drift

        val center = Offset(size.width / 2f, size.height / 2f + floatY)
        val radius = sizeMin * 0.42f * finalScale

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
                width = strokeDp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}