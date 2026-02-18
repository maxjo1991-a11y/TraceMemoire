package com.maxjth.tracememoire.ui.tracejour.components.screen.deepen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.tracejour.components.screen.utils.safeClickable

@Composable
fun DeePenMemoryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(999.dp)

    // ✅ Animation glow CALME / premium
    val inf = rememberInfiniteTransition(label = "deepenGlow")

    val glowAlpha by inf.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.58f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // ✅ Quand désactivé : on baisse l’intensité
    val enabledFactor = if (enabled) 1f else 0.55f

    val borderColor = MAUVE.copy(alpha = 0.55f * enabledFactor)
    val glowColor = MAUVE.copy(alpha = glowAlpha * enabledFactor)

    val gradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF141418),
            Color(0xFF1A1623),
            Color(0xFF201233)
        )
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(66.dp)
            .clip(shape)
            // ✅ FIX CRASH : on remplace clickable() par safeClickable()
            .safeClickable(enabled = enabled) {
                onClick()
            }
            .drawBehind {
                drawRoundRect(
                    color = glowColor,
                    style = Stroke(width = 2.6.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                        999.dp.toPx(),
                        999.dp.toPx()
                    )
                )
            },
        color = Color.Transparent,
        border = BorderStroke(1.2.dp, borderColor),
        shape = shape,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .background(gradient)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.4.sp,
                    color = Color.White.copy(alpha = if (enabled) 0.96f else 0.70f)
                )
                Text(
                    text = "  →",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.4.sp,
                    color = Color.White.copy(alpha = if (enabled) 0.92f else 0.66f)
                )
            }
        }
    }
}