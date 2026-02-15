package com.maxjth.tracememoire.ui.tracejour.components.screen.halo

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun SoftHaloCard(
    modifier: Modifier = Modifier,
    radius: Dp = 22.dp,
    padding: Dp = 18.dp,
    isPremium: Boolean = false,
    enabled: Boolean = true,

    baseBg: Color = Color(0xFF070707),     // ✅ PLUS sombre qu’avant
    glassTint: Color = Color.White,

    haloMauve: Color = Color(0xFF8E6BFF),
    haloTurquoise: Color = Color(0xFF00B3B3),

    borderWidth: Dp = 1.35.dp,
    outerGlowWidth: Dp = 2.4.dp,

    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)

    val infinite = rememberInfiniteTransition(label = "softHalo")
    val t by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "softHaloPulse"
    )

    val worldColor = if (isPremium) haloTurquoise else haloMauve
    val alphaBase = if (enabled) 1f else 0.55f

    val haloA = (0.22f + 0.05f * t) * alphaBase
    val haloB = (0.04f + 0.05f * (1f - t)) * alphaBase

    val glassA = (0.07f + 0.03f * t) * alphaBase   // ✅ Reflets plus doux

    val borderColor = worldColor.copy(alpha = (0.18f + 0.07f * t) * alphaBase)
    val innerLine = Color.White.copy(alpha = 0.05f * alphaBase)

    Box(
        modifier = modifier
            .clip(shape)
            .softHaloBackground(
                radius = radius,
                baseBg = baseBg,
                glassTint = glassTint,
                worldColor = worldColor,
                haloA = haloA,
                haloB = haloB,
                glassA = glassA,
                borderColor = borderColor,
                borderWidth = borderWidth,
                outerGlowWidth = outerGlowWidth,
                innerLine = innerLine,
                enabled = enabled
            )
            .padding(padding),
        content = content
    )
}

fun Modifier.softHaloBackground(
    radius: Dp,
    baseBg: Color,
    glassTint: Color,
    worldColor: Color,
    haloA: Float,
    haloB: Float,
    glassA: Float,
    borderColor: Color,
    borderWidth: Dp,
    outerGlowWidth: Dp,
    innerLine: Color,
    enabled: Boolean
): Modifier {

    val shape = RoundedCornerShape(radius)

    return this
        .clip(shape)
        .background(baseBg)
        .drawBehind {

            val w = size.width
            val h = size.height
            val r = radius.toPx().coerceAtMost(min(w, h) / 2f)
            val cr = CornerRadius(r, r)

            // ✅ HALO GLOBAL (plus discret / premium)
            val haloBrush = Brush.radialGradient(
                colors = listOf(
                    worldColor.copy(alpha = 0f),
                    worldColor.copy(alpha = haloA),
                    worldColor.copy(alpha = haloB),
                    worldColor.copy(alpha = 0f)
                ),
                center = Offset(w * 0.5f, h * 0.42f),
                radius = min(w, h) * 0.92f
            )

            drawRoundRect(
                brush = haloBrush,
                size = Size(w, h),
                cornerRadius = cr
            )

            // ✅ BRUME VERRE (adoucie)
            val fog = Brush.verticalGradient(
                colors = listOf(
                    glassTint.copy(alpha = 0.04f),
                    glassTint.copy(alpha = 0.015f),
                    Color.Transparent
                )
            )

            drawRoundRect(
                brush = fog,
                size = Size(w, h),
                cornerRadius = cr
            )

            // ✅ REFLET BAS TRÈS FAIBLE
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        glassTint.copy(alpha = 0.02f)
                    )
                ),
                size = Size(w, h),
                cornerRadius = cr
            )

            val bw = borderWidth.toPx()
            val ow = outerGlowWidth.toPx()

            // ✅ GLOW EXTERNE DOUX
            drawRoundRect(
                color = borderColor.copy(alpha = borderColor.alpha * 0.5f),
                size = Size(w, h),
                cornerRadius = cr,
                style = Stroke(width = ow)
            )

            // ✅ TRAIT FIN PRINCIPAL
            drawRoundRect(
                color = borderColor,
                size = Size(w, h),
                cornerRadius = cr,
                style = Stroke(width = bw)
            )

            // ✅ MINI INNER LINE
            drawRoundRect(
                color = innerLine,
                size = Size(w, h),
                cornerRadius = cr,
                style = Stroke(width = bw * 0.65f)
            )
        }
}