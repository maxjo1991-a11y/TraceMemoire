// BLOC 1 — FICHIER COMPLET (CORRIGÉ)
// FILE: app/src/main/java/com/maxjth/tracememoire/ui/squares/TraceSquareSystem.kt
package com.maxjth.tracememoire.ui.squares

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

// ────────────────────────────────────────────────
// Identité : "LES carrés" = signature de profondeur
// ────────────────────────────────────────────────

private enum class SquareCornerStyle { SOFT, SHARP }
private enum class SquarePattern { SOLID, DOTS, LINES }
private enum class SquareColorMode { TURQUOISE_ONLY, MAUVE_ONLY, DAILY_MIX }

private data class SquarePersona(
    val cornerStyle: SquareCornerStyle,
    val pattern: SquarePattern,
    val colorMode: SquareColorMode,

    val breatheMs: Int,
    val breatheAmp: Float,    // scale amplitude
    val alphaBase: Float,
    val alphaAmp: Float,

    val strokeBase: Float,    // stroke ratio (relative to min dimension)
    val strokeAmp: Float,

    val wobbleAmpDp: Float,
    val wobbleMs: Int,

    val blinkMs: Int
)

private fun stableSeed(seedBase: String, sliderKey: String, index: Int): Int {
    val s = "$seedBase|$sliderKey|sq|$index"
    return s.fold(0) { acc, c -> acc * 31 + c.code }
}

private fun buildPersona(seed: Int): SquarePersona {
    val r = Random(seed)

    val cornerStyle = if (r.nextFloat() < 0.55f) SquareCornerStyle.SOFT else SquareCornerStyle.SHARP

    val pattern = when (r.nextInt(0, 3)) {
        0 -> SquarePattern.SOLID
        1 -> SquarePattern.DOTS
        else -> SquarePattern.LINES
    }

    val colorMode = when (r.nextInt(0, 3)) {
        0 -> SquareColorMode.TURQUOISE_ONLY
        1 -> SquareColorMode.MAUVE_ONLY
        else -> SquareColorMode.DAILY_MIX
    }

    return SquarePersona(
        cornerStyle = cornerStyle,
        pattern = pattern,
        colorMode = colorMode,

        breatheMs = r.nextInt(2400, 5200),
        breatheAmp = r.nextFloat() * 0.05f + 0.03f, // 0.03..0.08
        alphaBase = r.nextFloat() * 0.16f + 0.18f,  // 0.18..0.34
        alphaAmp = r.nextFloat() * 0.18f + 0.10f,   // 0.10..0.28

        strokeBase = r.nextFloat() * 0.018f + 0.020f, // 0.020..0.038
        strokeAmp = r.nextFloat() * 0.014f + 0.010f,  // 0.010..0.024

        wobbleAmpDp = r.nextFloat() * 2.4f + 0.3f,  // 0.3..2.7 dp
        wobbleMs = r.nextInt(1700, 4200),

        blinkMs = r.nextInt(320, 560)
    )
}

/**
 * ✅ À UTILISER DANS TraceMoodSliderRow.kt
 */
@Composable
fun TraceSquaresRow(
    sliderKey: String,
    seedBase: String,
    pct: Int,
    sliderValue: Float,
    blink: Boolean = false,
    count: Int = 4,
    size: Dp = 14.dp,
    gap: Dp = 8.dp,
    followAmpY: Dp = 6.dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(gap)
    ) {
        repeat(count.coerceIn(1, 12)) { i ->
            TraceLivingSquare(
                sliderKey = sliderKey,
                seedBase = seedBase,
                pct = pct,
                sliderValue = sliderValue,
                index = i,
                blink = blink,
                size = size,
                followAmpY = followAmpY
            )
        }
    }
}

@Composable
private fun TraceLivingSquare(
    sliderKey: String,
    seedBase: String,
    pct: Int,
    sliderValue: Float,
    index: Int,
    blink: Boolean,
    size: Dp,
    followAmpY: Dp
) {
    val persona = remember(seedBase, sliderKey, index) {
        buildPersona(stableSeed(seedBase, sliderKey, index))
    }

    val infinite = rememberInfiniteTransition(label = "sqAlive_${sliderKey}_$index")

    val breathe by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(persona.breatheMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sqBreathe_${sliderKey}_$index"
    )

    val wobble by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(persona.wobbleMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sqWobble_${sliderKey}_$index"
    )

    val blinkPhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(persona.blinkMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sqBlink_${sliderKey}_$index"
    )

    val pct01 = (pct.coerceIn(0, 100) / 100f)
    val dayMix = (0.20f + 0.80f * pct01)

    val baseColor: Color = remember(pct, persona.colorMode) {
        when (persona.colorMode) {
            SquareColorMode.TURQUOISE_ONLY -> TURQUOISE
            SquareColorMode.MAUVE_ONLY -> MAUVE
            SquareColorMode.DAILY_MIX -> lerp(TURQUOISE, MAUVE, dayMix)
        }
    }

    val alpha = (persona.alphaBase + persona.alphaAmp * breathe).coerceIn(0.06f, 0.85f)

    val blinkFactor = if (blink) (0.18f + 0.82f * (1f - blinkPhase)) else 1f
    val finalAlpha = (alpha * blinkFactor).coerceIn(0.02f, 0.92f)

    val w = sin(wobble * 2f * PI).toFloat()
    val wobbleY = (w * persona.wobbleAmpDp).dp

    val followY = ((sliderValue / 100f).coerceIn(0f, 1f) - 0.5f) * 2f // -1..1
    val followDp = (followY * followAmpY.value).dp

    Canvas(
        modifier = Modifier
            .size(size)                 // ✅ AU LIEU DE width()/height()
            .offset(y = wobbleY + followDp)
    ) {
        // ✅ ici "size" = DrawScope.size (Size en pixels)
        val wPx = this.size.minDimension

        val pad = wPx * 0.12f
        val boxSize = Size(width = wPx - pad * 2f, height = wPx - pad * 2f)
        val topLeft = Offset(x = pad, y = pad)

        val cr = when (persona.cornerStyle) {
            SquareCornerStyle.SOFT -> wPx * 0.28f
            SquareCornerStyle.SHARP -> wPx * 0.08f
        }

        val strokeW = ((persona.strokeBase + persona.strokeAmp * breathe) * wPx)
            .coerceIn(1.2f, 5.6f)

        val stroke = Stroke(width = strokeW)
        val c = baseColor.copy(alpha = finalAlpha)

        when (persona.pattern) {
            SquarePattern.SOLID -> {
                drawRoundRect(
                    color = c,
                    topLeft = topLeft,
                    size = boxSize,
                    cornerRadius = CornerRadius(cr, cr)
                )
                drawRoundRect(
                    color = baseColor.copy(alpha = (finalAlpha * 0.85f).coerceIn(0.02f, 0.85f)),
                    topLeft = topLeft,
                    size = boxSize,
                    cornerRadius = CornerRadius(cr, cr),
                    style = stroke
                )
            }

            SquarePattern.DOTS -> {
                drawRoundRect(
                    color = baseColor.copy(alpha = (finalAlpha * 0.80f).coerceIn(0.02f, 0.85f)),
                    topLeft = topLeft,
                    size = boxSize,
                    cornerRadius = CornerRadius(cr, cr),
                    style = stroke
                )

                val dots = 4
                for (dx in 1..dots) {
                    for (dy in 1..dots) {
                        val px = topLeft.x + boxSize.width * (dx / (dots + 1f))
                        val py = topLeft.y + boxSize.height * (dy / (dots + 1f))
                        drawCircle(
                            color = baseColor.copy(alpha = (finalAlpha * 0.55f).coerceIn(0.02f, 0.75f)),
                            radius = wPx * 0.035f,
                            center = Offset(px, py)
                        )
                    }
                }
            }

            SquarePattern.LINES -> {
                drawRoundRect(
                    color = baseColor.copy(alpha = (finalAlpha * 0.80f).coerceIn(0.02f, 0.85f)),
                    topLeft = topLeft,
                    size = boxSize,
                    cornerRadius = CornerRadius(cr, cr),
                    style = stroke
                )

                val lines = 3
                for (i in 1..lines) {
                    val y = topLeft.y + boxSize.height * (i / (lines + 1f))
                    drawLine(
                        color = baseColor.copy(alpha = (finalAlpha * 0.55f).coerceIn(0.02f, 0.75f)),
                        start = Offset(topLeft.x + wPx * 0.06f, y),
                        end = Offset(topLeft.x + boxSize.width - wPx * 0.06f, y),
                        strokeWidth = (strokeW * 0.55f).coerceIn(0.9f, 3.4f)
                    )
                }
            }
        }

        val haloAlpha = (0.06f + 0.08f * breathe) * finalAlpha
        drawRoundRect(
            color = baseColor.copy(alpha = haloAlpha.coerceIn(0.01f, 0.22f)),
            topLeft = topLeft,
            size = boxSize,
            cornerRadius = CornerRadius(cr, cr),
            style = Stroke(width = (strokeW * 0.55f).coerceIn(0.8f, 3.0f))
        )
    }
}