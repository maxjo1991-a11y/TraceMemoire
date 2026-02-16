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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.Text
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun PercentBadgeDiamond(
    percent: Int,
    badgeSize: Dp = 30.dp,
    breathe: Boolean = false,
    modifier: Modifier = Modifier
) {
    val pct = percent.coerceIn(0, 100)

    val breatheAlpha: Float = if (breathe) {
        val t = rememberInfiniteTransition(label = "diamond_breathe")
        val a by t.animateFloat(
            initialValue = 0.80f,
            targetValue = 1.00f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400),
                repeatMode = RepeatMode.Reverse
            ),
            label = "diamond_breathe_alpha"
        )
        a
    } else 1f

    Box(
        modifier = modifier.size(badgeSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(badgeSize)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f
            val dx = w * 0.40f
            val dy = h * 0.40f

            fun diamondPath(scale: Float): Path {
                val sdx = dx * scale
                val sdy = dy * scale
                return Path().apply {
                    moveTo(cx, cy - sdy)
                    lineTo(cx + sdx, cy)
                    lineTo(cx, cy + sdy)
                    lineTo(cx - sdx, cy)
                    close()
                }
            }

            // Glow (simple, visible)
            drawPath(
                path = diamondPath(1.02f),
                color = MAUVE.copy(alpha = 0.22f * breatheAlpha),
                style = Stroke(width = 3.dp.toPx())
            )

            // Stroke principal
            drawPath(
                path = diamondPath(0.98f),
                color = Color.White.copy(alpha = 0.70f * breatheAlpha),
                style = Stroke(width = 1.6.dp.toPx())
            )

            // Petit éclat (diagonal)
            drawLine(
                color = MAUVE.copy(alpha = 0.18f * breatheAlpha),
                start = Offset(0f, 0f),
                end = Offset(w, h),
                strokeWidth = 1.2.dp.toPx()
            )
        }

        Text(
            text = "$pct%",
            color = WHITE_SOFT.copy(alpha = 0.95f * breatheAlpha),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}