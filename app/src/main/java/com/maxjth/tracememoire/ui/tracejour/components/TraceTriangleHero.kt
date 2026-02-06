@file:OptIn(ExperimentalLayoutApi::class)

package com.maxjth.tracememoire.ui.tracejour.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlin.math.sin
import androidx.compose.material3.Text


// ─────────────────────────────────────────────
// Couleurs CalmTrace (socle)
// ─────────────────────────────────────────────
private val TURQUOISE = Color(0xFF00A3A3)
private val MAUVE     = Color(0xFF7A63C6)
private val WHITE_SOFT = Color(0xFFE9E9E9)

// ─────────────────────────────────────────────
// HERO TRIANGLE — version officielle écran 2
// ─────────────────────────────────────────────
@Composable
fun TraceTriangleHero(
    percent: Int,
    modifier: Modifier = Modifier,
    isInteracting: Boolean = false
) {
    val pct = percent.coerceIn(0, 100)
    val t = pct / 100f

    // 🎨 Couleur dynamique (turquoise → mauve)
    val baseColor = lerpColor(TURQUOISE, MAUVE, t)
    val triColor  = baseColor
        .desaturate(0.22f)
        .brighten(0.10f)

    // 🫁 Respiration
    val duration = lerpFloat(8200f, 4200f, t).roundToInt()
    val infinite = rememberInfiniteTransition(label = "triangle-breath")

    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val wave = ((sin(phase.toDouble()) + 1.0) / 2.0).toFloat()

    val stroke = lerpFloat(14f, 22f, t) + wave * lerpFloat(2f, 6f, t)
    val glow   = lerpFloat(0.10f, 0.35f, t) * (0.6f + 0.4f * wave)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            val sizeMin = size.minDimension
            val scale = lerpFloat(0.82f, 1.0f, t)

            val w = sizeMin * scale
            val h = sizeMin * scale

            val left = (size.width - w) / 2f
            val top  = (size.height - h) / 2f

            val path = Path().apply {
                moveTo(left + w / 2f, top)
                lineTo(left + w, top + h)
                lineTo(left, top + h)
                close()
            }

            // Glow large
            drawPath(
                path = path,
                color = triColor.copy(alpha = glow),
                style = Stroke(width = stroke + 18f)
            )

            // Glow interne
            drawPath(
                path = path,
                color = triColor.copy(alpha = glow * 0.75f),
                style = Stroke(width = stroke + 8f)
            )

            // Trait principal
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(
                        triColor,
                        Color.White.copy(alpha = 0.25f),
                        triColor
                    )
                ),
                style = Stroke(width = stroke)
            )
        }

        // 💯 Pourcentage central
        Text(
            text = "$pct%",
            fontSize = if (pct >= 100) 54.sp else 62.sp,
            fontWeight = FontWeight.ExtraBold,
            color = WHITE_SOFT,
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(y = 24.dp)
        )
    }
}

// ─────────────────────────────────────────────
// Utils couleur
// ─────────────────────────────────────────────
private fun lerpColor(a: Color, b: Color, t: Float): Color {
    val tt = t.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * tt,
        green = a.green + (b.green - a.green) * tt,
        blue = a.blue + (b.blue - a.blue) * tt,
        alpha = 1f
    )
}

private fun lerpFloat(a: Float, b: Float, t: Float): Float =
    a + (b - a) * t.coerceIn(0f, 1f)

private fun Color.desaturate(amount: Float): Color {
    val gray = red * 0.2126f + green * 0.7152f + blue * 0.0722f
    fun mix(c: Float) = c + (gray - c) * amount
    return Color(mix(red), mix(green), mix(blue), alpha)
}

private fun Color.brighten(amount: Float): Color {
    fun up(c: Float) = (c + (1f - c) * amount).coerceIn(0f, 1f)
    return Color(up(red), up(green), up(blue), alpha)
}

