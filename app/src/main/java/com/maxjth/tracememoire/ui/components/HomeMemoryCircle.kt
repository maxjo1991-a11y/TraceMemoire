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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun HomeMemoryCircle(
    traceCount: Int,
    modifier: Modifier = Modifier,
    numberEntryScale: Float = 1f
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

    val breath = remember { currentMonthlyBreath() }
    val tr = rememberInfiniteTransition(label = "home_circle_life")

    val mainBreath by tr.animateFloat(
        initialValue = breath.minScale,
        targetValue = breath.maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = breath.durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "main_breath"
    )

    val microDrift by tr.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (breath.durationMs * 2.2f).roundToInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "micro_drift"
    )

    val finalScale = mainBreath * (1f + (microDrift * 0.0065f))

    val strokeBase = 8.dp
    val strokeBoost =
        ((finalScale - breath.minScale) / (breath.maxScale - breath.minScale)).coerceIn(0f, 1f)
    val strokeDp = strokeBase + (2.dp * strokeBoost)

    val orbitPhase by tr.animateFloat(
        initialValue = 0f,
        targetValue = (PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (breath.durationMs * 6.5f).roundToInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit_phase"
    )

    val orbitRadiusDp = lerpFloat(2.6f, 3.6f, strokeBoost)
    val orbitXdp = (cos(orbitPhase.toDouble()) * orbitRadiusDp).toFloat()
    val orbitYdp = (sin(orbitPhase.toDouble()) * orbitRadiusDp * 0.70f).toFloat()

    val density = LocalDensity.current
    val orbitXpx = with(density) { orbitXdp.dp.toPx() }
    val orbitYpx = with(density) { orbitYdp.dp.toPx() }

    val tiltPhase by tr.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (breath.durationMs * 8.0f).roundToInt(), easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt_phase"
    )

    val tiltDeg = lerpFloat(0.25f, 0.45f, strokeBoost) * tiltPhase

    // ✅ Halo : plus discret + vraiment externe
    val haloA = 0.03f + 0.07f * strokeBoost   // ↓ baisse d'alpha
    val haloOuter = 1.16f                      // ↑ dehors du cercle
    val haloMid = 1.08f                        // ↑ dehors aussi

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

        Canvas(
            modifier = Modifier.matchParentSize()
            // ❌ PAS de clip -> sinon le halo se replie et fait brume
        ) {

            val r = size.minDimension / 2f

            // ✅ Halo externe (dehors, pas dans le disque)
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.86f to Color.Transparent,
                        0.94f to MAUVE.copy(alpha = haloA),
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = r * haloOuter
                ),
                radius = r * haloOuter,
                center = center
            )

            // ✅ Halo secondaire (encore dehors)
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.84f to Color.Transparent,
                        0.93f to MAUVE.copy(alpha = haloA * 0.55f),
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = r * haloMid
                ),
                radius = r * haloMid,
                center = center
            )

            // ✅ Cercle principal net
            drawCircle(
                color = MAUVE,
                style = Stroke(width = strokeDp.toPx())
            )
        }

        Text(
            text = text,
            fontSize = numberSize,
            fontWeight = FontWeight.ExtraBold,
            color = TURQUOISE.copy(alpha = 1.0f),
            modifier = Modifier
                .offset(y = offsetY)
                .graphicsLayer {
                    scaleX = numberEntryScale
                    scaleY = numberEntryScale
                }
        )
    }
}
private fun lerpFloat(a: Float, b: Float, t: Float): Float =
    a + (b - a) * t.coerceIn(0f, 1f)