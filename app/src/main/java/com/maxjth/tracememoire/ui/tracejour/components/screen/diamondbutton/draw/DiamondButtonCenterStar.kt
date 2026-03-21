package com.maxjth.tracememoire.ui.tracejour.components.screen.diamondbutton.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

fun DrawScope.drawDiamondButtonCenterStar(
    center: Offset,
    starPulse: Float,
    starGlowPulse: Float,
    starTwinkle: Float,
    pressGlow: Float,
    rippleProgress: Float
) {
    val outerGlowRadius = 16.5f * starGlowPulse * pressGlow
    val midGlowRadius = 8.2f * starPulse * pressGlow
    val nucleusRadius = 2.35f * pressGlow

    val crossHalfLength = 8.8f * starPulse
    val crossThickness = 1.20f + (starTwinkle - 0.90f) * 0.75f

    val p = ((rippleProgress % 1f) + 1f) % 1f
    val rippleAlpha = if (p < 0.5f) p / 0.5f else (1f - p) / 0.5f
    val rippleRadius = 6.0f + 9.0f * p

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                WHITE_SOFT.copy(alpha = 0.11f),
                WHITE_SOFT.copy(alpha = 0.045f),
                Color.Transparent
            ),
            center = center,
            radius = outerGlowRadius
        ),
        radius = outerGlowRadius,
        center = center
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                WHITE_SOFT.copy(alpha = 0.95f),
                WHITE_SOFT.copy(alpha = 0.34f),
                Color.Transparent
            ),
            center = center,
            radius = midGlowRadius
        ),
        radius = midGlowRadius,
        center = center
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                TURQUOISE.copy(alpha = 0.07f * rippleAlpha),
                MAUVE.copy(alpha = 0.05f * rippleAlpha),
                Color.Transparent
            ),
            center = center,
            radius = rippleRadius
        ),
        radius = rippleRadius,
        center = center
    )

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                WHITE_SOFT.copy(alpha = 0.24f),
                WHITE_SOFT.copy(alpha = 0.98f),
                WHITE_SOFT.copy(alpha = 0.24f),
                Color.Transparent
            )
        ),
        topLeft = Offset(
            x = center.x - crossThickness / 2f,
            y = center.y - crossHalfLength
        ),
        size = Size(
            width = crossThickness,
            height = crossHalfLength * 2f
        ),
        blendMode = BlendMode.SrcOver
    )

    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                WHITE_SOFT.copy(alpha = 0.24f),
                WHITE_SOFT.copy(alpha = 0.98f),
                WHITE_SOFT.copy(alpha = 0.24f),
                Color.Transparent
            )
        ),
        topLeft = Offset(
            x = center.x - crossHalfLength,
            y = center.y - crossThickness / 2f
        ),
        size = Size(
            width = crossHalfLength * 2f,
            height = crossThickness
        ),
        blendMode = BlendMode.SrcOver
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                WHITE_SOFT.copy(alpha = 0.22f * rippleAlpha),
                TURQUOISE.copy(alpha = 0.10f * rippleAlpha),
                MAUVE.copy(alpha = 0.08f * rippleAlpha),
                Color.Transparent
            ),
            center = center,
            radius = 4.8f * pressGlow
        ),
        radius = 4.8f * pressGlow,
        center = center
    )

    drawCircle(
        color = WHITE_SOFT.copy(alpha = 1.0f),
        radius = nucleusRadius,
        center = center
    )

    drawCircle(
        color = Color.White,
        radius = 1.15f * pressGlow,
        center = center
    )
}