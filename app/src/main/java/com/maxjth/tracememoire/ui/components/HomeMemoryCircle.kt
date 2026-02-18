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

@Composable
fun HomeMemoryCircle(
    traceCount: Int,
    progressPercent: Int? = null,         // ✅ NOUVEAU : % de progression (0..100)
    modifier: Modifier = Modifier,
    numberEntryScale: Float = 1f,
    now: LocalDateTime = LocalDateTime.now() // ✅ NOUVEAU : pour la couleur par cycle
) {
    val pct = progressPercent?.coerceIn(0, 100)
    val centerText = (pct?.toString() ?: traceCount.toString())

    // Taille du chiffre central (selon longueur)
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

    // ✅ Respiration mensuelle + micro-drift
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

    // Stroke qui vit un peu avec la respiration
    val strokeBase = 8.dp
    val strokeBoost =
        ((finalScale - breath.minScale) / (breath.maxScale - breath.minScale)).coerceIn(0f, 1f)
    val strokeDp = strokeBase + (2.dp * strokeBoost)

    // Orbite (ultra légère)
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

    // Tilt (très faible)
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

    // ✅ Couleur par cycle (matin/jour/soir/nuit) — jamais agressif
    val baseRingColor = remember(now) { ringColorForCycle(now) }

    // Halo (calme)
    val haloA = 0.028f + 0.055f * strokeBoost
    val haloOuter = 1.16f
    val haloMid = 1.08f

    val haloColorMain = baseRingColor.copy(alpha = haloA)
    val haloColorSoft = baseRingColor.copy(alpha = haloA * 0.55f)

    // ✅ Progression (arc)
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

            // Halo externe
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.86f to Color.Transparent,
                        0.94f to haloColorMain,
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = r * haloOuter
                ),
                radius = r * haloOuter,
                center = center
            )

            // Halo secondaire
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.84f to Color.Transparent,
                        0.93f to haloColorSoft,
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = r * haloMid
                ),
                radius = r * haloMid,
                center = center
            )

            // Anneau "base" discret (arrière)
            drawCircle(
                color = baseRingColor.copy(alpha = 0.22f),
                style = Stroke(width = strokeDp.toPx(), cap = StrokeCap.Round)
            )

            // Anneau progression (arc) si on a un % (sinon on laisse juste le cercle)
            if (pct != null) {
                drawArc(
                    color = baseRingColor.copy(alpha = 0.92f),
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeDp.toPx(), cap = StrokeCap.Round)
                )
            } else {
                // Mode "simple" (full ring)
                drawCircle(
                    color = baseRingColor.copy(alpha = 0.92f),
                    style = Stroke(width = strokeDp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // ✅ Texte central (nombre)
        Text(
            text = centerText,
            fontSize = numberSize,
            fontWeight = FontWeight.ExtraBold,
            // texte toujours lisible (turquoise calme)
            color = TURQUOISE.copy(alpha = 0.98f),
            modifier = Modifier
                .offset(y = offsetY)
                .graphicsLayer {
                    scaleX = numberEntryScale
                    scaleY = numberEntryScale
                }
        )

        // ✅ Petit symbole % à droite (doux)
        if (pct != null) {
            Text(
                text = "%",
                fontSize = (numberSize.value * 0.34f).sp,
                fontWeight = FontWeight.SemiBold,
                color = WHITE_SOFT.copy(alpha = 0.78f),
                modifier = Modifier
                    // positionnée à droite du chiffre, sans agresser
                    .offset(
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
 * ✅ Palette cycle (calme / galaxie / jamais aveuglante)
 * - Matin : turquoise
 * - Jour  : turquoise + lait blanc
 * - Soir  : transition vers mauve doux
 * - Nuit  : mauve nuit "espace"
 */
private fun ringColorForCycle(now: LocalDateTime): Color {
    val minutes = now.hour * 60 + now.minute

    // petit blanc "lait"
    val milk = WHITE_SOFT.copy(alpha = 0.90f)

    // mauve nuit profond (sans être neon)
    val mauveNight = androidx.compose.ui.graphics.lerp(MAUVE, Color(0xFF2A1840), 0.55f)

    // mauve soir : plus “velours”, moins sombre que la nuit
    val mauveEvening = androidx.compose.ui.graphics.lerp(MAUVE, Color(0xFF3A2460), 0.35f)

    return when {
        // NUIT 00:00 -> 04:59
        minutes in 0..(4 * 60 + 59) -> mauveNight.copy(alpha = 0.95f)

        // MATIN 05:00 -> 11:59
        minutes in (5 * 60)..(11 * 60 + 59) ->
            androidx.compose.ui.graphics.lerp(TURQUOISE, milk, 0.10f).copy(alpha = 0.95f)

        // JOUR 12:00 -> 17:59 (un peu plus clair)
        minutes in (12 * 60)..(17 * 60 + 59) ->
            androidx.compose.ui.graphics.lerp(TURQUOISE, milk, 0.20f).copy(alpha = 0.95f)

        // SOIR 18:00 -> 23:59
        else ->
            androidx.compose.ui.graphics.lerp(TURQUOISE, mauveEvening, 0.72f).copy(alpha = 0.95f)
    }
}

private fun lerpFloat(a: Float, b: Float, t: Float): Float =
    a + (b - a) * t.coerceIn(0f, 1f)