package com.maxjth.tracememoire.ui.tracejour.components.screen.header

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE

@Composable
fun CycleStatusPill(
    label: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isActive) return

    val inf = rememberInfiniteTransition(label = "pill_breath")

    val scale by inf.animateFloat(
        initialValue = 0.995f,
        targetValue = 1.020f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4800,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pill_scale"
    )

    val glowAlpha by inf.animateFloat(
        initialValue = 0.14f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pill_glow"
    )

    val shape = RoundedCornerShape(999.dp)

    Box(
        modifier = modifier
            .padding(top = 15.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            ),
        contentAlignment = Alignment.Center
    ) {
        // HALO DOUX
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(16.dp)
                .clip(shape)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = glowAlpha * 0.90f),
                            TURQUOISE.copy(alpha = glowAlpha)
                        )
                    )
                )
        )

        // CAPSULE
        Box(
            modifier = Modifier
                .clip(shape)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = 0.24f),
                            TURQUOISE.copy(alpha = 0.18f)
                        )
                    )
                )
                .border(
                    width = 1.6.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = 0.68f),
                            TURQUOISE.copy(alpha = 0.95f)
                        )
                    ),
                    shape = shape
                )
                .padding(horizontal = 20.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.94f)
                )

                Text(
                    text = " • ",
                    fontSize = 14.sp,
                    color = MAUVE.copy(alpha = 0.92f)
                )

                Text(
                    text = "en cours",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.78f)
                )
            }
        }
    }
}