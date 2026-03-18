package com.maxjth.tracememoire.ui.tracejour.components.screen.diamondbutton.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

fun DrawScope.drawDiamondButtonDiamond(
    center: Offset,
    outerDiamondSize: Float,
    innerDiamondSize: Float,
    slowRotate: Float
) {
    val outerPath = Path().apply {
        moveTo(center.x, center.y - outerDiamondSize)
        lineTo(center.x + outerDiamondSize, center.y)
        lineTo(center.x, center.y + outerDiamondSize)
        lineTo(center.x - outerDiamondSize, center.y)
        close()
    }

    val innerPath = Path().apply {
        moveTo(center.x, center.y - innerDiamondSize)
        lineTo(center.x + innerDiamondSize, center.y)
        lineTo(center.x, center.y + innerDiamondSize)
        lineTo(center.x - innerDiamondSize, center.y)
        close()
    }

    // Glow externe
    rotate(slowRotate, pivot = center) {
        drawPath(
            path = outerPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    MAUVE.copy(alpha = 0.30f),
                    MAUVE.copy(alpha = 0.14f),
                    TURQUOISE.copy(alpha = 0.14f),
                    TURQUOISE.copy(alpha = 0.30f)
                ),
                start = Offset(
                    x = center.x - outerDiamondSize,
                    y = center.y - outerDiamondSize
                ),
                end = Offset(
                    x = center.x + outerDiamondSize,
                    y = center.y + outerDiamondSize
                )
            ),
            style = Stroke(
                width = 7.0f,
                cap = StrokeCap.Round
            )
        )
    }

    // Losange principal
    rotate(slowRotate, pivot = center) {
        drawPath(
            path = outerPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    MAUVE.copy(alpha = 1.0f),
                    MAUVE.copy(alpha = 0.97f),
                    WHITE_SOFT.copy(alpha = 0.92f),
                    TURQUOISE.copy(alpha = 0.97f),
                    TURQUOISE.copy(alpha = 1.0f)
                ),
                start = Offset(
                    x = center.x - outerDiamondSize,
                    y = center.y - outerDiamondSize
                ),
                end = Offset(
                    x = center.x + outerDiamondSize,
                    y = center.y + outerDiamondSize
                )
            ),
            style = Stroke(
                width = 3.0f,
                cap = StrokeCap.Round
            )
        )

        drawPath(
            path = innerPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    MAUVE.copy(alpha = 0.34f),
                    WHITE_SOFT.copy(alpha = 0.88f),
                    TURQUOISE.copy(alpha = 0.72f)
                ),
                start = Offset(
                    x = center.x - innerDiamondSize,
                    y = center.y - innerDiamondSize
                ),
                end = Offset(
                    x = center.x + innerDiamondSize,
                    y = center.y + innerDiamondSize
                )
            ),
            style = Stroke(
                width = 2.0f,
                cap = StrokeCap.Round
            )
        )
    }

    // Pointes différenciées
    val topPoint = Offset(center.x, center.y - outerDiamondSize)
    val rightPoint = Offset(center.x + outerDiamondSize, center.y)
    val bottomPoint = Offset(center.x, center.y + outerDiamondSize)
    val leftPoint = Offset(center.x - outerDiamondSize, center.y)

    // Haut = mauve doux
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                MAUVE.copy(alpha = 0.20f),
                Color.Transparent
            ),
            center = Offset(
                x = topPoint.x - 1.4f,
                y = topPoint.y - 1.2f
            ),
            radius = 7.6f
        ),
        radius = 7.6f,
        center = Offset(
            x = topPoint.x - 1.4f,
            y = topPoint.y - 1.2f
        )
    )

    // Gauche = mauve fort
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                MAUVE.copy(alpha = 0.36f),
                Color.Transparent
            ),
            center = Offset(
                x = leftPoint.x - 1.7f,
                y = leftPoint.y
            ),
            radius = 10.8f
        ),
        radius = 10.8f,
        center = Offset(
            x = leftPoint.x - 1.7f,
            y = leftPoint.y
        )
    )

    // Droite = turquoise fort
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                TURQUOISE.copy(alpha = 0.36f),
                Color.Transparent
            ),
            center = Offset(
                x = rightPoint.x + 1.7f,
                y = rightPoint.y
            ),
            radius = 10.8f
        ),
        radius = 10.8f,
        center = Offset(
            x = rightPoint.x + 1.7f,
            y = rightPoint.y
        )
    )

    // Bas = turquoise doux
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                TURQUOISE.copy(alpha = 0.20f),
                Color.Transparent
            ),
            center = Offset(
                x = bottomPoint.x + 1.2f,
                y = bottomPoint.y + 1.5f
            ),
            radius = 7.8f
        ),
        radius = 7.8f,
        center = Offset(
            x = bottomPoint.x + 1.2f,
            y = bottomPoint.y + 1.5f
        )
    )
}