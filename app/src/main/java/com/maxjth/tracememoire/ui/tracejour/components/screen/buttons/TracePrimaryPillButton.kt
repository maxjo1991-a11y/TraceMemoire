package com.maxjth.tracememoire.ui.tracejour.components.screen.buttons

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.tracejour.components.screen.utils.safeClickable

@Composable
fun TracePrimaryPillButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = MAUVE,

    // ✅ plus safe que (accent == TURQUOISE)
    accentIsTurquoise: Boolean = false
) {
    val shape = RoundedCornerShape(999.dp)

    // ✅ Glow LED
    val inf = rememberInfiniteTransition(label = "pillGlow")
    val glow by inf.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.42f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val innerBg = Color(0xFF0D0D10)

    // ✅ Dégradé interne (turquoise + accent)
    val accentDeep = accent.copy(alpha = if (enabled) 0.28f else 0.10f)
    val turquoiseDeep = TURQUOISE.copy(alpha = if (enabled) 0.20f else 0.08f)

    val premiumBrush = if (accentIsTurquoise) {
        Brush.horizontalGradient(listOf(accentDeep, turquoiseDeep))
    } else {
        Brush.horizontalGradient(listOf(turquoiseDeep, accentDeep))
    }

    // ✅ Bordure + halo
    val borderColor =
        if (enabled) accent.copy(alpha = 0.52f) else Color.White.copy(alpha = 0.10f)

    val haloOuter =
        if (enabled) accent.copy(alpha = glow * 0.55f) else Color.Transparent

    val haloInner =
        if (enabled) accent.copy(alpha = glow) else Color.White.copy(alpha = 0.05f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(66.dp)
            .clip(shape)

            // ✅ IMPORTANT: c’est safeClickable QUI DOIT GÉRER enabled
            // (sinon tu peux encore avoir des comportements bizarres)
            .safeClickable(enabled = enabled) {
                onClick()
            }

            .drawBehind {
                val r = CornerRadius(999.dp.toPx(), 999.dp.toPx())

                // ✅ halo externe (plus large, très doux)
                drawRoundRect(
                    color = haloOuter,
                    style = Stroke(width = 6.dp.toPx()),
                    cornerRadius = r
                )

                // ✅ halo interne (plus fin, plus net)
                drawRoundRect(
                    color = haloInner,
                    style = Stroke(width = 3.dp.toPx()),
                    cornerRadius = r
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
                .height(66.dp)
                .clip(shape)
                .background(innerBg)
                .background(premiumBrush)
                .padding(horizontal = 22.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 19.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.4.sp,
                textAlign = TextAlign.Center,
                color = if (enabled) Color.White.copy(alpha = 0.95f)
                else Color.White.copy(alpha = 0.32f)
            )
        }
    }
}