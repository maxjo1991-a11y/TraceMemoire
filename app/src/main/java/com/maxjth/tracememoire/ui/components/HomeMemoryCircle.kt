// FILE: app/src/main/java/com/maxjth/tracememoire/ui/components/HomeMemoryCircle.kt
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.utils.currentMonthlyBreath
import java.time.LocalDateTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.delay

@Composable
fun HomeMemoryCircle(
    traceCount: Int,
    progressPercent: Int? = null,          // % (0..100) si tu veux un arc
    modifier: Modifier = Modifier,
    numberEntryScale: Float = 1f,
    // ✅ optionnel : pour tests/preview. Si null => horloge réelle.
    nowOverride: LocalDateTime? = null
) {
    // ✅ Horloge réelle qui tick -> la couleur + lumière se met à jour toute seule
    val now by produceState(initialValue = nowOverride ?: LocalDateTime.now(), key1 = nowOverride) {
        if (nowOverride != null) {
            value = nowOverride
            return@produceState
        }
        while (true) {
            value = LocalDateTime.now()
            delay(30_000L) // tick 30s (léger + fiable)
        }
    }

    val pct = progressPercent?.coerceIn(0, 100)
    val centerText = (pct?.toString() ?: traceCount.toString())

    val numberSize = when (centerText.length) {
        1 -> 108.sp
        2 -> 96.sp
        else -> 84.sp
    }

    val offsetY = when (centerText.length) {
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
            animation = tween(
                durationMillis = (breath.durationMs * 2.2f).roundToInt(),
                easing = LinearEasing
            ),
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
            animation = tween(
                durationMillis = (breath.durationMs * 6.5f).roundToInt(),
                easing = LinearEasing
            ),
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
            animation = tween(
                durationMillis = (breath.durationMs * 8.0f).roundToInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt_phase"
    )
    val tiltDeg = lerpFloat(0.25f, 0.45f, strokeBoost) * tiltPhase

    // ✅ Couleur pilotée par l’horloge
    val baseRingColor = remember(now) { ringColorForCycle(now) }

    // ✅ Boost lumière la nuit (ton screenshot "4h49" était trop éteint)
    val isNight = (now.hour < 5)
    val lightBoost = if (isNight) 1.18f else 1.00f

    // ✅ Halo plus présent + ring plus clair (sans "néon cheap")
    val haloA = 0.0f + 0.0f * strokeBoost
    val haloOuter = 1.00f
    val haloMid = 1.0f
    val baseRingAlpha = 1.80f

    val ringBright = androidx.compose.ui.graphics.lerp(
        baseRingColor,
        WHITE_SOFT.copy(alpha = 0.95f),
        0.22f
    ).copy(alpha = (0.95f * lightBoost).coerceIn(0.86f, 0.98f))

    val ringBase = androidx.compose.ui.graphics.lerp(
        baseRingColor,
        WHITE_SOFT.copy(alpha = 0.85f),
        0.10f
    ).copy(alpha = (baseRingAlpha * lightBoost).coerceIn(0.28f, 0.60f))

    val haloColorMain = baseRingColor.copy(alpha = (haloA * lightBoost).coerceIn(0.10f, 0.55f))
    val haloColorSoft = baseRingColor.copy(alpha = (haloA * 0.58f * lightBoost).coerceIn(0.06f, 0.38f))

    val sweep = ((pct ?: 0) / 100f) * 360f

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
            val r = size.minDimension / 2f

            // Halo externe (plus visible)
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.84f to Color.Transparent,
                        0.93f to haloColorMain,
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = r * haloOuter
                ),
                radius = r * haloOuter,
                center = center
            )

            // Halo mid (plus visible)
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.82f to Color.Transparent,
                        0.92f to haloColorSoft,
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = r * haloMid
                ),
                radius = r * haloMid,
                center = center
            )

            // ✅ Glow interne (donne le côté "vivant" même quand le ring est sombre)
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to baseRingColor.copy(alpha = (0.05f * lightBoost).coerceIn(0.03f, 0.09f)),
                        0.55f to baseRingColor.copy(alpha = (0.02f * lightBoost).coerceIn(0.01f, 0.05f)),
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = r * 0.98f
                ),
                radius = r * 0.98f,
                center = center
            )

            // Base ring (plus visible)
            drawCircle(
                color = ringBase,
                style = Stroke(width = strokeDp.toPx(), cap = StrokeCap.Round)
            )

            // Progress arc OR full ring (plus lumineux)
            if (pct != null) {
                drawArc(
                    color = ringBright,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeDp.toPx(), cap = StrokeCap.Round)
                )
            } else {
                drawCircle(
                    color = ringBright,
                    style = Stroke(width = strokeDp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        Text(
            text = centerText,
            fontSize = numberSize,
            fontWeight = FontWeight.ExtraBold,
            color = TURQUOISE.copy(alpha = 0.98f),
            modifier = Modifier
                .offset(y = offsetY)
                .graphicsLayer {
                    scaleX = numberEntryScale
                    scaleY = numberEntryScale
                }
        )

        if (pct != null) {
            Text(
                text = "%",
                fontSize = (numberSize.value * 0.34f).sp,
                fontWeight = FontWeight.SemiBold,
                color = WHITE_SOFT.copy(alpha = 0.78f),
                modifier = Modifier.offset(
                    x = when (centerText.length) {
                        1 -> 44.dp
                        2 -> 52.dp
                        else -> 58.dp
                    },
                    y = (-10).dp
                )
            )
        }
    }
}

/**
 * ✅ Palette cycle (pilotée par l’horloge)
 * - Nuit : mauve nuit
 * - Matin : turquoise doux
 * - Jour : turquoise + “lait”
 * - Soir : transition vers mauve velours
 */
private fun ringColorForCycle(now: LocalDateTime): Color {
    val minutes = now.hour * 60 + now.minute

    val milk = WHITE_SOFT.copy(alpha = 0.90f)
    val mauveNight = androidx.compose.ui.graphics.lerp(MAUVE, Color(0xFF2A1840), 0.55f)
    val mauveEvening = androidx.compose.ui.graphics.lerp(MAUVE, Color(0xFF3A2460), 0.35f)

    return when {
        // NUIT 00:00 -> 04:59
        minutes in 0..(4 * 60 + 59) ->
            mauveNight.copy(alpha = 0.95f)

        // MATIN 05:00 -> 11:59
        minutes in (5 * 60)..(11 * 60 + 59) ->
            androidx.compose.ui.graphics.lerp(TURQUOISE, milk, 0.10f).copy(alpha = 0.95f)

        // JOUR 12:00 -> 17:59
        minutes in (12 * 60)..(17 * 60 + 59) ->
            androidx.compose.ui.graphics.lerp(TURQUOISE, milk, 0.20f).copy(alpha = 0.95f)

        // SOIR 18:00 -> 23:59
        else ->
            androidx.compose.ui.graphics.lerp(TURQUOISE, mauveEvening, 0.72f).copy(alpha = 0.95f)
    }
}

private fun lerpFloat(a: Float, b: Float, t: Float): Float =
    a + (b - a) * t.coerceIn(0f, 1f)