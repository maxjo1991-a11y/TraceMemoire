package com.maxjth.tracememoire.ui.tracejour.components.screen.diamondbutton.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import kotlin.math.max

fun DrawScope.drawDiamondButtonDiamond(
    center: Offset,
    outerDiamondSize: Float,
    innerDiamondSize: Float,
    slowRotate: Float,
    runnerProgress: Float,
    innerRunnerProgress: Float,
    bridgeRunnerProgress: Float,
    driftRunnerProgress: Float,
    rippleProgress: Float
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

    val outerGlowStroke = 7.0f
    val outerStroke = 3.0f
    val innerStroke = 2.0f

    val outerGlowStyle = Stroke(
        width = outerGlowStroke,
        cap = StrokeCap.Butt,
        join = StrokeJoin.Miter,
        miter = 10f
    )

    val outerLineStyle = Stroke(
        width = outerStroke,
        cap = StrokeCap.Butt,
        join = StrokeJoin.Miter,
        miter = 10f
    )

    val innerLineStyle = Stroke(
        width = innerStroke,
        cap = StrokeCap.Butt,
        join = StrokeJoin.Miter,
        miter = 10f
    )

    rotate(slowRotate, pivot = center) {
        drawPath(
            path = outerPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    MAUVE.copy(alpha = 0.24f),
                    MAUVE.copy(alpha = 0.09f),
                    TURQUOISE.copy(alpha = 0.09f),
                    TURQUOISE.copy(alpha = 0.24f)
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
            style = outerGlowStyle
        )

        drawPath(
            path = outerPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    MAUVE.copy(alpha = 1.0f),
                    MAUVE.copy(alpha = 0.97f),
                    WHITE_SOFT.copy(alpha = 0.93f),
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
            style = outerLineStyle
        )

        drawPath(
            path = innerPath,
            brush = Brush.linearGradient(
                colors = listOf(
                    MAUVE.copy(alpha = 0.28f),
                    WHITE_SOFT.copy(alpha = 0.84f),
                    TURQUOISE.copy(alpha = 0.66f)
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
            style = innerLineStyle
        )

        drawDiamondVerticalRipple(
            center = center,
            outerDiamondSize = outerDiamondSize,
            innerDiamondSize = innerDiamondSize,
            progress = rippleProgress
        )

        val outerOrb = outerRunnerSample(
            center = center,
            size = outerDiamondSize,
            progress = runnerProgress
        )

        val innerOrb = diamondRunnerPoint(
            center = center,
            size = innerDiamondSize,
            progress = innerRunnerProgress
        )

        val bridgeOrb = bridgeRunnerSample(
            center = center,
            outerSize = outerDiamondSize,
            innerSize = innerDiamondSize,
            progress = bridgeRunnerProgress
        )

        val driftOrb = driftRunnerSample(
            center = center,
            outerSize = outerDiamondSize,
            innerSize = innerDiamondSize,
            progress = driftRunnerProgress
        )

        if (outerOrb.alpha > 0.01f) {
            drawLivingOrb(
                point = outerOrb.point,
                glowRadius = 14.5f,
                coreRadius = 5.8f,
                nucleusRadius = 1.7f,
                primary = TURQUOISE.copy(alpha = 0.88f * outerOrb.alpha),
                secondary = WHITE_SOFT.copy(alpha = 0.98f * outerOrb.alpha),
                outerAlpha = 0.30f * outerOrb.alpha
            )
        }

        drawLivingOrb(
            point = innerOrb,
            glowRadius = 10.8f,
            coreRadius = 4.4f,
            nucleusRadius = 1.35f,
            primary = MAUVE.copy(alpha = 0.82f),
            secondary = WHITE_SOFT.copy(alpha = 0.97f),
            outerAlpha = 0.24f
        )

        if (bridgeOrb.alpha > 0.01f) {
            drawLivingOrb(
                point = bridgeOrb.point,
                glowRadius = 11.6f,
                coreRadius = 4.6f,
                nucleusRadius = 1.25f,
                primary = TURQUOISE.copy(alpha = 0.76f * bridgeOrb.alpha),
                secondary = WHITE_SOFT.copy(alpha = 0.96f * bridgeOrb.alpha),
                outerAlpha = 0.20f * bridgeOrb.alpha
            )
        }

        if (driftOrb.alpha > 0.01f) {
            drawLivingOrb(
                point = driftOrb.point,
                glowRadius = 9.6f,
                coreRadius = 3.8f,
                nucleusRadius = 1.10f,
                primary = MAUVE.copy(alpha = 0.72f * driftOrb.alpha),
                secondary = WHITE_SOFT.copy(alpha = 0.90f * driftOrb.alpha),
                outerAlpha = 0.15f * driftOrb.alpha
            )
        }
    }

    val topPoint = Offset(center.x, center.y - outerDiamondSize)
    val rightPoint = Offset(center.x + outerDiamondSize, center.y)
    val bottomPoint = Offset(center.x, center.y + outerDiamondSize)
    val leftPoint = Offset(center.x - outerDiamondSize, center.y)

    val topRadius = max(5.8f, outerStroke * 1.8f)
    val sideRadius = max(7.2f, outerStroke * 2.2f)
    val bottomRadius = max(6.0f, outerStroke * 1.9f)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                MAUVE.copy(alpha = 0.14f),
                Color.Transparent
            ),
            center = Offset(
                x = topPoint.x,
                y = topPoint.y - 0.8f
            ),
            radius = topRadius
        ),
        radius = topRadius,
        center = Offset(
            x = topPoint.x,
            y = topPoint.y - 0.8f
        )
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                MAUVE.copy(alpha = 0.20f),
                Color.Transparent
            ),
            center = Offset(
                x = leftPoint.x - 0.7f,
                y = leftPoint.y
            ),
            radius = sideRadius
        ),
        radius = sideRadius,
        center = Offset(
            x = leftPoint.x - 0.7f,
            y = leftPoint.y
        )
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                TURQUOISE.copy(alpha = 0.20f),
                Color.Transparent
            ),
            center = Offset(
                x = rightPoint.x + 0.7f,
                y = rightPoint.y
            ),
            radius = sideRadius
        ),
        radius = sideRadius,
        center = Offset(
            x = rightPoint.x + 0.7f,
            y = rightPoint.y
        )
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                TURQUOISE.copy(alpha = 0.14f),
                Color.Transparent
            ),
            center = Offset(
                x = bottomPoint.x,
                y = bottomPoint.y + 0.9f
            ),
            radius = bottomRadius
        ),
        radius = bottomRadius,
        center = Offset(
            x = bottomPoint.x,
            y = bottomPoint.y + 0.9f
        )
    )
}

private fun DrawScope.drawLivingOrb(
    point: Offset,
    glowRadius: Float,
    coreRadius: Float,
    nucleusRadius: Float,
    primary: Color,
    secondary: Color,
    outerAlpha: Float
) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                secondary.copy(alpha = outerAlpha),
                primary.copy(alpha = outerAlpha * 0.62f),
                Color.Transparent
            ),
            center = point,
            radius = glowRadius
        ),
        radius = glowRadius,
        center = point
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                secondary,
                primary,
                Color.Transparent
            ),
            center = point,
            radius = coreRadius
        ),
        radius = coreRadius,
        center = point
    )

    drawCircle(
        color = WHITE_SOFT.copy(alpha = 0.99f),
        radius = nucleusRadius,
        center = point
    )
}

private fun DrawScope.drawDiamondVerticalRipple(
    center: Offset,
    outerDiamondSize: Float,
    innerDiamondSize: Float,
    progress: Float
) {
    val p = ((progress % 1f) + 1f) % 1f
    val startY = center.y - outerDiamondSize * 0.92f
    val endY = center.y + outerDiamondSize * 0.92f
    val rippleY = lerpFloat(startY, endY, p)

    val outerRadius = outerDiamondSize * 0.34f
    val innerRadius = innerDiamondSize * 0.44f

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                WHITE_SOFT.copy(alpha = 0.065f),
                MAUVE.copy(alpha = 0.050f),
                Color.Transparent
            ),
            center = Offset(center.x, rippleY),
            radius = outerRadius
        ),
        radius = outerRadius,
        center = Offset(center.x, rippleY)
    )

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                WHITE_SOFT.copy(alpha = 0.090f),
                TURQUOISE.copy(alpha = 0.065f),
                Color.Transparent
            ),
            center = Offset(center.x, rippleY),
            radius = innerRadius
        ),
        radius = innerRadius,
        center = Offset(center.x, rippleY)
    )

    drawLine(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                MAUVE.copy(alpha = 0.10f),
                WHITE_SOFT.copy(alpha = 0.22f),
                TURQUOISE.copy(alpha = 0.12f),
                Color.Transparent
            ),
            startY = rippleY - innerDiamondSize * 0.44f,
            endY = rippleY + innerDiamondSize * 0.44f
        ),
        start = Offset(center.x, rippleY - innerDiamondSize * 0.44f),
        end = Offset(center.x, rippleY + innerDiamondSize * 0.44f),
        strokeWidth = 1.7f,
        cap = StrokeCap.Round
    )
}

private data class RunnerSample(
    val point: Offset,
    val alpha: Float
)

private fun outerRunnerSample(
    center: Offset,
    size: Float,
    progress: Float
): RunnerSample {
    val p = ((progress % 1f) + 1f) % 1f

    val top = Offset(center.x, center.y - size)
    val right = Offset(center.x + size, center.y)
    val bottom = Offset(center.x, center.y + size)
    val left = Offset(center.x - size, center.y)

    return when {
        // top -> right
        p < 0.16f -> {
            val t = smoothSegment((p - 0.00f) / 0.16f)
            RunnerSample(
                point = lerpOffset(top, right, t),
                alpha = 1f
            )
        }

        // pause à droite
        p < 0.24f -> {
            RunnerSample(
                point = right,
                alpha = 1f
            )
        }

        // right -> top (retour inverse)
        p < 0.40f -> {
            val t = smoothSegment((p - 0.24f) / 0.16f)
            RunnerSample(
                point = lerpOffset(right, top, t),
                alpha = 1f
            )
        }

        // mini fade sur le haut
        p < 0.46f -> {
            val t = (p - 0.40f) / 0.06f
            RunnerSample(
                point = top,
                alpha = 1f - 0.55f * t
            )
        }

        // top -> left
        p < 0.62f -> {
            val t = smoothSegment((p - 0.46f) / 0.16f)
            RunnerSample(
                point = lerpOffset(top, left, t),
                alpha = 0.80f + 0.20f * fadeInOut(t)
            )
        }

        // pause à gauche
        p < 0.70f -> {
            RunnerSample(
                point = left,
                alpha = 1f
            )
        }

        // left -> bottom
        p < 0.84f -> {
            val t = smoothSegment((p - 0.70f) / 0.14f)
            RunnerSample(
                point = lerpOffset(left, bottom, t),
                alpha = 1f
            )
        }

        // bottom -> right
        else -> {
            val t = smoothSegment((p - 0.84f) / 0.16f)
            RunnerSample(
                point = lerpOffset(bottom, right, t),
                alpha = 0.88f + 0.12f * fadeInOut(t)
            )
        }
    }
}

private fun bridgeRunnerSample(
    center: Offset,
    outerSize: Float,
    innerSize: Float,
    progress: Float
): RunnerSample {
    val p = ((progress % 1f) + 1f) % 1f

    val outerTop = Offset(center.x, center.y - outerSize)
    val outerRight = Offset(center.x + outerSize, center.y)
    val outerBottom = Offset(center.x, center.y + outerSize)
    val outerLeft = Offset(center.x - outerSize, center.y)

    val innerTop = Offset(center.x, center.y - innerSize)
    val innerRight = Offset(center.x + innerSize, center.y)
    val innerBottom = Offset(center.x, center.y + innerSize)
    val innerLeft = Offset(center.x - innerSize, center.y)

    return when {
        p < 0.16f -> {
            val t = smoothSegment(p / 0.16f)
            RunnerSample(
                point = lerpOffset(outerTop, outerRight, t),
                alpha = fadeInOut(t)
            )
        }

        p < 0.24f -> {
            val t = (p - 0.16f) / 0.08f
            RunnerSample(
                point = outerRight,
                alpha = 1f - t
            )
        }

        p < 0.42f -> {
            val t = smoothSegment((p - 0.24f) / 0.18f)
            RunnerSample(
                point = lerpOffset(innerLeft, innerTop, t),
                alpha = fadeInOut(t)
            )
        }

        p < 0.52f -> {
            val t = (p - 0.42f) / 0.10f
            RunnerSample(
                point = innerTop,
                alpha = 1f - t
            )
        }

        p < 0.72f -> {
            val t = smoothSegment((p - 0.52f) / 0.20f)
            RunnerSample(
                point = lerpOffset(outerBottom, outerLeft, t),
                alpha = 0.18f + 0.82f * fadeInOut(t)
            )
        }

        p < 0.84f -> {
            val t = (p - 0.72f) / 0.12f
            RunnerSample(
                point = outerLeft,
                alpha = 1f - t
            )
        }

        else -> {
            val t = smoothSegment((p - 0.84f) / 0.16f)
            RunnerSample(
                point = lerpOffset(innerBottom, innerRight, t),
                alpha = 0.28f + 0.72f * fadeInOut(t)
            )
        }
    }
}

private fun driftRunnerSample(
    center: Offset,
    outerSize: Float,
    innerSize: Float,
    progress: Float
): RunnerSample {
    val p = ((progress % 1f) + 1f) % 1f

    val outerTop = Offset(center.x, center.y - outerSize)
    val outerRight = Offset(center.x + outerSize, center.y)
    val outerBottom = Offset(center.x, center.y + outerSize)
    val outerLeft = Offset(center.x - outerSize, center.y)

    val innerTop = Offset(center.x, center.y - innerSize)
    val innerRight = Offset(center.x + innerSize, center.y)
    val innerBottom = Offset(center.x, center.y + innerSize)
    val innerLeft = Offset(center.x - innerSize, center.y)

    return when {
        p < 0.22f -> {
            val t = smoothSegment(p / 0.22f)
            RunnerSample(
                point = lerpOffset(outerLeft, outerTop, t),
                alpha = 0.24f + 0.76f * fadeInOut(t)
            )
        }

        p < 0.34f -> {
            val t = (p - 0.22f) / 0.12f
            RunnerSample(
                point = outerTop,
                alpha = 0.8f - 0.8f * t
            )
        }

        p < 0.58f -> {
            val t = smoothSegment((p - 0.34f) / 0.24f)
            RunnerSample(
                point = lerpOffset(innerRight, innerBottom, t),
                alpha = 0.18f + 0.82f * fadeInOut(t)
            )
        }

        p < 0.70f -> {
            val t = (p - 0.58f) / 0.12f
            RunnerSample(
                point = innerBottom,
                alpha = 0.9f - 0.9f * t
            )
        }

        else -> {
            val t = smoothSegment((p - 0.70f) / 0.30f)
            RunnerSample(
                point = lerpOffset(outerRight, outerBottom, t),
                alpha = 0.12f + 0.88f * fadeInOut(t)
            )
        }
    }
}

private fun diamondRunnerPoint(
    center: Offset,
    size: Float,
    progress: Float
): Offset {
    val p = ((progress % 1f) + 1f) % 1f

    val top = Offset(center.x, center.y - size)
    val right = Offset(center.x + size, center.y)
    val bottom = Offset(center.x, center.y + size)
    val left = Offset(center.x - size, center.y)

    return when {
        p < 0.25f -> lerpOffset(top, right, smoothSegment(p / 0.25f))
        p < 0.50f -> lerpOffset(right, bottom, smoothSegment((p - 0.25f) / 0.25f))
        p < 0.75f -> lerpOffset(bottom, left, smoothSegment((p - 0.50f) / 0.25f))
        else -> lerpOffset(left, top, smoothSegment((p - 0.75f) / 0.25f))
    }
}

private fun fadeInOut(t: Float): Float {
    return if (t < 0.5f) {
        t / 0.5f
    } else {
        (1f - t) / 0.5f
    }.coerceIn(0f, 1f)
}

private fun smoothSegment(t: Float): Float {
    return t * t * (3f - 2f * t)
}

private fun lerpFloat(
    start: Float,
    end: Float,
    t: Float
): Float {
    return start + (end - start) * t
}

private fun lerpOffset(
    start: Offset,
    end: Offset,
    t: Float
): Offset {
    return Offset(
        x = start.x + (end.x - start.x) * t,
        y = start.y + (end.y - start.y) * t
    )
}