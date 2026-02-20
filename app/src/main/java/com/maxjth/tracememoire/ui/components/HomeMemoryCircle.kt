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
import androidx.compose.ui.geometry.Offset
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
import kotlin.random.Random
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
    // ✅ Horloge réelle (timer) : la couleur + lumière suit l'heure
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

    // Respiration principale (ton rythme mensuel)
    val mainBreath by tr.animateFloat(
        initialValue = breath.minScale,
        targetValue = breath.maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = breath.durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "main_breath"
    )

    // Micro dérive subtile (déjà chez toi)
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

    // ✅ Phase "œil" : écrase légèrement à l’expiration
    val eyePhase by tr.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = breath.durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "eye_phase"
    )

    // Scale finale (souffle + micro drift)
    val baseScale = mainBreath * (1f + (microDrift * 0.0065f))

    // Normalise 0..1 pour piloter les épaisseurs / effets
    val strokeBoost =
        ((baseScale - breath.minScale) / (breath.maxScale - breath.minScale)).coerceIn(0f, 1f)

    // ✅ Effet "œil" (ellipse douce) à l’expiration (pas trop, pour ne pas écraser le texte)
    val exhale = (1f - eyePhase).coerceIn(0f, 1f)
    val eyeX = 1f + (0.020f * exhale)     // un peu plus large
    val eyeY = 1f - (0.055f * exhale)     // un peu plus plat
    val finalScaleX = baseScale * eyeX
    val finalScaleY = baseScale * eyeY

    val strokeBase = 8.dp
    val strokeDp = strokeBase + (2.dp * strokeBoost)

    // Orbite (déplacement vivant)
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

    // Tilt
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

    // ✅ Boost de lumière la nuit (sans changer la teinte)
    val isNight = (now.hour < 5)
    val lightBoost = if (isNight) 1.18f else 1.00f

    // ✅ “Plus vivant” = même teinte, mais un peu plus lumineuse (sans changer la logique horaire)
    val vivid = androidx.compose.ui.graphics.lerp(
        baseRingColor,
        WHITE_SOFT.copy(alpha = 0.98f),
        0.28f
    )

    // ✅ Néon propre (pas agressif) : glow large + bord net
    val neonGlowColor = vivid.copy(alpha = (0.18f * lightBoost).coerceIn(0.10f, 0.28f))
    val neonEdgeColor = vivid.copy(alpha = (0.92f * lightBoost).coerceIn(0.86f, 0.98f))

    // ✅ Ring base (présence douce)
    val ringBase = vivid.copy(alpha = (0.30f * lightBoost).coerceIn(0.22f, 0.52f))

    // ✅ Arc / ring principal (net, vivant)
    val ringBright = vivid.copy(alpha = (0.96f * lightBoost).coerceIn(0.88f, 0.99f))

    // Halos (soft)
    val haloA = (0.020f + 0.050f * strokeBoost) * lightBoost
    val haloOuter = 1.16f
    val haloMid = 1.08f
    val haloColorMain = baseRingColor.copy(alpha = haloA.coerceIn(0.06f, 0.22f))
    val haloColorSoft = baseRingColor.copy(alpha = (haloA * 0.55f).coerceIn(0.03f, 0.14f))

    // ✅ “Œil cosmique” (iris + pupille) : MAUVE profond + reflets, SANS toucher au cycle
    val irisDeep = Color(0xFF1A0F2A) // mauve/noir cosmique
    val irisMauve = androidx.compose.ui.graphics.lerp(MAUVE, Color(0xFF2C1455), 0.55f)
    val irisAccent = androidx.compose.ui.graphics.lerp(irisMauve, baseRingColor, 0.18f) // petit lien avec la teinte du cycle (subtil)
    val pupilDeep = Color(0xFF05060C)

    // ✅ Micro étoiles (stables, pas “random” à chaque recomposition)
    val starPoints = remember(now.year, now.dayOfYear) {
        val rnd = Random(now.year * 10_000 + now.dayOfYear)
        List(10) {
            // positions normalisées dans un disque interne (on recentre ensuite)
            val x = rnd.nextFloat() * 2f - 1f
            val y = rnd.nextFloat() * 2f - 1f
            val a = 0.06f + rnd.nextFloat() * 0.10f
            Triple(x, y, a)
        }
    }

    val sweep = ((pct ?: 0) / 100f) * 360f

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

            // Halo externe (premium, discret)
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

            // ✅ IRIS / LENTILLE (cosmique, verre)
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

            // ✅ Vignettage doux (bords plus sombres => effet verre)
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

            // ✅ Reflet “lentille” (highlight haut-gauche)
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

            // ✅ Micro-étoiles (très subtil)
            val starsR = irisR * 0.78f
            starPoints.forEach { (nx, ny, a) ->
                val px = center.x + (nx * starsR)
                val py = center.y + (ny * starsR)
                // garde seulement les points dans le disque interne
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

            // ✅ Pupille (trou) + micro glow interne (pas creepy)
            val pupilR = r * 0.26f
            drawCircle(
                color = pupilDeep.copy(alpha = 0.96f),
                radius = pupilR,
                center = center
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to baseRingColor.copy(alpha = (0.10f * lightBoost).coerceIn(0.06f, 0.14f)),
                        0.55f to baseRingColor.copy(alpha = (0.04f * lightBoost).coerceIn(0.02f, 0.08f)),
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = pupilR * 1.20f
                ),
                radius = pupilR * 1.20f,
                center = center
            )

            // Glow interne général (vie)
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

            // ✅ Néon glow (large, doux)
            drawCircle(
                color = neonGlowColor,
                style = Stroke(width = strokePx + 10f, cap = StrokeCap.Round)
            )

            // ✅ Base ring (présence)
            drawCircle(
                color = ringBase,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // ✅ Bord net (le “contour néon” propre)
            drawCircle(
                color = neonEdgeColor.copy(alpha = (neonEdgeColor.alpha * 0.55f).coerceIn(0.35f, 0.70f)),
                style = Stroke(width = (strokePx + 2.2f), cap = StrokeCap.Round)
            )

            // Arc ou ring principal
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

        // ✅ Le chiffre reste EXACTEMENT au centre (dans la pupille)
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
 * ✅ Palette cycle (pilotée par l’horloge) — INTACTE
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