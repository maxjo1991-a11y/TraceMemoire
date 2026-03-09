// FILE: app/src/main/java/com/maxjth/tracememoire/ui/systeme/soleil/visual/HomeSoleilCanvas.kt
package com.maxjth.tracememoire.ui.systeme.soleil.visual

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun HomeSoleilCanvas(
    displayPct: Int,
    strokeDp: Dp,
    ringBase: Color,
    ringBright: Color,
    neonGlowColor: Color,
    haloColorMain: Color,
    haloColorSoft: Color,
    irisDeep: Color,
    irisMauve: Color,
    irisAccent: Color,
    pupilDeep: Color,
    starPoints: List<Triple<Float, Float, Float>>,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 1000.dp,
    isSecondary: Boolean = false
) {
    val pct = displayPct.coerceIn(0, 100)

    val ringGapDeg = 2f
    val startBaseAngle = -80f + (ringGapDeg / 2f)
    val sweepMax = 360f - ringGapDeg
    val sweepAngle = (pct / 100f) * sweepMax

    Box(modifier = modifier.size(sizeDp)) {
        Canvas(modifier = Modifier.matchParentSize()) {

            val r = (size.minDimension / 2f) * 0.85f
            val strokePx = strokeDp.toPx()

            // =========================
            // MODE SECONDARY = Jour
            // =========================
            if (isSecondary) {

                // Fond interne plus propre / plus lumineux que Terre
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            irisDeep.copy(alpha = 0.92f),
                            irisDeep.copy(alpha = 0.72f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = r * 0.82f
                    ),
                    radius = r * 0.82f,
                    center = center
                )

                // Halo externe principal : présence maîtrisée
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.00f to Color.Transparent,
                            0.84f to Color.Transparent,
                            0.93f to haloColorMain.copy(alpha = 0.18f),
                            1.00f to Color.Transparent
                        ),
                        center = center,
                        radius = r * 1.03f
                    ),
                    radius = r * 1.03f,
                    center = center
                )

                // Halo secondaire doux
                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.00f to Color.Transparent,
                            0.82f to Color.Transparent,
                            0.92f to haloColorSoft.copy(alpha = 0.08f),
                            1.00f to Color.Transparent
                        ),
                        center = center,
                        radius = r * 0.98f
                    ),
                    radius = r * 0.98f,
                    center = center
                )

                // Noyau clair / lisible
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            WHITE_SOFT.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(
                            x = center.x - r * 0.10f,
                            y = center.y - r * 0.12f
                        ),
                        radius = r * 0.24f
                    ),
                    radius = r * 0.24f,
                    center = Offset(
                        x = center.x - r * 0.10f,
                        y = center.y - r * 0.12f
                    ),
                    blendMode = BlendMode.SrcOver
                )

                // Anneau de base complet
                drawArc(
                    color = ringBase.copy(alpha = 0.82f),
                    startAngle = startBaseAngle,
                    sweepAngle = sweepMax,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                // Fine lumière extérieure
                drawArc(
                    color = WHITE_SOFT.copy(alpha = 0.05f),
                    startAngle = startBaseAngle,
                    sweepAngle = sweepMax,
                    useCenter = false,
                    style = Stroke(width = strokePx * 1.18f, cap = StrokeCap.Round)
                )

                // Progression
                if (pct > 0) {
                    drawArc(
                        color = neonGlowColor.copy(alpha = 0.16f),
                        startAngle = startBaseAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokePx * 1.12f, cap = StrokeCap.Round)
                    )

                    drawArc(
                        color = ringBright.copy(alpha = 0.96f),
                        startAngle = startBaseAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )

                    // Petit accent de tête d’arc
                    drawArc(
                        color = WHITE_SOFT.copy(alpha = 0.14f),
                        startAngle = startBaseAngle + (sweepAngle - 10f).coerceAtLeast(0f),
                        sweepAngle = 10f.coerceAtMost(sweepAngle),
                        useCenter = false,
                        style = Stroke(width = strokePx * 0.82f, cap = StrokeCap.Round)
                    )
                }

                return@Canvas
            }

            // =========================
            // MODE PRIMARY
            // =========================

            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.86f to Color.Transparent,
                        0.94f to haloColorMain.copy(alpha = 0.78f),
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = r * 1.02f
                ),
                radius = r * 1.02f,
                center = center
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.84f to Color.Transparent,
                        0.93f to haloColorSoft.copy(alpha = 0.11f),
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = r * 0.98f
                ),
                radius = r * 0.98f,
                center = center
            )

            val irisR = r * 0.70f

            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to irisDeep.copy(alpha = 0.97f),
                        0.40f to irisMauve.copy(alpha = 0.70f),
                        0.72f to irisAccent.copy(alpha = 0.12f),
                        1.00f to irisDeep.copy(alpha = 0.66f)
                    ),
                    center = center,
                    radius = irisR
                ),
                radius = irisR,
                center = center
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.62f to Color.Transparent,
                        0.86f to irisDeep.copy(alpha = 0.22f),
                        1.00f to irisDeep.copy(alpha = 0.58f)
                    ),
                    center = center,
                    radius = irisR
                ),
                radius = irisR,
                center = center
            )

            val starsR = irisR * 0.95f
            val starTint = lerp(irisMauve, irisAccent, 0.60f)
            val starCore = lerp(starTint, WHITE_SOFT, 0.19f)

            starPoints.forEach { (nx, ny, a) ->
                val px = center.x + (nx * starsR)
                val py = center.y + (ny * starsR)
                val alpha = (a * 0.20f).coerceIn(0.010f, 0.050f)

                drawCircle(
                    color = starCore.copy(alpha = alpha),
                    radius = (r * 0.0092f).coerceAtLeast(1.05f),
                    center = Offset(px, py)
                )
            }

            val pupilR = r * 0.25f
            drawCircle(
                color = pupilDeep.copy(alpha = 0.950f),
                radius = pupilR,
                center = center
            )

            val hlCenter = Offset(center.x - pupilR * 0.22f, center.y - pupilR * 0.28f)
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to WHITE_SOFT.copy(alpha = 0.20f),
                        0.55f to WHITE_SOFT.copy(alpha = 0.06f),
                        1.00f to Color.Transparent
                    ),
                    center = hlCenter,
                    radius = pupilR * 0.65f
                ),
                radius = pupilR * 0.65f,
                center = hlCenter
            )

            drawArc(
                color = ringBase,
                startAngle = startBaseAngle,
                sweepAngle = sweepMax,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            if (pct > 0) {
                drawArc(
                    color = neonGlowColor.copy(alpha = 0.24f),
                    startAngle = startBaseAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokePx * 0.95f, cap = StrokeCap.Round)
                )

                drawArc(
                    color = ringBright,
                    startAngle = startBaseAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }
    }
}