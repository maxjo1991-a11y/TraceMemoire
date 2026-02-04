package com.maxjth.tracememoire.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.utils.currentMonthlyBreath
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun HomeMemoryCircle(
    traceCount: Int,
    modifier: Modifier = Modifier
) {
    val text = traceCount.toString()

    val numberSize = when (text.length) {
        1 -> 108.sp
        2 -> 96.sp
        else -> 84.sp
    }

    val offsetY = when (text.length) {
        1 -> (-6).dp
        2 -> (-5).dp
        else -> (-4).dp
    }

    // 🌙 RYTHME DU MOIS (temps souverain)
    val breath = remember { currentMonthlyBreath() }

    // 🌬️ RESPIRATION + MICRO-VIE
    val tr = rememberInfiniteTransition(label = "home_circle_life")

    // Souffle principal (visible, lent)
    val mainBreath by tr.animateFloat(
        initialValue = breath.minScale,
        targetValue = breath.maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = breath.durationMs,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "main_breath"
    )

    // Micro-drift humain (très doux, lent)
    val microDrift by tr.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (breath.durationMs * 2.2f).roundToInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micro_drift"
    )

    // Mix final : petit +/- (ça se sent sans “vibrer”)
    val finalScale = mainBreath * (1f + (microDrift * 0.0065f))

    // Cercle légèrement plus épais quand il “inspire”
    val strokeBase = 8.dp
    val strokeBoost =
        ((finalScale - breath.minScale) / (breath.maxScale - breath.minScale)).coerceIn(0f, 1f)
    val strokeDp = strokeBase + (2.dp * strokeBoost)

    // 🌍 ORBITE LENTE (gravité / flottement)
    val orbitPhase by tr.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (breath.durationMs * 6.5f).roundToInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_phase"
    )

    // 2.6dp -> 3.6dp selon l’intensité (discret mais visible)
    val orbitRadiusDp = lerpFloat(2.6f, 3.6f, strokeBoost)
    val orbitXdp = (cos(orbitPhase.toDouble()) * orbitRadiusDp).toFloat()
    val orbitYdp = (sin(orbitPhase.toDouble()) * orbitRadiusDp * 0.70f).toFloat()

    // ✅ dp -> px (obligatoire pour graphicsLayer)
    val density = LocalDensity.current
    val orbitXpx = with(density) { orbitXdp.dp.toPx() }
    val orbitYpx = with(density) { orbitYdp.dp.toPx() }

    // 🧭 TILT ultra lent (0.25° -> 0.45°)
    val tiltPhase by tr.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (breath.durationMs * 8.0f).roundToInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt_phase"
    )
    val tiltDeg = lerpFloat(0.25f, 0.45f, strokeBoost) * tiltPhase

    // ✨ HALO (donne l’impression que ça flotte dans le noir)
    val haloA = 0.08f + 0.16f * strokeBoost // alpha du glow
    val haloR = 0.76f + 0.14f * strokeBoost // rayon du glow

    Box(
        modifier = modifier
            .size(240.dp)
            .graphicsLayer(
                scaleX = finalScale,
                scaleY = finalScale,
                translationX = orbitXpx,
                translationY = orbitYpx,
                rotationZ = tiltDeg
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {

            // Halo 1 (large)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MAUVE.copy(alpha = haloA),
                        Color.Transparent
                    ),
                    radius = size.minDimension * haloR
                ),
                radius = size.minDimension * haloR
            )

            // Halo 2 (plus serré)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        MAUVE.copy(alpha = haloA * 0.65f),
                        Color.Transparent
                    ),
                    radius = size.minDimension * (haloR * 0.86f)
                ),
                radius = size.minDimension * (haloR * 0.86f)
            )

            // Cercle principal
            drawCircle(
                color = MAUVE,
                style = Stroke(width = strokeDp.toPx())
            )
        }

        Text(
            text = text,
            fontSize = numberSize,
            fontWeight = FontWeight.ExtraBold,
            color = TURQUOISE,
            modifier = Modifier.offset(y = offsetY)
        )
    }
}

private fun lerpFloat(a: Float, b: Float, t: Float): Float =
    a + (b - a) * t.coerceIn(0f, 1f)