// FILE: app/src/main/java/com/maxjth/tracememoire/ui/terre/components/TerrePercentCircle.kt
package com.maxjth.tracememoire.ui.systeme.terre.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.systeme.soleil.cycle.rememberHomeNow
import com.maxjth.tracememoire.ui.systeme.soleil.cycle.ringColorForCycle
import com.maxjth.tracememoire.ui.systeme.terre.TerreCenterText
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.utils.currentMonthlyBreath
import java.time.LocalDateTime
import kotlin.math.roundToInt

@Composable
fun TerrePercentCircle(
    // ✅ Terre = % annuel global
    percentYearly: Int?,
    modifier: Modifier = Modifier,
    sizeDp: Int = 116,
    numberScale: Float = 0.82f,
    marsBreathSlowFactor: Float = 1.25f,

    // ✅ IMPORTANT: HomeScreen envoie le même "now" à Terre + Soleil
    nowOverride: LocalDateTime? = null
) {
    // ✅ FORCER 0 AU LIEU DE null => plus jamais de point / état vide
    val pct: Int = (percentYearly ?: 0).coerceIn(0, 100)

    // 🔒 chez toi : pas de symbole %
    val showPercentSymbol = false
    val offsetY = (-2).dp

    // ✅ Tick temps partagé (Terre + Soleil)
    val now = rememberHomeNow(nowOverride)

    // ✅ Respiration
    val breath = remember { currentMonthlyBreath() }
    val tr = rememberInfiniteTransition(label = "terre_circle_life")

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
        label = "terre_main_breath"
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
        label = "terre_micro_drift"
    )

    val baseScale = mainBreath * (1f + (microDrift * 0.0048f))
    val strokeBoost =
        ((baseScale - breath.minScale) / (breath.maxScale - breath.minScale)).coerceIn(0f, 1f)

    val strokeBase = 6.dp
    val strokeDp = strokeBase + (1.4.dp * strokeBoost)

    // =========================================================
    // ✅ Cycle couleur = EXACTEMENT la même fonction que Soleil
    // =========================================================
    val baseRingColor = ringColorForCycle(now)

    // “verre” clean
    val vivid = lerp(baseRingColor, WHITE_SOFT.copy(alpha = 0.98f), 0.18f)

    // Intensités “Terre” (calme mais lisible)
    val ringBase = vivid.copy(alpha = 0.18f)
    val ringEdge = vivid.copy(alpha = 0.72f)
    val ringBright = vivid.copy(alpha = 0.55f)

    val sweep = (pct / 100f) * 360f

    // ✅ CHIFFRE: blanc net comme le titre + glow mauve doux
    val numberCore = Color.White
    val numberAccent = MAUVE.copy(alpha = 0.55f)

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            // ✅ FIX TRANSPARENCE : on force un vrai fond circulaire (comme la Lune)
            .background(BG_DEEP, CircleShape)
            .graphicsLayer(
                scaleX = baseScale,
                scaleY = baseScale
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val r = size.minDimension / 2f
            val strokePx = strokeDp.toPx()

            // ✅ Fond noir SOLIDE jusqu'au bord => l’anneau ne “mange” plus du transparent
            drawCircle(
                color = BG_DEEP.copy(alpha = 0.98f),
                radius = r
            )

            // micro vignette interne
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.72f to Color.Transparent,
                        1.00f to Color.Black.copy(alpha = 0.22f)
                    ),
                    center = center,
                    radius = r * 0.92f
                ),
                radius = r * 0.92f,
                center = center
            )

            // base ring
            drawCircle(
                color = ringBase,
                style = Stroke(width = strokePx)
            )

            // edge ring
            drawCircle(
                color = ringEdge,
                style = Stroke(width = strokePx + 1.2f, cap = StrokeCap.Round)
            )

            // ✅ arc (progress)
            drawArc(
                color = ringBright,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        TerreCenterText(
            pct = pct, // ✅ plus jamais null
            numberScale = numberScale,
            offsetY = offsetY,
            showPercentSymbol = showPercentSymbol,
            colorOverride = numberCore,
            glowColor = numberAccent
        )
    }
}