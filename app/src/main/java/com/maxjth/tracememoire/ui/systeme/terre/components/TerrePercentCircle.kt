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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.systeme.soleil.cycle.rememberHomeNow
import com.maxjth.tracememoire.ui.systeme.soleil.cycle.ringColorForCycle
import com.maxjth.tracememoire.ui.systeme.terre.TerreCenterText
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.utils.currentMonthlyBreath
import java.time.LocalDateTime
import kotlin.math.roundToInt

@Composable
fun TerrePercentCircle(
    scoreHier: Int?,
    deltaText: String? = null,
    modifier: Modifier = Modifier,
    sizeDp: Int = 116,
    numberScale: Float = 0.82f,
    marsBreathSlowFactor: Float = 1.25f,
    nowOverride: LocalDateTime? = null
) {
    val pct by rememberUpdatedState((scoreHier ?: 0).coerceIn(0, 100))

    val showPercentSymbol = false
    val offsetY = (-2).dp

    val now = rememberHomeNow(nowOverride)

    println("DEBUG TERRE_CIRCLE → scoreHier reçu = $scoreHier | pct actuel = $pct | deltaText = $deltaText")

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

    val innerShimmer by tr.animateFloat(
        initialValue = 0.04f,
        targetValue = 0.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (breath.durationMs * 2.2f).roundToInt(),
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "terre_inner_shimmer"
    )

    val baseScale = mainBreath * (1f + (microDrift * 0.0042f))
    val strokeBoost = ((baseScale - breath.minScale) / (breath.maxScale - breath.minScale)).coerceIn(0f, 1f)

    val strokeBase = 6.dp
    val strokeDp = strokeBase + (1.4.dp * strokeBoost)

    val baseRingColor = ringColorForCycle(now)
    val vivid = lerp(baseRingColor, WHITE_SOFT.copy(alpha = 0.96f), 0.14f)

    val ringBase = vivid.copy(alpha = 0.16f)
    val ringEdge = vivid.copy(alpha = 0.58f)
    val ringBright = vivid.copy(alpha = 0.46f)

    val deepMauve = MAUVE.copy(alpha = 0.24f)
    val softTeal = TURQUOISE.copy(alpha = 0.10f)

    val sweep = (pct / 100f) * 360f

    val numberCore = WHITE_SOFT
    val numberAccent = MAUVE.copy(alpha = 0.34f)

    Box(
        modifier = modifier
            .size(sizeDp.dp)
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

            // Fond principal
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF09090C).copy(alpha = 0.98f),
                        Color(0xFF05060A).copy(alpha = 0.96f),
                        BG_DEEP.copy(alpha = 0.98f)
                    ),
                    center = center,
                    radius = r
                ),
                radius = r,
                center = center
            )

            // Profondeur interne large
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.58f to Color.Transparent,
                        1.00f to Color.Black.copy(alpha = 0.30f)
                    ),
                    center = center,
                    radius = r * 0.96f
                ),
                radius = r * 0.96f,
                center = center
            )

            // Noyau mémoire légèrement mauve
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        deepMauve.copy(alpha = innerShimmer),
                        Color.Transparent
                    ),
                    center = center,
                    radius = r * 0.42f
                ),
                radius = r * 0.42f,
                center = center
            )

            // Halo secondaire discret turquoise
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        softTeal,
                        Color.Transparent
                    ),
                    center = center,
                    radius = r * 0.68f
                ),
                radius = r * 0.68f,
                center = center
            )

            // Reflet haut-gauche très sobre
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WHITE_SOFT.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(
                        x = center.x - r * 0.18f,
                        y = center.y - r * 0.20f
                    ),
                    radius = r * 0.26f
                ),
                radius = r * 0.26f,
                center = Offset(
                    x = center.x - r * 0.18f,
                    y = center.y - r * 0.20f
                ),
                blendMode = BlendMode.SrcOver
            )

            // Anneau base
            drawCircle(
                color = ringBase,
                style = Stroke(width = strokePx)
            )

            // Anneau edge externe
            drawCircle(
                color = ringEdge,
                style = Stroke(width = strokePx + 1.0f, cap = StrokeCap.Round)
            )

            // Arc de progression
            drawArc(
                color = ringBright,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        TerreCenterText(
            pct = pct,
            deltaText = deltaText,
            numberScale = numberScale,
            offsetY = offsetY,
            showPercentSymbol = showPercentSymbol,
            colorOverride = numberCore,
            glowColor = numberAccent
        )
    }
}