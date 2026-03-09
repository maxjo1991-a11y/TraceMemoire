package com.maxjth.tracememoire.ui.systeme.Galaxie

import androidx.compose.animation.core.FastOutSlowInEasing
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
import kotlin.math.min

@Composable
fun GalaxyOrbitLines(
    modifier: Modifier = Modifier,
    starAlpha: Float = 0.68f
) {
    val inf = rememberInfiniteTransition(label = "galaxy_orbit_lines")

    val starPulse by inf.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "starPulse"
    )

    val anchorPulse by inf.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "anchorPulse"
    )

    val fieldDrift by inf.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fieldDrift"
    )

    val haloPulse by inf.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10_000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloPulse"
    )

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val w = size.width
        val h = size.height
        val shortest = min(w, h)

        val centerX = w * 0.5f
        val centerY = h * 0.585f

        val driftX = shortest * 0.0022f * fieldDrift
        val driftY = shortest * 0.0016f * fieldDrift

        // Ancres latérales discrètes (sans ligne)
        val leftAnchor = Offset(w * 0.245f + driftX, h * 0.515f + driftY)
        val rightAnchor = Offset(w * 0.755f + driftX, h * 0.515f + driftY)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    MAUVE.copy(alpha = 0.12f * anchorPulse),
                    Color.Transparent
                ),
                center = leftAnchor,
                radius = shortest * 0.022f * anchorPulse
            ),
            radius = shortest * 0.022f * anchorPulse,
            center = leftAnchor
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    TURQUOISE.copy(alpha = 0.12f * anchorPulse),
                    Color.Transparent
                ),
                center = rightAnchor,
                radius = shortest * 0.022f * anchorPulse
            ),
            radius = shortest * 0.022f * anchorPulse,
            center = rightAnchor
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.42f * anchorPulse),
            radius = shortest * 0.0036f,
            center = leftAnchor
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.42f * anchorPulse),
            radius = shortest * 0.0036f,
            center = rightAnchor
        )

        // Quelques points lumineux subtils seulement
        val stars = listOf(
            Triple(
                Offset(centerX + driftX, h * 0.655f + driftY),
                shortest * 0.0072f,
                Color.White
            ),
            Triple(
                Offset(w * 0.43f + driftX, h * 0.73f + driftY),
                shortest * 0.0050f,
                MAUVE
            ),
            Triple(
                Offset(w * 0.57f + driftX, h * 0.73f + driftY),
                shortest * 0.0050f,
                TURQUOISE
            ),
            Triple(
                Offset(w * 0.50f + driftX, h * 0.775f + driftY),
                shortest * 0.0038f,
                Color.White
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
                    radius = radius * 2.8f * starPulse
                ),
                radius = radius * 2.8f * starPulse,
                center = pos
            )

            drawCircle(
                color = color.copy(alpha = starAlpha * starPulse.coerceAtMost(1.08f)),
                radius = radius,
                center = pos
            )
        }

        // Halo central ultra doux
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    MAUVE.copy(alpha = 0.042f * haloPulse),
                    TURQUOISE.copy(alpha = 0.028f * haloPulse),
                    Color.Transparent
                ),
                center = Offset(centerX + driftX, centerY + driftY),
                radius = shortest * 0.14f * haloPulse
            ),
            radius = shortest * 0.14f * haloPulse,
            center = Offset(centerX + driftX, centerY + driftY)
        )
    }
}