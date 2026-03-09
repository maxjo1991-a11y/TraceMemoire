// FILE: app/src/main/java/com/maxjth/tracememoire/ui/systeme/soleil/adn/HomeSoleilParams.kt
package com.maxjth.tracememoire.ui.systeme.soleil.adn

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.systeme.soleil.calculs.lerpFloat
import com.maxjth.tracememoire.ui.systeme.soleil.cycle.ringColorForCycle
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.time.currentMonthlyBreath
import java.time.LocalDateTime
import kotlin.math.roundToInt
import kotlin.random.Random

data class HomeSoleilParams(
    val displayPct: Int,
    val centerText: String,

    val numberSizeSp: Float,
    val opticalOffsetXdp: Float,
    val opticalOffsetYdp: Float,

    val finalScaleX: Float,
    val finalScaleY: Float,
    val tiltDeg: Float,

    val strokeDp: Dp,

    val baseRingColor: Color,
    val ringBase: Color,
    val ringBright: Color,
    val neonGlowColor: Color,
    val haloColorMain: Color,
    val haloColorSoft: Color,

    val irisDeep: Color,
    val irisMauve: Color,
    val irisAccent: Color,
    val pupilDeep: Color,

    val lightBoost: Float,
    val starPoints: List<Triple<Float, Float, Float>>
)

@Composable
fun rememberHomeSoleilParams(
    now: LocalDateTime,
    progressPercent: Int?
): HomeSoleilParams {

    val pct = progressPercent?.coerceIn(0, 100)
    val displayPct = (pct ?: 50).coerceIn(0, 100)
    val centerText = displayPct.toString()

    val numberSizeSp = when (centerText.length) {
        1 -> 108f
        2 -> 99f
        else -> 84f
    }
    val opticalOffsetXdp = when (centerText.length) {
        1 -> 0f
        2 -> 1f
        else -> 0f
    }
    val opticalOffsetYdp = when (centerText.length) {
        1 -> -2f
        2 -> -2f
        else -> -3f
    }

    // ✅ IMPORTANT : currentMonthlyBreath() vient maintenant de ui.time
    val breath = remember { currentMonthlyBreath() }
    val tr = rememberInfiniteTransition(label = "home_soleil_life")

    val mainBreath by tr.animateFloat(
        initialValue = breath.minScale,
        targetValue = breath.maxScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = breath.durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "soleil_main_breath"
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
        label = "soleil_micro_drift"
    )

    val eyePhase by tr.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = breath.durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "soleil_eye_phase"
    )

    val baseScale = mainBreath * (1f + (microDrift * 0.0060f))
    val strokeBoost =
        ((baseScale - breath.minScale) / (breath.maxScale - breath.minScale)).coerceIn(0f, 1f)

    val exhale = (1f - eyePhase).coerceIn(0f, 1f)
    val eyeUniform = 1f - (0.018f * exhale)

    val finalScaleX = baseScale * eyeUniform
    val finalScaleY = baseScale * eyeUniform

    val strokeBase = 8.dp
    // ✅ évite les ambiguïtés d’operator times(Dp, Float) selon imports/versions
    val strokeDp = strokeBase + (2f * strokeBoost).dp

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
        label = "soleil_tilt_phase"
    )
    val tiltDeg = lerpFloat(0.18f, 0.34f, strokeBoost) * tiltPhase

    // =========================
    // ✅ COULEUR = même cycle que Terre (minute-based)
    // =========================
    val baseRingColor = ringColorForCycle(now)

    // ✅ Nuit = un peu plus “vivant”
    val minutes = now.hour * 60 + now.minute
    val isNight = (minutes in 0..(4 * 60 + 59))
    val lightBoost = if (isNight) 1.10f else 1.00f

    // ✅ Vivid contrôlé
    val vivid = lerp(
        baseRingColor,
        WHITE_SOFT.copy(alpha = 0.98f),
        0.16f
    )

    val ringBase = vivid.copy(alpha = (0.22f * lightBoost).coerceIn(0.16f, 0.34f))
    val ringBright = vivid.copy(alpha = (0.90f * lightBoost).coerceIn(0.80f, 0.98f))
    val neonGlowColor = vivid.copy(alpha = (0.12f * lightBoost).coerceIn(0.06f, 0.16f))

    val haloA = (0.016f + 0.032f * strokeBoost) * lightBoost
    val haloColorMain = baseRingColor.copy(alpha = haloA.coerceIn(0.04f, 0.14f))
    val haloColorSoft = baseRingColor.copy(alpha = (haloA * 0.50f).coerceIn(0.02f, 0.08f))

    val irisDeep = Color(0xFF140B22)
    val irisMauve = lerp(MAUVE, Color(0xFF24103F), 0.55f)
    val irisAccent = lerp(irisMauve, baseRingColor, 0.18f)
    val pupilDeep = Color(0xFF05060C)

    val starPoints = remember(now.year, now.dayOfYear) {
        val rnd = Random(now.year * 10_000 + now.dayOfYear + 77)
        List(10) {
            val x = rnd.nextFloat() * 2f - 1f
            val y = rnd.nextFloat() * 2f - 1f
            val a = 0.06f + rnd.nextFloat() * 0.10f
            Triple(x, y, a)
        }
    }

    return HomeSoleilParams(
        displayPct = displayPct,
        centerText = centerText,
        numberSizeSp = numberSizeSp,
        opticalOffsetXdp = opticalOffsetXdp,
        opticalOffsetYdp = opticalOffsetYdp,
        finalScaleX = finalScaleX,
        finalScaleY = finalScaleY,
        tiltDeg = tiltDeg,
        strokeDp = strokeDp,
        baseRingColor = baseRingColor,
        ringBase = ringBase,
        ringBright = ringBright,
        neonGlowColor = neonGlowColor,
        haloColorMain = haloColorMain,
        haloColorSoft = haloColorSoft,
        irisDeep = irisDeep,
        irisMauve = irisMauve,
        irisAccent = irisAccent,
        pupilDeep = pupilDeep,
        lightBoost = lightBoost,
        starPoints = starPoints
    )
}