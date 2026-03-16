// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/percent/PercentBadge.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.percent

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.TURQUOISE

@Composable
fun PercentBadge(
    percent: Int,
    modifier: Modifier = Modifier,
    baseAlpha: Float = 0.92f,
    fontSizeSp: Int = 15,
    accentColor: Color = TURQUOISE
) {
    val pct = percent.coerceIn(0, 100)

    val transition = rememberInfiniteTransition(label = "percent_breath")

    val alpha by transition.animateFloat(
        initialValue = (baseAlpha - 0.08f).coerceIn(0f, 1f),
        targetValue = (baseAlpha + 0.06f).coerceIn(0f, 1f),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2200,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "percent_alpha"
    )

    val glowAlpha by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.34f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2400,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "percent_glow_alpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(50.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = glowAlpha),
                            accentColor.copy(alpha = glowAlpha * 0.38f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            accentColor.copy(alpha = 0.06f * alpha),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.03f),
                            Color.Black.copy(alpha = 0.10f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    color = accentColor.copy(alpha = alpha),
                    shape = CircleShape
                )
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .border(
                        width = 0.8.dp,
                        color = Color.White.copy(alpha = 0.08f),
                        shape = CircleShape
                    )
            )

            Text(
                text = "$pct",
                color = Color.White.copy(alpha = alpha),
                fontSize = fontSizeSp.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}