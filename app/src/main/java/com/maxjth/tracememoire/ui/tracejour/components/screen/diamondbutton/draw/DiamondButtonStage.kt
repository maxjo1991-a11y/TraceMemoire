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

fun DrawScope.drawDiamondButtonStage(
    center: Offset,
    sizeWidth: Float,
    stageY: Float,
    shimmerShift: Float
) {
    drawDiamondButtonMidShimmer(
        center = center,
        sizeWidth = sizeWidth,
        shimmerShift = shimmerShift
    )

    drawDiamondButtonFloor(
        center = center,
        sizeWidth = sizeWidth,
        stageY = stageY
    )
}

fun DrawScope.drawDiamondButtonMidShimmer(
    center: Offset,
    sizeWidth: Float,
    shimmerShift: Float
) {
    val midLineCenterX = center.x + (sizeWidth * 0.11f * shimmerShift)

    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                MAUVE.copy(alpha = 0.06f),
                WHITE_SOFT.copy(alpha = 0.10f),
                TURQUOISE.copy(alpha = 0.09f),
                Color.Transparent
            ),
            startX = midLineCenterX - sizeWidth * 0.40f,
            endX = midLineCenterX + sizeWidth * 0.40f
        ),
        topLeft = Offset(0f, center.y - 0.9f),
        size = Size(sizeWidth, 1.8f),
        blendMode = BlendMode.SrcOver
    )
}

fun DrawScope.drawDiamondButtonFloor(
    center: Offset,
    sizeWidth: Float,
    stageY: Float
) {
    drawOval(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                MAUVE.copy(alpha = 0.10f),
                WHITE_SOFT.copy(alpha = 0.15f),
                TURQUOISE.copy(alpha = 0.12f),
                Color.Transparent
            )
        ),
        topLeft = Offset(
            x = center.x - sizeWidth * 0.42f,
            y = stageY - 13f
        ),
        size = Size(
            width = sizeWidth * 0.84f,
            height = 26f
        ),
        blendMode = BlendMode.SrcOver
    )

    drawRect(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                MAUVE.copy(alpha = 0.12f),
                WHITE_SOFT.copy(alpha = 0.30f),
                TURQUOISE.copy(alpha = 0.14f),
                Color.Transparent
            )
        ),
        topLeft = Offset(
            x = center.x - sizeWidth * 0.44f,
            y = stageY - 1.2f
        ),
        size = Size(
            width = sizeWidth * 0.88f,
            height = 2.4f
        ),
        blendMode = BlendMode.SrcOver
    )
}

