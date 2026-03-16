package com.maxjth.tracememoire.ui.accueil.ecran.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE

@Composable
fun HomeEmpreinteStarCanvas(
    start: Offset?,
    end: Offset?,
    progress: Float,
    alpha: Float,
    modifier: Modifier = Modifier
) {

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {

        if (start != null && end != null && alpha > 0.01f) {

            val control = Offset(
                x = (start.x + end.x) / 2f,
                y = minOf(start.y, end.y) - 180f
            )

            val starPos = quadraticBezierPoint(
                start = start,
                end = end,
                control = control,
                t = progress
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        TURQUOISE.copy(alpha = alpha * 0.20f),
                        MAUVE.copy(alpha = alpha * 0.12f),
                        Color.Transparent
                    ),
                    center = starPos,
                    radius = 44f
                ),
                center = starPos,
                radius = 44f
            )

            if (progress > 0.04f) {

                val tailPos = quadraticBezierPoint(
                    start = start,
                    end = end,
                    control = control,
                    t = (progress - 0.05f).coerceAtLeast(0f)
                )

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            MAUVE.copy(alpha = alpha * 0.28f),
                            TURQUOISE.copy(alpha = alpha * 0.36f)
                        ),
                        start = tailPos,
                        end = starPos
                    ),
                    start = tailPos,
                    end = starPos,
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
            }

            drawStar(
                center = starPos,
                outerRadius = 14f,
                innerRadius = 6.2f,
                color = Color.White.copy(alpha = alpha * 0.96f)
            )

            drawStar(
                center = starPos,
                outerRadius = 10f,
                innerRadius = 4.4f,
                color = TURQUOISE.copy(alpha = alpha * 0.52f)
            )
        }
    }
}

