package com.maxjth.tracememoire.ui.tracejour.components.screen.buttons

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.maxjth.tracememoire.ui.theme.TURQUOISE

@Composable
fun TracePrimaryPillButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(999.dp)

    // ✅ Animation CALME : souffle (plus sombre, plus discret)
    val inf = rememberInfiniteTransition(label = "pillGlow")
    val glow by inf.animateFloat(
        initialValue = 0.14f,
        targetValue = 0.24f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // ✅ Fond plus "CalmTrace" (noir profond, pas gris clair)
    val baseBg = Color(0xFF0A0A0A)
    val innerBg = Color(0xFF0D0D0F) // légèrement différent pour donner de la profondeur

    // ✅ Couleurs thème, MAIS plus sombres/mates
    val mauveDeep = MAUVE.copy(alpha = if (enabled) 0.22f else 0.10f)
    val turquoiseDeep = TURQUOISE.copy(alpha = if (enabled) 0.18f else 0.08f)

    // ✅ Dégradé interne discret (pas “flashy”)
    val premiumBrush = Brush.horizontalGradient(
        colors = listOf(
            turquoiseDeep,
            mauveDeep
        )
    )

    // ✅ Bordure et glow plus sombres
    val borderColor = if (enabled) MAUVE.copy(alpha = 0.40f) else Color.White.copy(alpha = 0.10f)
    val glowColor = if (enabled) MAUVE.copy(alpha = glow) else Color.White.copy(alpha = 0.05f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(shape)
            .background(baseBg)
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .drawBehind {
                // Glow externe subtil
                val stroke = 2.25.dp.toPx()
                drawRoundRect(
                    color = glowColor,
                    style = Stroke(width = stroke),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(999.dp.toPx(), 999.dp.toPx())
                )
            },
        color = Color.Transparent,
        border = BorderStroke(1.dp, borderColor),
        shape = shape,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(shape)
                // Base interne sombre + dégradé discret par-dessus
                .background(innerBg)
                .background(premiumBrush)
                .padding(horizontal = 22.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.2.sp,
                color = if (enabled) Color.White.copy(alpha = 0.88f) else Color.White.copy(alpha = 0.32f)
            )
        }
    }
}