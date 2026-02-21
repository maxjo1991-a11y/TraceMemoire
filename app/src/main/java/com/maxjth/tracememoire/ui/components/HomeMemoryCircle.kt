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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
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
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
fun HomeMemoryCircle(
    traceCount: Int,
    progressPercent: Int? = null,          // % (0..100) si tu veux un arc
    modifier: Modifier = Modifier,
    numberEntryScale: Float = 1f,
    nowOverride: LocalDateTime? = null
) {
    // ✅ Horloge réelle (timer) : la couleur + lumière suit l'heure
    val now by produceState(initialValue = nowOverride ?: LocalDateTime.now(), key1 = nowOverride) {
        if (nowOverride != null) {
            value = nowOverride
            return@produceState
        }
        while (true) {
            value = LocalDateTime.now()
            delay(30_000L)
        }
    }

    // ✅ pct (si null => on choisit un fallback neutre)
    val pct = progressPercent?.coerceIn(0, 100)

    // ✅ IMPORTANT: sur Home, si pct est null (prefs pas chargées / 1er boot),
    // on affiche 50 (neutre) au lieu d'afficher traceCount (qui est 0 chez toi).
    val fallbackNeutral = 50
    val displayPct = (pct ?: fallbackNeutral).coerceIn(0, 100)

    // ✅ Texte centre: toujours un pourcentage "logique" (50 par défaut)
    val centerText = displayPct.toString()

    val numberSize = when (centerText.length) {
        1 -> 86.sp
        2 -> 96.sp
        else -> 100.sp
    }

    val offsetY = when (centerText.length) {
        1 -> (-8).dp
        2 -> (-2).dp
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

    val eyePhase by tr.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = breath.durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eye_phase"
    )

    val baseScale = mainBreath * (1f + (microDrift * 0.0065f))

    val strokeBoost =
        ((baseScale - breath.minScale) / (breath.maxScale - breath.minScale)).coerceIn(0f, 1f)

    val exhale = (1f - eyePhase).coerceIn(0f, 1f)
    val eyeX = 1f + (0.020f * exhale)
    val eyeY = 1f - (0.055f * exhale)

    val finalScaleX = baseScale * eyeX
    val finalScaleY = baseScale * eyeY

    val strokeBase = 8.dp
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

    // ✅ Couleur pilotée par l’horloge (NE PAS CHANGER)
    val baseRingColor = remember(now) { ringColorForCycle(now) }

    val isNight = (now.hour < 5)
    val lightBoost = if (isNight) 1.18f else 1.00f

    val vivid = androidx.compose.ui.graphics.lerp(
        baseRingColor,
        WHITE_SOFT.copy(alpha = 0.98f),
        0.28f
    )

    val neonGlowColor = vivid.copy(alpha = (0.18f * lightBoost).coerceIn(0.10f, 0.28f))
    val neonEdgeColor = vivid.copy(alpha = (0.92f * lightBoost).coerceIn(0.86f, 0.98f))
    val ringBase = vivid.copy(alpha = (0.30f * lightBoost).coerceIn(0.22f, 0.52f))
    val ringBright = vivid.copy(alpha = (0.96f * lightBoost).coerceIn(0.88f, 0.99f))

    val haloA = (0.020f + 0.050f * strokeBoost) * lightBoost
    val haloOuter = 1.16f
    val haloMid = 1.08f
    val haloColorMain = baseRingColor.copy(alpha = haloA.coerceIn(0.06f, 0.22f))
    val haloColorSoft = baseRingColor.copy(alpha = (haloA * 0.55f).coerceIn(0.03f, 0.14f))

    val irisDeep = Color(0xFF1A0F2A)
    val irisMauve = androidx.compose.ui.graphics.lerp(MAUVE, Color(0xFF2C1455), 0.55f)
    val irisAccent = androidx.compose.ui.graphics.lerp(irisMauve, baseRingColor, 0.18f)
    val pupilDeep = Color(0xFF05060C)

    val starPoints = remember(now.year, now.dayOfYear) {
        val rnd = Random(now.year * 10_000 + now.dayOfYear)
        List(10) {
            val x = rnd.nextFloat() * 2f - 1f
            val y = rnd.nextFloat() * 2f - 1f
            val a = 0.06f + rnd.nextFloat() * 0.10f
            Triple(x, y, a)
        }
    }

    // ✅ Arc: basé sur displayPct (donc 50 par défaut)
    val sweep = (displayPct / 100f) * 360f

    Box(
        modifier = modifier
            .size(240.dp)
            .graphicsLayer(
                scaleX = finalScaleX,
                scaleY = finalScaleY,
                translationX = orbitXpx,
                translationY = orbitYpx,
                rotationZ = tiltDeg
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val r = size.minDimension / 2f
            val strokePx = strokeDp.toPx()

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

            val irisR = r * 0.70f
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to irisDeep.copy(alpha = 0.98f),
                        0.45f to irisMauve.copy(alpha = 0.78f),
                        0.75f to irisAccent.copy(alpha = 0.18f),
                        1.00f to irisDeep.copy(alpha = 0.55f)
                    ),
                    center = center,
                    radius = irisR
                ),
                radius = irisR,
                center = center
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.70f to Color.Transparent,
                        1.00f to irisDeep.copy(alpha = 0.35f)
                    ),
                    center = center,
                    radius = irisR
                ),
                radius = irisR,
                center = center
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to WHITE_SOFT.copy(alpha = 0.18f),
                        0.35f to WHITE_SOFT.copy(alpha = 0.06f),
                        1.00f to Color.Transparent
                    ),
                    center = center + Offset(-irisR * 0.28f, -irisR * 0.30f),
                    radius = irisR * 0.45f
                ),
                radius = irisR * 0.45f,
                center = center + Offset(-irisR * 0.28f, -irisR * 0.30f)
            )

            val starsR = irisR * 0.78f
            starPoints.forEach { (nx, ny, a) ->
                val px = center.x + (nx * starsR)
                val py = center.y + (ny * starsR)
                val dx = px - center.x
                val dy = py - center.y
                if ((dx * dx + dy * dy) <= (starsR * starsR)) {
                    drawCircle(
                        color = WHITE_SOFT.copy(alpha = (a * 0.55f).coerceIn(0.02f, 0.08f)),
                        radius = (r * 0.010f).coerceAtLeast(1.2f),
                        center = Offset(px, py)
                    )
                }
            }

            val pupilR = r * 0.41f
            drawCircle(
                color = pupilDeep.copy(alpha = 0.96f),
                radius = pupilR,
                center = center
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to baseRingColor.copy(alpha = (0.27f * lightBoost).coerceIn(0.08f, 0.14f)),
                        0.55f to baseRingColor.copy(alpha = (0.03f * lightBoost).coerceIn(0.03f, 0.08f)),
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = pupilR * 1.20f
                ),
                radius = pupilR * 1.25f,
                center = center
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to baseRingColor.copy(alpha = (0.04f * lightBoost).coerceIn(0.02f, 0.07f)),
                        0.55f to baseRingColor.copy(alpha = (0.02f * lightBoost).coerceIn(0.01f, 0.05f)),
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = r * 0.98f
                ),
                radius = r * 0.98f,
                center = center
            )

            drawCircle(
                color = neonGlowColor,
                style = Stroke(width = strokePx + 10f, cap = StrokeCap.Round)
            )

            drawCircle(
                color = ringBase,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            drawCircle(
                color = neonEdgeColor.copy(alpha = (neonEdgeColor.alpha * 0.55f).coerceIn(0.35f, 0.70f)),
                style = Stroke(width = (strokePx + 2.2f), cap = StrokeCap.Round)
            )

            // ✅ Toujours un arc (displayPct). Ça “verrouille” la lecture visuelle.
            drawArc(
                color = ringBright,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        val numberBrush = Brush.linearGradient(
            colors = listOf(
                TURQUOISE.copy(alpha = 0.98f),
                WHITE_SOFT.copy(alpha = 0.92f)
            )
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .offset(x = 10.dp, y = offsetY)
                .graphicsLayer {
                    scaleX = numberEntryScale
                    scaleY = numberEntryScale
                }
        ) {
            Text(
                text = centerText,
                fontSize = numberSize,
                fontWeight = FontWeight.ExtraBold,
                style = TextStyle(brush = numberBrush)
            )

            // ✅ % toujours affiché (parce que Home c'est un %)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "%",
                fontSize = (numberSize.value * 0.34f).sp,
                fontWeight = FontWeight.SemiBold,
                color = TURQUOISE.copy(alpha = 0.60f)
            )
        }
    }
}

/**
 * ✅ Palette cycle (pilotée par l’horloge) — INTACTE
 */
private fun ringColorForCycle(now: LocalDateTime): Color {
    val minutes = now.hour * 60 + now.minute
    val milk = WHITE_SOFT.copy(alpha = 0.90f)
    val mauveNight = androidx.compose.ui.graphics.lerp(MAUVE, Color(0xFF2A1840), 0.55f)
    val mauveEvening = androidx.compose.ui.graphics.lerp(MAUVE, Color(0xFF3A2460), 0.35f)

    return when {
        minutes in 0..(4 * 60 + 59) ->
            mauveNight.copy(alpha = 0.95f)

        minutes in (5 * 60)..(11 * 60 + 59) ->
            androidx.compose.ui.graphics.lerp(TURQUOISE, milk, 0.10f).copy(alpha = 0.95f)

        minutes in (12 * 60)..(17 * 60 + 59) ->
            androidx.compose.ui.graphics.lerp(TURQUOISE, milk, 0.20f).copy(alpha = 0.95f)

        else ->
            androidx.compose.ui.graphics.lerp(TURQUOISE, mauveEvening, 0.72f).copy(alpha = 0.95f)
    }
}

private fun lerpFloat(a: Float, b: Float, t: Float): Float =
    a + (b - a) * t.coerceIn(0f, 1f)