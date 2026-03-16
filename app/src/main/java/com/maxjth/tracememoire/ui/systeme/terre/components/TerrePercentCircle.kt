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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.systeme.soleil.cycle.rememberHomeNow
import com.maxjth.tracememoire.ui.systeme.soleil.cycle.ringColorForCycle
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
    numberScale: Float = 0.76f,
    marsBreathSlowFactor: Float = 1.25f,
    nowOverride: LocalDateTime? = null
) {
    val displayValue by rememberUpdatedState((scoreHier ?: 0).coerceIn(0, 400))
    val visualPct by rememberUpdatedState(((displayValue / 400f) * 100f).coerceIn(0f, 100f))

    val now = rememberHomeNow(nowOverride)
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
        initialValue = 0.025f,
        targetValue = 0.060f,
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
    val strokeBoost =
        ((baseScale - breath.minScale) / (breath.maxScale - breath.minScale)).coerceIn(0f, 1f)

    // anneau un peu plus premium
    val strokeBase = 5.0.dp
    val strokeDp = strokeBase + (1.0.dp * strokeBoost)

    val baseRingColor = ringColorForCycle(now)
    val vivid = lerp(baseRingColor, WHITE_SOFT.copy(alpha = 0.96f), 0.14f)

    val ringBase = vivid.copy(alpha = 0.12f)
    val ringEdge = vivid.copy(alpha = 0.44f)
    val ringBright = vivid.copy(alpha = 0.36f)

    val deepMauve = MAUVE.copy(alpha = 0.12f)
    val softTeal = TURQUOISE.copy(alpha = 0.05f)

    val sweep = (visualPct / 100f) * 360f

    val centerText = displayValue.toString()
    val digitCount = centerText.length
    val showDelta = !deltaText.isNullOrBlank()

    // chiffre un peu plus héroïque
    val numberFontSize = when {
        digitCount <= 1 -> (34f * numberScale).sp
        digitCount == 2 -> (29f * numberScale).sp
        digitCount == 3 -> (25f * numberScale).sp
        else -> (18f * numberScale).sp
    }

    val deltaFontSize = (11f * numberScale).sp
    val numberToLineGap = 3.dp
    val lineToDeltaGap = 3.dp
    val lineWidth = 34.dp
    val lineHeight = 1.4.dp
    val contentOffsetY = if (showDelta) (-6).dp else (-1).dp

    val numberCore = WHITE_SOFT.copy(alpha = 0.985f)
    val numberAccent = Color.Black.copy(alpha = 0.14f)

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

            // fond profond principal
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0A0C14).copy(alpha = 0.98f),
                        Color(0xFF06070D).copy(alpha = 0.96f),
                        BG_DEEP.copy(alpha = 0.98f)
                    ),
                    center = center,
                    radius = r
                ),
                radius = r,
                center = center
            )

            // ombre intérieure douce
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.58f to Color.Transparent,
                        1.00f to Color.Black.copy(alpha = 0.18f)
                    ),
                    center = center,
                    radius = r * 0.96f
                ),
                radius = r * 0.96f,
                center = center
            )

            // coeur mauve discret
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        deepMauve.copy(alpha = innerShimmer),
                        Color.Transparent
                    ),
                    center = center,
                    radius = r * 0.32f
                ),
                radius = r * 0.32f,
                center = center
            )

            // voile turquoise discret
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        softTeal,
                        Color.Transparent
                    ),
                    center = center,
                    radius = r * 0.52f
                ),
                radius = r * 0.52f,
                center = center
            )

            // reflet haut-gauche plus fin
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WHITE_SOFT.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(
                        x = center.x - r * 0.16f,
                        y = center.y - r * 0.18f
                    ),
                    radius = r * 0.18f
                ),
                radius = r * 0.18f,
                center = Offset(
                    x = center.x - r * 0.16f,
                    y = center.y - r * 0.18f
                ),
                blendMode = BlendMode.SrcOver
            )

            // coeur lumineux très discret
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.024f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = r * 0.09f
                ),
                radius = r * 0.09f,
                center = center
            )

            // anneau base
            drawCircle(
                color = ringBase,
                style = Stroke(width = strokePx)
            )

            // anneau contour
            drawCircle(
                color = ringEdge,
                style = Stroke(width = strokePx + 0.6f, cap = StrokeCap.Round)
            )

            // portion active
            drawArc(
                color = ringBright,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        Box(
            modifier = Modifier.offset(y = contentOffsetY),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = centerText,
                    style = TextStyle(
                        color = numberCore,
                        fontSize = numberFontSize,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = when {
                            digitCount >= 4 -> (-0.10).sp
                            digitCount == 3 -> (-0.20).sp
                            else -> (-0.34).sp
                        },
                        shadow = Shadow(
                            color = numberAccent,
                            offset = Offset(0f, 0.8f),
                            blurRadius = 4.5f
                        )
                    )
                )

                if (showDelta) {
                    Spacer(modifier = Modifier.height(numberToLineGap))

                    Box(
                        modifier = Modifier
                            .width(lineWidth)
                            .height(lineHeight)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        MAUVE.copy(alpha = 0.10f),
                                        MAUVE.copy(alpha = 0.50f),
                                        TURQUOISE.copy(alpha = 0.50f),
                                        TURQUOISE.copy(alpha = 0.10f)
                                    )
                                ),
                                CircleShape
                            )
                    )

                    Spacer(modifier = Modifier.height(lineToDeltaGap))

                    Text(
                        text = deltaText!!.trim(),
                        fontSize = deltaFontSize,
                        fontWeight = FontWeight.Bold,
                        color = if (deltaText.trim().startsWith("-")) {
                            MAUVE.copy(alpha = 0.94f)
                        } else {
                            TURQUOISE.copy(alpha = 0.94f)
                        },
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.10f),
                                offset = Offset.Zero,
                                blurRadius = 4f
                            )
                        )
                    )
                }
            }
        }
    }
}