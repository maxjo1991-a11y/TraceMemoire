@file:OptIn(ExperimentalLayoutApi::class)

package com.maxjth.tracememoire.ui.tracejour.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin

// ─────────────────────────────────────────────
// Couleurs CalmTrace (socle)
// ─────────────────────────────────────────────
private val TURQUOISE = Color(0xFF00A3A3)
private val MAUVE = Color(0xFF7A63C6)

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
    val triColor = baseColor
        .desaturate(0.18f)
        .brighten(0.12f)

    // 🔥 Boost glow aux extrêmes (0% et 100%)
    val extreme = (abs(t - 0.5f) * 2f).coerceIn(0f, 1f) // 0 au milieu, 1 aux extrêmes

    // 🫁 Respiration (calme). + rapide si interaction.
    val baseDuration = lerpFloat(8200f, 4200f, t).roundToInt()
    val duration = if (isInteracting) (baseDuration * 0.65f).roundToInt() else baseDuration

    val infinite = rememberInfiniteTransition(label = "triangle-breath")

    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (Math.PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val wave = ((sin(phase.toDouble()) + 1.0) / 2.0).toFloat()

    // ✅ Flottaison douce (plus visible en interaction)
    val floatAmp = if (isInteracting) 10f else 6f
    val floatY = ((wave - 0.5f) * 2f) * floatAmp // -amp..+amp

    // Paramètres trait + glow
    val strokeBase = lerpFloat(14f, 22f, t)
    val strokeWave = wave * lerpFloat(2f, 6f, t)
    val stroke = strokeBase + strokeWave

    // Glow base + respiration + boost extrêmes
    val glowBase = lerpFloat(0.12f, 0.36f, t)
    val glowBreath = (0.62f + 0.38f * wave)
    val glowExtremeBoost = 1f + (0.55f * extreme) // ✅ plus lumineux à 0% / 100%
    val glow = (glowBase * glowBreath * glowExtremeBoost).coerceIn(0f, 0.75f)

    // ✅ Couleur du % : même famille que le triangle (plus claire)
    val pctColor = triColor.brighten(0.28f)

    // Le triangle doit remplir le carré du hero
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .offset(y = floatY.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val sizeMin = size.minDimension

            // IMPORTANT: proche de 1f pour occuper le hero + micro-vie
            val scale = lerpFloat(0.92f, 1.00f, t) * (0.985f + 0.015f * wave)

            val w = sizeMin * scale
            val h = sizeMin * scale

            val left = (size.width - w) / 2f
            val top = (size.height - h) / 2f

            val path = Path().apply {
                moveTo(left + w / 2f, top)
                lineTo(left + w, top + h)
                lineTo(left, top + h)
                close()
            }

            // ✅ Fond intérieur (pas noir absolu) : voile radial discret
            // (donne un “dedans” vivant, différent du cercle)
            drawPath(
                path = path,
                brush = Brush.radialGradient(
                    colors = listOf(
                        triColor.copy(alpha = 0.10f + 0.06f * wave),
                        Color.Black.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = sizeMin * 0.78f
                )
            )

            // Glow large
            drawPath(
                path = path,
                color = triColor.copy(alpha = glow),
                style = Stroke(width = stroke + 20f)
            )

            // Glow interne
            drawPath(
                path = path,
                color = triColor.copy(alpha = glow * 0.78f),
                style = Stroke(width = stroke + 9f)
            )

            // Trait principal (dégradé vivant)
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(
                        triColor.brighten(0.10f),
                        Color.White.copy(alpha = 0.22f + 0.06f * wave),
                        triColor
                    )
                ),
                style = Stroke(width = stroke)
            )
        }

        // 💯 Pourcentage central (même couleur que triangle)
        Text(
            text = "$pct%",
            fontSize = if (pct >= 100) 54.sp else 62.sp,
            fontWeight = FontWeight.ExtraBold,
            color = pctColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(y = 20.dp)
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