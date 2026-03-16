package com.maxjth.tracememoire.ui.systeme.Galaxie

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun GalaxyOrbitLines(
    modifier: Modifier = Modifier,
    starAlpha: Float = 0.74f,
    percent: Int = 50
) {
    val inf = rememberInfiniteTransition(label = "galaxy_orbit_lines")

    val starPulse by inf.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starPulse"
    )

    val anchorPulse by inf.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anchorPulse"
    )

    val fieldDrift by inf.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fieldDrift"
    )

    val haloPulse by inf.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloPulse"
    )

    val deepOrbit by inf.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 54000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "deepOrbit"
    )

    val midOrbit by inf.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 34000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "midOrbit"
    )

    val innerOrbit by inf.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "innerOrbit"
    )

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val w = size.width
        val h = size.height
        val shortest = min(w, h)

        val center = Offset(
            x = w * 0.5f,
            y = h * 0.585f
        )

        val safePercent = percent.coerceIn(0, 100)
        val normalizedPercent = (safePercent / 100f) - 0.5f

        // bas = plus bas, haut = plus haut
        val reactiveYOffset = -shortest * 0.080f * normalizedPercent

        val driftX = shortest * 0.0030f * fieldDrift
        val driftY = shortest * 0.0022f * fieldDrift

        val shiftedCenter = Offset(
            x = center.x + driftX,
            y = center.y + driftY + reactiveYOffset
        )

        // ─────────────────────────────────────────────
        // ANCRAGES LATÉRAUX DISCRETS
        // ─────────────────────────────────────────────
        val leftAnchor = Offset(w * 0.242f + driftX, h * 0.515f + driftY)
        val rightAnchor = Offset(w * 0.758f + driftX, h * 0.515f + driftY)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    MAUVE.copy(alpha = 0.11f * anchorPulse),
                    Color.Transparent
                ),
                center = leftAnchor,
                radius = shortest * 0.026f * anchorPulse
            ),
            radius = shortest * 0.026f * anchorPulse,
            center = leftAnchor
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    TURQUOISE.copy(alpha = 0.11f * anchorPulse),
                    Color.Transparent
                ),
                center = rightAnchor,
                radius = shortest * 0.026f * anchorPulse
            ),
            radius = shortest * 0.026f * anchorPulse,
            center = rightAnchor
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.42f * anchorPulse),
            radius = shortest * 0.0032f,
            center = leftAnchor
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.42f * anchorPulse),
            radius = shortest * 0.0032f,
            center = rightAnchor
        )

        // ─────────────────────────────────────────────
        // GALAXIE CENTRALE — PLUS NETTE, MOINS BRUMEUSE
        // ─────────────────────────────────────────────
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    MAUVE.copy(alpha = 0.040f * haloPulse),
                    TURQUOISE.copy(alpha = 0.024f * haloPulse),
                    Color.Transparent
                ),
                center = shiftedCenter,
                radius = shortest * 0.16f * haloPulse
            ),
            radius = shortest * 0.16f * haloPulse,
            center = shiftedCenter
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.024f * haloPulse),
                    Color.Transparent
                ),
                center = Offset(
                    shiftedCenter.x,
                    shiftedCenter.y + shortest * 0.045f
                ),
                radius = shortest * 0.082f * haloPulse
            ),
            radius = shortest * 0.082f * haloPulse,
            center = Offset(
                shiftedCenter.x,
                shiftedCenter.y + shortest * 0.045f
            )
        )

        // noyau central net
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.70f * haloPulse),
                    Color.White.copy(alpha = 0.18f * haloPulse),
                    Color.Transparent
                ),
                center = shiftedCenter,
                radius = shortest * 0.030f
            ),
            radius = shortest * 0.030f,
            center = shiftedCenter
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.95f),
            radius = shortest * 0.0062f,
            center = shiftedCenter
        )

        // ─────────────────────────────────────────────
        // PARTICULES VIVANTES EN ORBITE
        // ─────────────────────────────────────────────
        val orbit1 = Offset(
            x = shiftedCenter.x + cos(deepOrbit) * shortest * 0.126f,
            y = shiftedCenter.y + sin(deepOrbit) * shortest * 0.052f
        )

        val orbit2 = Offset(
            x = shiftedCenter.x + cos(deepOrbit + 2.2f) * shortest * 0.098f,
            y = shiftedCenter.y + sin(deepOrbit + 2.2f) * shortest * 0.040f
        )

        val orbit3 = Offset(
            x = shiftedCenter.x + cos(midOrbit + 0.85f) * shortest * 0.078f,
            y = shiftedCenter.y + sin(midOrbit + 0.85f) * shortest * 0.032f
        )

        val orbit4 = Offset(
            x = shiftedCenter.x + cos(midOrbit + 3.4f) * shortest * 0.068f,
            y = shiftedCenter.y + sin(midOrbit + 3.4f) * shortest * 0.028f
        )

        val orbit5 = Offset(
            x = shiftedCenter.x + cos(innerOrbit + 1.6f) * shortest * 0.050f,
            y = shiftedCenter.y + sin(innerOrbit + 1.6f) * shortest * 0.020f
        )

        val orbit6 = Offset(
            x = shiftedCenter.x + cos(innerOrbit + 4.5f) * shortest * 0.058f,
            y = shiftedCenter.y + sin(innerOrbit + 4.5f) * shortest * 0.024f
        )

        val livingParticles = listOf(
            Triple(orbit1, shortest * 0.0046f, Color.White.copy(alpha = 0.74f)),
            Triple(orbit2, shortest * 0.0037f, MAUVE.copy(alpha = 0.72f)),
            Triple(orbit3, shortest * 0.0032f, TURQUOISE.copy(alpha = 0.76f)),
            Triple(orbit4, shortest * 0.0028f, Color.White.copy(alpha = 0.54f)),
            Triple(orbit5, shortest * 0.0024f, TURQUOISE.copy(alpha = 0.56f)),
            Triple(orbit6, shortest * 0.0022f, MAUVE.copy(alpha = 0.50f))
        )

        livingParticles.forEach { (pos, radius, color) ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = color.alpha * 0.20f * starPulse),
                        Color.Transparent
                    ),
                    center = pos,
                    radius = radius * 4.8f
                ),
                radius = radius * 4.8f,
                center = pos
            )

            drawCircle(
                color = color.copy(alpha = color.alpha * starPulse.coerceAtMost(1.08f)),
                radius = radius,
                center = pos
            )
        }

        // ─────────────────────────────────────────────
        // ÉTOILES PRINCIPALES
        // ─────────────────────────────────────────────
        val stars = listOf(
            Triple(
                Offset(shiftedCenter.x, h * 0.656f + driftY),
                shortest * 0.0070f,
                Color.White
            ),
            Triple(
                Offset(w * 0.424f + driftX, h * 0.726f + driftY),
                shortest * 0.0056f,
                MAUVE
            ),
            Triple(
                Offset(w * 0.570f + driftX, h * 0.730f + driftY),
                shortest * 0.0048f,
                TURQUOISE
            ),
            Triple(
                Offset(w * 0.493f + driftX, h * 0.779f + driftY),
                shortest * 0.0038f,
                Color.White
            ),
            Triple(
                Offset(w * 0.456f + driftX, h * 0.704f + driftY),
                shortest * 0.0029f,
                Color.White.copy(alpha = 0.88f)
            ),
            Triple(
                Offset(w * 0.540f + driftX, h * 0.694f + driftY),
                shortest * 0.0026f,
                TURQUOISE.copy(alpha = 0.84f)
            ),
            Triple(
                Offset(w * 0.520f + driftX, h * 0.744f + driftY),
                shortest * 0.0022f,
                MAUVE.copy(alpha = 0.80f)
            ),
            Triple(
                Offset(w * 0.468f + driftX, h * 0.762f + driftY),
                shortest * 0.0019f,
                Color.White.copy(alpha = 0.72f)
            )
        )

        stars.forEach { (pos, radius, color) ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = starAlpha * 0.18f * starPulse),
                        Color.Transparent
                    ),
                    center = pos,
                    radius = radius * 3.5f * starPulse
                ),
                radius = radius * 3.5f * starPulse,
                center = pos
            )

            drawCircle(
                color = color.copy(alpha = starAlpha * starPulse.coerceAtMost(1.08f)),
                radius = radius,
                center = pos
            )
        }

        // ─────────────────────────────────────────────
        // MICRO-POUSSIÈRES FINES
        // ─────────────────────────────────────────────
        val dust = listOf(
            Offset(shiftedCenter.x - shortest * 0.050f, shiftedCenter.y + shortest * 0.034f),
            Offset(shiftedCenter.x - shortest * 0.030f, shiftedCenter.y + shortest * 0.056f),
            Offset(shiftedCenter.x + shortest * 0.022f, shiftedCenter.y + shortest * 0.050f),
            Offset(shiftedCenter.x + shortest * 0.046f, shiftedCenter.y + shortest * 0.028f),
            Offset(shiftedCenter.x + shortest * 0.010f, shiftedCenter.y + shortest * 0.072f),
            Offset(shiftedCenter.x - shortest * 0.012f, shiftedCenter.y + shortest * 0.080f)
        )

        dust.forEachIndexed { index, pos ->
            val radius = when (index) {
                0 -> shortest * 0.0025f
                1 -> shortest * 0.0021f
                2 -> shortest * 0.0023f
                3 -> shortest * 0.0019f
                4 -> shortest * 0.0026f
                else -> shortest * 0.0020f
            }

            val color = when (index) {
                0 -> MAUVE.copy(alpha = 0.18f)
                1 -> Color.White.copy(alpha = 0.14f)
                2 -> TURQUOISE.copy(alpha = 0.18f)
                3 -> Color.White.copy(alpha = 0.12f)
                4 -> MAUVE.copy(alpha = 0.14f)
                else -> TURQUOISE.copy(alpha = 0.12f)
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color,
                        Color.Transparent
                    ),
                    center = pos,
                    radius = radius * 3.4f
                ),
                radius = radius * 3.4f,
                center = pos
            )
        }
    }
}