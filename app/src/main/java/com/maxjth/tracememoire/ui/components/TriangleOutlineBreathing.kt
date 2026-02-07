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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun TriangleOutlineBreathing(
    percent: Int,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00B3A4), // turquoise doux
    isInteracting: Boolean = false
) {
    val pct = percent.coerceIn(0, 100)
    val t = pct / 100f

    // Rythme: plus le % est haut -> un peu plus rapide + plus ample
    val baseDuration = lerpInt(7400, 4200, t)
    val duration = when {
        pct >= 100 -> (baseDuration * 0.65f).roundToInt()
        isInteracting -> (baseDuration * 0.80f).roundToInt()
        pct == 0 -> 9000
        else -> baseDuration
    }

    // Amplitude: visible mais “sacrée”
    val minScale = lerpFloat(0.945f, 0.965f, t)   // 0% -> plus calme
    val maxScale = lerpFloat(1.015f, 1.045f, t)   // 100% -> plus vivant

    val transition = rememberInfiniteTransition(label = "triangle-life")

    // Respiration principale (doux)
    val breath by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    // Micro-vie (irrégularité douce)
    val micro by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (duration * 2.4f).roundToInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micro"
    )

    // Micro-orbite (gravité) — TRÈS LENTE
    val orbitPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration * 6, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    // Tilt ultra lent (gravité) — 0.2° -> 0.4°
    val tiltPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration * 8, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tilt"
    )

    val scale = lerpFloat(minScale, maxScale, breath) * (1f + (micro - 0.5f) * 0.0075f)

    // Orbite: 2.4dp -> 3.6dp selon %
    val orbitRadiusDp = lerpFloat(2.4f, 3.6f, t)
    val orbitX = (cos(orbitPhase.toDouble()) * orbitRadiusDp).toFloat()
    val orbitY = (sin(orbitPhase.toDouble()) * orbitRadiusDp * 0.70f).toFloat()

    // Halo: plus visible quand ça “inspire”
    val haloA = lerpFloat(0.08f, 0.22f, breath) * (0.75f + 0.35f * t)
    val hiA = lerpFloat(0.10f, 0.26f, breath) * (0.65f + 0.40f * t)

    // Stroke qui respire un peu
    val strokeDp =
        lerpFloat(2.6f, 3.4f, t) +
                lerpFloat(0f, 1.2f, breath) * (0.25f + 0.45f * t)

    Canvas(modifier = modifier) {
        val sizeMin = min(size.width, size.height)
        val center = Offset(size.width / 2f, size.height / 2f)

        val c = Offset(
            center.x + orbitX.dp.toPx(),
            center.y + orbitY.dp.toPx()
        )

        // Tilt: 0.2° -> 0.4° selon le %
        val maxTiltDeg = lerpFloat(0.2f, 0.4f, t)
        val rotationDeg =
            (sin(tiltPhase.toDouble()) * maxTiltDeg).toFloat() *
                    (0.85f + 0.15f * breath)

        rotate(rotationDeg, pivot = c) {

            // ✅ Mise à jour: triangle plus grand (0.42f -> 0.46f)
            val radius = sizeMin * 0.46f * scale

            val path = Path().apply {
                moveTo(c.x, c.y - radius)
                lineTo(c.x - radius * 0.87f, c.y + radius * 0.5f)
                lineTo(c.x + radius * 0.87f, c.y + radius * 0.5f)
                close()
            }

            // Halo (2 couches)
            drawPath(
                path = path,
                color = color.copy(alpha = haloA),
                style = Stroke(
                    width = (strokeDp + 14f).dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            drawPath(
                path = path,
                color = color.copy(alpha = haloA * 0.75f),
                style = Stroke(
                    width = (strokeDp + 7f).dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Trait principal + highlight
            drawPath(
                path = path,
                brush = Brush.sweepGradient(
                    colors = listOf(
                        color.copy(alpha = 0.92f),
                        Color.White.copy(alpha = hiA),
                        color.copy(alpha = 0.92f)
                    ),
                    center = c
                ),
                style = Stroke(
                    width = strokeDp.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

// Helpers locaux
private fun lerpFloat(a: Float, b: Float, t: Float): Float =
    a + (b - a) * t.coerceIn(0f, 1f)

private fun lerpInt(a: Int, b: Int, t: Float): Int =
    (a + (b - a) * t.coerceIn(0f, 1f)).roundToInt()