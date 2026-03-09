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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE

@Composable
fun CycleStatusPill(
    label: String,
    isActive: Boolean,
    modifier: Modifier = Modifier
)
 {
    if (!isActive) return

    // 🌫 Micro breathing très subtil
    val inf = rememberInfiniteTransition(label = "pill_breath")
    val scale by inf.animateFloat(
        initialValue = 0.995f,
        targetValue = 1.036f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pill_scale"
    )

    Box(
        modifier = Modifier
            .padding(top = 15.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(999.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MAUVE.copy(alpha = 0.28f),   // ← plus mauve
                        TURQUOISE.copy(alpha = 0.20f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        MAUVE.copy(alpha = 0.10f),
                        TURQUOISE.copy(alpha = 0.8f)
                    )
                ),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.92f)
            )

            Text(
                text = " • ",
                fontSize = 14.sp,
                color = MAUVE.copy(alpha = 0.9f) // ← bullet plus mauve
            )

            Text(
                text = "en cours",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.75f)
            )
        }
    }
}