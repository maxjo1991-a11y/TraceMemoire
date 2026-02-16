package com.maxjth.tracememoire.ui.tracejour.components.screen.percent

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.maxjth.tracememoire.ui.theme.MAUVE

@Composable
fun PercentBadgeDiamond(
    percent: Int,
    badgeSize: Dp = 30.dp,
    breathe: Boolean = true,
    alpha: Float = 1f
) {
    val p = percent.coerceIn(0, 100)

    // micro respiration (presque imperceptible)
    val infinite = rememberInfiniteTransition(label = "badgeBreath")
    val breatheAlpha by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.00f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "badgeBreathAlpha"
    )

    val finalAlpha = (alpha * (if (breathe) breatheAlpha else 1f)).coerceIn(0f, 1f)

    val glow = MAUVE.copy(alpha = 0.20f * finalAlpha)
    val stroke = Color.White.copy(alpha = 0.22f * finalAlpha)
    val textColor = Color.White.copy(alpha = 0.92f * finalAlpha)

    Box(
        modifier = Modifier.size(badgeSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            fun diamondPath(scale: Float): Path {
                val dx = (w * 0.48f) * scale
                val dy = (h * 0.48f) * scale
                return Path().apply {
                    moveTo(cx, cy - dy)
                    lineTo(cx + dx, cy)
                    lineTo(cx, cy + dy)
                    lineTo(cx - dx, cy)
                    close()
                }
            }

            // glow doux (derrière)
            drawPath(
                path = diamondPath(scale = 1.03f),
                brush = Brush.radialGradient(
                    colors = listOf(
                        glow,
                        Color.Transparent
                    ),
                    center = androidx.compose.ui.geometry.Offset(cx, cy),
                    radius = w * 0.90f
                )
            )

            // fill léger (optionnel mais classe)
            drawPath(
                path = diamondPath(scale = 1.00f),
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.06f * finalAlpha),
                        Color.Transparent
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(w, h)
                )
            )

            // stroke
            drawPath(
                path = diamondPath(scale = 0.98f),
                color = stroke,
                style = Stroke(width = 1.6.dp.toPx())
            )
        }

        // ✅ ICI: plus de roundToInt() (p est Int)
        Text(
            text = "$p",
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}