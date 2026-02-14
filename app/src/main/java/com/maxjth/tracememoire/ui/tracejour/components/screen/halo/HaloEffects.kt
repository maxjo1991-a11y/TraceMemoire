


package com.maxjth.tracememoire.ui.tracejour.components.screen.halo

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

/**
 * SoftHalo — matière "verre / brume" + halo doux, enveloppant.
 * Objectif : un fond premium distinct (turquoise) sans casser l’ADN calme.
 *
 * Utilisation simple :
 * SoftHaloCard(isPremium = true) { ...contenu... }
 *
 * Ou en Modifier :
 * Modifier.softHaloBackground(isPremium = false, radius = 22.dp)
 */

@Composable
fun SoftHaloCard(
    modifier: Modifier = Modifier,
    radius: Dp = 22.dp,
    padding: Dp = 18.dp,
    isPremium: Boolean = false,
    enabled: Boolean = true,
    // Couleurs (tu peux les relier à tes couleurs de thème si tu veux)
    baseBg: Color = Color(0xFF0A0A0A),
    glassTint: Color = Color.White,
    haloMauve: Color = Color(0xFF8E6BFF),      // mauve calme
    haloTurquoise: Color = Color(0xFF00B3B3),  // turquoise calmtrace
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)

    val infinite = rememberInfiniteTransition(label = "softHalo")
    val t by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "softHaloPulse"
    )

    val worldColor = if (isPremium) haloTurquoise else haloMauve

    // Intensités (calmes)
    val alphaBase = if (enabled) 1f else 0.55f
    val haloA = (0.10f + 0.06f * t) * alphaBase
    val haloB = (0.06f + 0.05f * (1f - t)) * alphaBase
    val glassA = (0.10f + 0.04f * t) * alphaBase

    val borderColor = if (isPremium) {
        haloTurquoise.copy(alpha = (0.38f + 0.10f * t) * alphaBase)
    } else {
        haloMauve.copy(alpha = (0.22f + 0.06f * t) * alphaBase)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .softHaloBackground(
                radius = radius,
                isPremium = isPremium,
                enabled = enabled,
                baseBg = baseBg,
                glassTint = glassTint,
                worldColor = worldColor,
                haloA = haloA,
                haloB = haloB,
                glassA = glassA
            )
            .border(width = 1.dp, color = borderColor, shape = shape)
            .padding(padding),
        content = content
    )
}

fun Modifier.softHaloBackground(
    radius: Dp = 22.dp,
    isPremium: Boolean = false,
    enabled: Boolean = true,
    baseBg: Color = Color(0xFF0A0A0A),
    glassTint: Color = Color.White,
    worldColor: Color = if (isPremium) Color(0xFF00B3B3) else Color(0xFF8E6BFF),
    haloA: Float = 0.12f,
    haloB: Float = 0.08f,
    glassA: Float = 0.12f
): Modifier {
    val shape = RoundedCornerShape(radius)

    return this
        .clip(shape)
        .background(baseBg.copy(alpha = 0.92f))
        .drawBehind {
            val w = size.width
            val h = size.height
            val r = radius.toPx().coerceAtMost(min(w, h) / 2f)
            val cr = CornerRadius(r, r)

            // 1) HALO (enveloppe)
            // Doux, périphérique, pas agressif.
            val haloBrush = Brush.radialGradient(
                colors = listOf(
                    worldColor.copy(alpha = 0f),
                    worldColor.copy(alpha = haloA),
                    worldColor.copy(alpha = haloB),
                    worldColor.copy(alpha = 0f)
                ),
                center = Offset(w * 0.50f, h * 0.45f),
                radius = min(w, h) * 0.85f
            )
            drawRoundRect(
                brush = haloBrush,
                topLeft = Offset.Zero,
                size = Size(w, h),
                cornerRadius = cr
            )

            // 2) BRUME (matière)
            // On simule une brume "verre dépoli" via 2 gradients superposés.
            val fog1 = Brush.linearGradient(
                colors = listOf(
                    glassTint.copy(alpha = 0.00f),
                    glassTint.copy(alpha = 0.06f),
                    glassTint.copy(alpha = 0.00f)
                ),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )
            drawRoundRect(
                brush = fog1,
                topLeft = Offset.Zero,
                size = Size(w, h),
                cornerRadius = cr
            )

            val fog2 = Brush.linearGradient(
                colors = listOf(
                    glassTint.copy(alpha = 0.05f),
                    glassTint.copy(alpha = 0.00f),
                    glassTint.copy(alpha = 0.07f)
                ),
                start = Offset(w, 0f),
                end = Offset(0f, h)
            )
            drawRoundRect(
                brush = fog2,
                topLeft = Offset.Zero,
                size = Size(w, h),
                cornerRadius = cr
            )

            // 3) VERRE (highlight doux)
            // Un reflet haut + un reflet bas, très subtil.
            val glassTop = Brush.verticalGradient(
                colors = listOf(
                    glassTint.copy(alpha = glassA),
                    glassTint.copy(alpha = 0.02f),
                    glassTint.copy(alpha = 0.00f)
                ),
                startY = 0f,
                endY = h * 0.55f
            )
            drawRoundRect(
                brush = glassTop,
                topLeft = Offset(0f, 0f),
                size = Size(w, h),
                cornerRadius = cr
            )

            val glassBottom = Brush.verticalGradient(
                colors = listOf(
                    glassTint.copy(alpha = 0.00f),
                    glassTint.copy(alpha = 0.02f),
                    glassTint.copy(alpha = (0.07f * if (enabled) 1f else 0.55f))
                ),
                startY = h * 0.35f,
                endY = h
            )
            drawRoundRect(
                brush = glassBottom,
                topLeft = Offset(0f, 0f),
                size = Size(w, h),
                cornerRadius = cr
            )
        }
}