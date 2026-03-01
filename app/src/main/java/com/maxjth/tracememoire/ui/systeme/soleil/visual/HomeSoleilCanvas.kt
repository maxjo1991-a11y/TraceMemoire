// FILE: app/src/main/java/com/maxjth/tracememoire/ui/soleil/visual/HomeSoleilCanvas.kt
package com.maxjth.tracememoire.ui.systeme.soleil.visual

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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

    // ✅ AJOUT : permet SECONDARY clean (sans pupille/iris/stars)
    isSecondary: Boolean = false
) {
    val pct = displayPct.coerceIn(0, 100)

    /** ✅ SIGNATURE VISUELLE */
    val ringGapDeg = 2f
    val startBaseAngle = -80f + (ringGapDeg / 2f)
    val sweepMax = 360f - ringGapDeg

    // ✅ FIX BUG IMPORTANT : pct/50f faisait dépasser 360° (100% = 2 tours)
    val sweepAngle = (pct / 100f) * sweepMax

    Box(modifier = modifier.size(sizeDp)) {
        Canvas(modifier = Modifier.matchParentSize()) {

            // ✅ Rayon utile : on garde ça pour éviter que le glow déborde trop
            val r = (size.minDimension / 2f) * 0.85f
            val strokePx = strokeDp.toPx()

            // =========================
            // ✅ MODE SECONDARY (clean)
            // Juste anneau + progression.
            // Pas d’iris / pas de pupille / pas de stars (donc pas “pitié”, pas “oeil”)
            // =========================
            if (isSecondary) {

                // Mini halos très discrets (optionnel, mais ça garde le “premium”)
                val haloOuter = 1.02f
                val haloMid = 0.98f

                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.00f to Color.Transparent,
                            0.88f to Color.Transparent,
                            0.95f to haloColorMain.copy(alpha = 0.12f),
                            1.00f to Color.Transparent
                        ),
                        center = center,
                        radius = r * haloOuter
                    ),
                    radius = r * haloOuter,
                    center = center
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.00f to Color.Transparent,
                            0.86f to Color.Transparent,
                            0.94f to haloColorSoft.copy(alpha = 0.06f),
                            1.00f to Color.Transparent
                        ),
                        center = center,
                        radius = r * haloMid
                    ),
                    radius = r * haloMid,
                    center = center
                )

                // Ring base (tour complet)
                drawArc(
                    color = ringBase,
                    startAngle = startBaseAngle,
                    sweepAngle = sweepMax,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                // Progression
                if (pct > 0) {
                    drawArc(
                        color = neonGlowColor.copy(alpha = 0.18f),
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

                return@Canvas
            }

            // =========================
            // ✅ MODE PRIMARY (complet)
            // (si tu l’utilises plus tard)
            // =========================

            // Halos (plus clean, moins "wash")
            val haloOuter = 1.02f
            val haloMid = 0.98f

            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to Color.Transparent,
                        0.86f to Color.Transparent,
                        0.94f to haloColorMain.copy(alpha = 0.78f),
                        1.00f to Color.Transparent
                    ),
                    center = center,
                    radius = r * haloOuter
                ),
                radius = r * haloOuter,
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
                    radius = r * haloMid
                ),
                radius = r * haloMid,
                center = center
            )

            /** IRIS */
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

            // Vignette iris
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

            /** STARS */
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

            /** PUPIL */
            val pupilR = r * 0.25f
            drawCircle(
                color = pupilDeep.copy(alpha = 0.950f),
                radius = pupilR,
                center = center
            )

            // Micro highlight
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

            /** RING BASE */
            drawArc(
                color = ringBase,
                startAngle = startBaseAngle,
                sweepAngle = sweepMax,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            /** PROGRESSION */
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