package com.maxjth.tracememoire.ui.systeme.saturne

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import kotlin.math.min

@Composable
fun SaturneCanvas(
    ringStartColor: Color,
    ringEndColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val shortest = min(size.width, size.height)

        val ringWidth = 8f
        val radius = (size.minDimension / 2f) - (ringWidth * 0.95f)

        val innerLineWidth = 2.0f
        val centerGap = 22f

        val ringGradient = Brush.sweepGradient(
            colors = listOf(
                ringStartColor.copy(alpha = 0.96f),
                ringEndColor.copy(alpha = 0.98f),
                ringStartColor.copy(alpha = 0.96f)
            ),
            center = center
        )

        // =========================
        // FOND INTERNE LÉGER
        // =========================
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF060812).copy(alpha = 0.28f),
                    Color(0xFF04060B).copy(alpha = 0.18f),
                    BG_DEEP.copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = center,
                radius = radius * 0.96f
            ),
            radius = radius * 0.96f,
            center = center
        )

        // =========================
        // HALO EXTERNE
        // =========================
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    ringEndColor.copy(alpha = 0.08f),
                    ringStartColor.copy(alpha = 0.06f),
                    Color.Transparent
                ),
                center = center,
                radius = radius * 1.12f
            ),
            radius = radius * 1.12f,
            center = center
        )

        // =========================
        // GALAXIE FIXE À L’INTÉRIEUR DU CERCLE
        // =========================
        val clipCircle = Path().apply {
            addOval(
                androidx.compose.ui.geometry.Rect(
                    left = center.x - radius + ringWidth,
                    top = center.y - radius + ringWidth,
                    right = center.x + radius - ringWidth,
                    bottom = center.y + radius - ringWidth
                )
            )
        }

        clipPath(clipCircle) {
            val nebulaCenter = Offset(center.x, center.y + radius * 0.06f)
            val lowerCore = Offset(center.x, center.y + radius * 0.36f)
            val leftCloud = Offset(center.x - radius * 0.26f, center.y + radius * 0.12f)
            val rightCloud = Offset(center.x + radius * 0.24f, center.y + radius * 0.10f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ringStartColor.copy(alpha = 0.12f),
                        ringEndColor.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = nebulaCenter,
                    radius = radius * 0.72f
                ),
                radius = radius * 0.72f,
                center = nebulaCenter
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ringEndColor.copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = lowerCore,
                    radius = radius * 0.34f
                ),
                radius = radius * 0.34f,
                center = lowerCore
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ringStartColor.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = leftCloud,
                    radius = radius * 0.26f
                ),
                radius = radius * 0.26f,
                center = leftCloud
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ringEndColor.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = rightCloud,
                    radius = radius * 0.24f
                ),
                radius = radius * 0.24f,
                center = rightCloud
            )

            val stars = listOf(
                Triple(
                    Offset(center.x - radius * 0.68f, center.y - radius * 0.42f),
                    shortest * 0.0050f,
                    Color.White.copy(alpha = 0.62f)
                ),
                Triple(
                    Offset(center.x - radius * 0.55f, center.y - radius * 0.35f),
                    shortest * 0.0036f,
                    Color.White.copy(alpha = 0.48f)
                ),
                Triple(
                    Offset(center.x - radius * 0.42f, center.y - radius * 0.30f),
                    shortest * 0.0030f,
                    ringStartColor.copy(alpha = 0.50f)
                ),
                Triple(
                    Offset(center.x + radius * 0.46f, center.y - radius * 0.34f),
                    shortest * 0.0038f,
                    Color.White.copy(alpha = 0.54f)
                ),
                Triple(
                    Offset(center.x + radius * 0.60f, center.y - radius * 0.26f),
                    shortest * 0.0032f,
                    Color.White.copy(alpha = 0.42f)
                ),
                Triple(
                    Offset(center.x + radius * 0.72f, center.y - radius * 0.18f),
                    shortest * 0.0028f,
                    ringStartColor.copy(alpha = 0.44f)
                ),
                Triple(
                    Offset(center.x - radius * 0.10f, center.y + radius * 0.06f),
                    shortest * 0.0032f,
                    Color.White.copy(alpha = 0.26f)
                ),
                Triple(
                    Offset(center.x + radius * 0.18f, center.y + radius * 0.02f),
                    shortest * 0.0032f,
                    ringEndColor.copy(alpha = 0.22f)
                ),
                Triple(
                    Offset(center.x + radius * 0.28f, center.y + radius * 0.16f),
                    shortest * 0.0038f,
                    Color.White.copy(alpha = 0.30f)
                ),
                Triple(
                    Offset(center.x - radius * 0.28f, center.y + radius * 0.18f),
                    shortest * 0.0038f,
                    Color.White.copy(alpha = 0.30f)
                ),
                Triple(
                    Offset(center.x - radius * 0.38f, center.y + radius * 0.34f),
                    shortest * 0.0046f,
                    Color.White.copy(alpha = 0.44f)
                ),
                Triple(
                    Offset(center.x - radius * 0.22f, center.y + radius * 0.20f),
                    shortest * 0.0038f,
                    ringEndColor.copy(alpha = 0.24f)
                ),
                Triple(
                    Offset(center.x - radius * 0.08f, center.y + radius * 0.42f),
                    shortest * 0.0032f,
                    ringStartColor.copy(alpha = 0.44f)
                ),
                Triple(
                    Offset(center.x + radius * 0.06f, center.y + radius * 0.46f),
                    shortest * 0.0032f,
                    Color.White.copy(alpha = 0.42f)
                ),
                Triple(
                    Offset(center.x + radius * 0.18f, center.y + radius * 0.36f),
                    shortest * 0.0034f,
                    ringEndColor.copy(alpha = 0.40f)
                ),
                Triple(
                    Offset(center.x + radius * 0.30f, center.y + radius * 0.26f),
                    shortest * 0.0032f,
                    ringStartColor.copy(alpha = 0.30f)
                ),
                Triple(
                    Offset(center.x + radius * 0.38f, center.y + radius * 0.44f),
                    shortest * 0.0030f,
                    Color.White.copy(alpha = 0.34f)
                )
            )

            stars.forEach { (pos, r, color) ->
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = color.alpha * 0.22f),
                            Color.Transparent
                        ),
                        center = pos,
                        radius = r * 5.0f
                    ),
                    radius = r * 5.0f,
                    center = pos
                )

                drawCircle(
                    color = color,
                    radius = r,
                    center = pos
                )
            }

            val core = Offset(center.x, center.y + radius * 0.36f)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.82f),
                        ringStartColor.copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = core,
                    radius = radius * 0.13f
                ),
                radius = radius * 0.13f,
                center = core
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.92f),
                        Color.White.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = core,
                    radius = radius * 0.055f
                ),
                radius = radius * 0.055f,
                center = core
            )

            drawCircle(
                color = Color.White.copy(alpha = 0.95f),
                radius = radius * 0.014f,
                center = core
            )
        }

        // =========================
        // ANNEAU PRINCIPAL
        // =========================
        drawCircle(
            brush = ringGradient,
            radius = radius,
            center = center,
            style = Stroke(
                width = ringWidth,
                cap = StrokeCap.Round
            )
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = radius - 12f,
            center = center,
            style = Stroke(
                width = 1.1f,
                cap = StrokeCap.Round
            )
        )

        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    ringStartColor.copy(alpha = 0.14f),
                    Color.White.copy(alpha = 0.18f),
                    ringEndColor.copy(alpha = 0.14f)
                )
            ),
            start = Offset(center.x - radius * 0.90f, center.y),
            end = Offset(center.x - centerGap, center.y),
            strokeWidth = innerLineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    ringStartColor.copy(alpha = 0.14f),
                    Color.White.copy(alpha = 0.18f),
                    ringEndColor.copy(alpha = 0.14f)
                )
            ),
            start = Offset(center.x + centerGap, center.y),
            end = Offset(center.x + radius * 0.90f, center.y),
            strokeWidth = innerLineWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.18f),
                    ringEndColor.copy(alpha = 0.12f)
                )
            ),
            start = Offset(center.x, center.y + centerGap),
            end = Offset(center.x, center.y + radius * 0.78f),
            strokeWidth = innerLineWidth,
            cap = StrokeCap.Round
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.03f),
                    Color.Transparent
                ),
                center = Offset(
                    x = center.x - radius * 0.22f,
                    y = center.y - radius * 0.24f
                ),
                radius = radius * 0.16f
            ),
            radius = radius * 0.16f,
            center = Offset(
                x = center.x - radius * 0.22f,
                y = center.y - radius * 0.24f
            ),
            blendMode = BlendMode.SrcOver
        )
    }
}