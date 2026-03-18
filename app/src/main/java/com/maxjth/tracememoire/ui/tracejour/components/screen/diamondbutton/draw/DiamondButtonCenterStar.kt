package com.maxjth.tracememoire.ui.tracejour.components.screen.diamondbutton.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

fun DrawScope.drawDiamondButtonCenterStar(
    center: Offset,
    starPulse: Float,
    starGlowPulse: Float,
    pressGlow: Float
) {
    val outerGlowRadius = 16.5f * starGlowPulse * pressGlow
    val midGlowRadius = 8.2f * starPulse * pressGlow
    val nucleusRadius = 2.35f * pressGlow

    val crossHalfLength = 8.8f * starPulse
    val crossThickness = 1.35f

    // Halo externe plus visible
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

    // Halo moyen respirant
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

    // Croisillon vertical façon étoile
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                WHITE_SOFT.copy(alpha = 0.28f),
                WHITE_SOFT.copy(alpha = 0.98f),
                WHITE_SOFT.copy(alpha = 0.28f),
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

    // Croisillon horizontal façon étoile
    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                WHITE_SOFT.copy(alpha = 0.28f),
                WHITE_SOFT.copy(alpha = 0.98f),
                WHITE_SOFT.copy(alpha = 0.28f),
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

    // Petit coeur blanc très visible
    drawCircle(
        color = WHITE_SOFT.copy(alpha = 1.0f),
        radius = nucleusRadius,
        center = center
    )

    // Point central ultra net
    drawCircle(
        color = Color.White,
        radius = 1.15f * pressGlow,
        center = center
    )
}