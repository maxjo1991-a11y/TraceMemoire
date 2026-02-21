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
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.utils.currentMonthlyBreath
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * YesterdayPercentCircle (Mars)
 * - Mini Jupiter : même look, mais plus doux + plus lent.
 * - Couleur: basée sur le % d'hier (stable), pas sur l'horloge.
 */
@Composable
fun YesterdayPercentCircle(
    percentYesterday: Int?,
    modifier: Modifier = Modifier,
    sizeDp: Int = 116,
    numberScale: Float = 0.78f,
    // ✅ Mars respire aussi, mais moins fort + plus lent
    marsBreathSlowFactor: Float = 1.25f
) {
    val pct = percentYesterday?.coerceIn(0, 100)
    val showPercentSymbol = true

    val offsetY = (-2).dp

    // ✅ Respiration (même rythme mensuel, ralenti)
    val breath = remember { currentMonthlyBreath() }
    val tr = rememberInfiniteTransition(label = "mars_circle_life")

    val mainBreath by tr.animateFloat(
        initialValue = breath.minScale,
        targetValue = breath.maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (breath.durationMs * marsBreathSlowFactor).roundToInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mars_main_breath"
    )

    val microDrift by tr.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (breath.durationMs * 2.6f * marsBreathSlowFactor).roundToInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mars_micro_drift"
    )

    val eyePhase by tr.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (breath.durationMs * marsBreathSlowFactor).roundToInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mars_eye_phase"
    )

    val baseScale = mainBreath * (1f + (microDrift * 0.0048f)) // ✅ moins fort que Jupiter

    val strokeBoost =
        ((baseScale - breath.minScale) / (breath.maxScale - breath.minScale)).coerceIn(0f, 1f)

    val exhale = (1f - eyePhase).coerceIn(0f, 1f)
    val eyeX = 1f + (0.014f * exhale)    // ✅ moins elliptique
    val eyeY = 1f - (0.038f * exhale)
    val finalScaleX = baseScale * eyeX
    val finalScaleY = baseScale * eyeY

    val strokeBase = 6.dp
    val strokeDp = strokeBase + (1.4.dp * strokeBoost)

    // Orbit (plus doux)
    val orbitPhase by tr.animateFloat(
        initialValue = 0f,
        targetValue = (PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (breath.durationMs * 7.5f * marsBreathSlowFactor).roundToInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "mars_orbit_phase"
    )

    val orbitRadiusDp = lerpFloat(1.6f, 2.4f, strokeBoost)
    val orbitXdp = (cos(orbitPhase.toDouble()) * orbitRadiusDp).toFloat()
    val orbitYdp = (sin(orbitPhase.toDouble()) * orbitRadiusDp * 0.70f).toFloat()

    val density = LocalDensity.current
    val orbitXpx = with(density) { orbitXdp.dp.toPx() }
    val orbitYpx = with(density) { orbitYdp.dp.toPx() }

    // Tilt (plus faible)
    val tiltPhase by tr.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (breath.durationMs * 9.0f * marsBreathSlowFactor).roundToInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mars_tilt_phase"
    )
    val tiltDeg = lerpFloat(0.16f, 0.28f, strokeBoost) * tiltPhase

    // ✅ Couleur Mars (stable) : lerp TURQUOISE -> MAUVE selon % (si null => mauve soft)
    val baseRingColor = remember(pct) {
        if (pct == null) MAUVE.copy(alpha = 0.65f)
        else androidx.compose.ui.graphics.lerp(TURQUOISE, MAUVE, (pct / 100f)).copy(alpha = 0.92f)
    }

    // ✅ Atténuation globale Mars (pour ne pas voler la vedette)
    val lightBoost = 0.82f

    val vivid = androidx.compose.ui.graphics.lerp(
        baseRingColor,
        WHITE_SOFT.copy(alpha = 0.98f),
        0.22f
    )

    val neonGlowColor = vivid.copy(alpha = (0.14f * lightBoost).coerceIn(0.06f, 0.18f))
    val neonEdgeColor = vivid.copy(alpha = (0.82f * lightBoost).coerceIn(0.62f, 0.90f))
    val ringBase = vivid.copy(alpha = (0.26f * lightBoost).coerceIn(0.14f, 0.32f))
    val ringBright = vivid.copy(alpha = (0.88f * lightBoost).coerceIn(0.55f, 0.92f))

    val haloA = (0.016f + 0.030f * strokeBoost) * lightBoost
    val haloOuter = 1.14f
    val haloMid = 1.06f
    val haloColorMain = baseRingColor.copy(alpha = haloA.coerceIn(0.03f, 0.12f))
    val haloColorSoft = baseRingColor.copy(alpha = (haloA * 0.55f).coerceIn(0.02f, 0.08f))

    // Iris / pupille (même ADN)
    val irisDeep = Color(0xFF1A0F2A)
    val irisMauve = androidx.compose.ui.graphics.lerp(MAUVE, Color(0xFF2C1455), 0.55f)
    val irisAccent = androidx.compose.ui.graphics.lerp(irisMauve, baseRingColor, 0.16f)
    val pupilDeep = Color(0xFF05060C)

    // Micro étoiles (stables)
    val starPoints = remember(pct) {
        val seed = (pct ?: 777) * 1337
        val rnd = Random(seed)
        List(8) {
            val x = rnd.nextFloat() * 2f - 1f
            val y = rnd.nextFloat() * 2f - 1f
            val a = 0.05f + rnd.nextFloat() * 0.08f
            Triple(x, y, a)
        }
    }

    val sweep = ((pct ?: 0) / 100f) * 360f

    Box(
        modifier = modifier
            .size(sizeDp.dp)
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

            // Halo mid
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

            // Iris
            val irisR = r * 0.70f
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to irisDeep.copy(alpha = 0.98f),
                        0.45f to irisMauve.copy(alpha = 0.72f),
                        0.75f to irisAccent.copy(alpha = 0.16f),
                        1.00f to irisDeep.copy(alpha = 0.52f)
                    ),
                    center = center,
                    radius = irisR
                ),
                radius = irisR,
                center = center
            )

            // Reflet lentille (plus petit)
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to WHITE_SOFT.copy(alpha = 0.14f),
                        0.35f to WHITE_SOFT.copy(alpha = 0.05f),
                        1.00f to Color.Transparent
                    ),
                    center = center + Offset(-irisR * 0.26f, -irisR * 0.28f),
                    radius = irisR * 0.40f
                ),
                radius = irisR * 0.40f,
                center = center + Offset(-irisR * 0.26f, -irisR * 0.28f)
            )

            // Micro étoiles
            val starsR = irisR * 0.78f
            starPoints.forEach { (nx, ny, a) ->
                val px = center.x + (nx * starsR)
                val py = center.y + (ny * starsR)
                val dx = px - center.x
                val dy = py - center.y
                if ((dx * dx + dy * dy) <= (starsR * starsR)) {
                    drawCircle(
                        color = WHITE_SOFT.copy(alpha = (a * 0.50f).coerceIn(0.02f, 0.06f)),
                        radius = (r * 0.010f).coerceAtLeast(1.1f),
                        center = Offset(px, py)
                    )
                }
            }

            // Pupille
            val pupilR = r * 0.42f
            drawCircle(
                color = pupilDeep.copy(alpha = 0.96f),
                radius = pupilR,
                center = center
            )

            // micro glow interne
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to baseRingColor.copy(alpha = (0.18f * lightBoost).coerceIn(0.05f, 0.10f)),
                        0.60f to baseRingColor.copy(alpha = (0.03f * lightBoost).coerceIn(0.02f, 0.06f)),
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = pupilR * 1.20f
                ),
                radius = pupilR * 1.25f,
                center = center
            )

            // Néon glow
            drawCircle(
                color = neonGlowColor,
                style = Stroke(width = strokePx + 7f, cap = StrokeCap.Round)
            )

            // Base ring
            drawCircle(
                color = ringBase,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Bord net
            drawCircle(
                color = neonEdgeColor.copy(alpha = (neonEdgeColor.alpha * 0.55f).coerceIn(0.28f, 0.58f)),
                style = Stroke(width = (strokePx + 1.6f), cap = StrokeCap.Round)
            )

            if (pct != null) {
                drawArc(
                    color = ringBright,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            } else {
                drawCircle(
                    color = ringBright,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        // ✅ Texte central séparé (MarsCenterText)
        MarsCenterText(
            pct = pct,
            numberScale = numberScale,
            offsetY = offsetY,
            showPercentSymbol = showPercentSymbol
        )
    }
}

private fun lerpFloat(a: Float, b: Float, t: Float): Float =
    a + (b - a) * t.coerceIn(0f, 1f)