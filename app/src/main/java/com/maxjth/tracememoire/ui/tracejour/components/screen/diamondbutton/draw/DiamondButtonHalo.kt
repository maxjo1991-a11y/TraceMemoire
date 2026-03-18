package com.maxjth.tracememoire.ui.tracejour.components.screen.diamondbutton.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

fun DrawScope.drawDiamondButtonHalo(
    center: Offset,
    outerDiamondSize: Float,
    haloOuterRadius: Float,
    haloInnerRadius: Float
) {

    // Halo central doux (blanc très léger)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                WHITE_SOFT.copy(alpha = 0.018f),
                Color.Transparent
            ),
            center = center,
            radius = haloOuterRadius
        ),
        radius = haloOuterRadius,
        center = center
    )

    // Halo gauche (mauve)
    val leftCenter = Offset(
        x = center.x - outerDiamondSize * 0.70f,
        y = center.y
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                MAUVE.copy(alpha = 0.12f),
                Color.Transparent
            ),
            center = leftCenter,
            radius = haloOuterRadius * 0.88f
        ),
        radius = haloOuterRadius * 0.88f,
        center = leftCenter
    )

    // Halo droite (turquoise)
    val rightCenter = Offset(
        x = center.x + outerDiamondSize * 0.70f,
        y = center.y
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                TURQUOISE.copy(alpha = 0.12f),
                Color.Transparent
            ),
            center = rightCenter,
            radius = haloOuterRadius * 0.88f
        ),
        radius = haloOuterRadius * 0.88f,
        center = rightCenter
    )

    // Halo intérieur (blanc très léger)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                WHITE_SOFT.copy(alpha = 0.026f),
                Color.Transparent
            ),
            center = center,
            radius = haloInnerRadius
        ),
        radius = haloInnerRadius,
        center = center
    )
}

