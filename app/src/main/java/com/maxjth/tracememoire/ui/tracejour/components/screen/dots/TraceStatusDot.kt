// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/dots/TraceStatusDot.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.dots

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

enum class TraceDotState {
    ON,
    OFF,
    DISABLED
}

@Composable
fun TraceStatusDot(
    state: TraceDotState,

    // Taille globale du “slot” (incluant le glow)
    glowSize: Dp = 18.dp,

    // Teintes
    colorOn: Color = Color(0xFF00B3B3),       // turquoise actif
    colorOff: Color = Color(0xFF00B3B3),      // même teinte (anneau vide)
    colorDisabled: Color = Color(0xFF777777), // gris

    // Alphas glow (halo externe)
    alphaGlowOn: Float = 0.38f,
    alphaGlowOff: Float = 0.22f,
    alphaGlowDisabled: Float = 0.10f,

    // Présence de l’anneau (OFF)
    offRingAlpha: Float = 0.85f,      // OFF doit être visible
    disabledRingAlpha: Float = 0.40f  // DISABLED plus fade
) {
    Canvas(modifier = Modifier.size(glowSize)) {

        val w = size.width
        val h = size.height
        val rMin = min(w, h)

        val center = Offset(w / 2f, h / 2f)

        val isOn = state == TraceDotState.ON
        val isOff = state == TraceDotState.OFF
        val isDisabled = state == TraceDotState.DISABLED

        val baseColor = when {
            isOn -> colorOn
            isOff -> colorOff
            else -> colorDisabled
        }

        // ✅ Taille du glow (extérieur)
        val outerR = rMin * 0.48f

        // ✅ Taille du “dot” interne
        // OFF = anneau normal
        // DISABLED = un peu plus petit (ça fait premium/lock sans être agressif)
        val dotR = when {
            isDisabled -> outerR * 0.62f
            else -> outerR * 0.72f
        }

        // ✅ Épaisseur de l’anneau (OFF)
        val ringStroke = when {
            isDisabled -> outerR * 0.18f
            else -> outerR * 0.20f
        }

        val glowAlpha = when {
            isOn -> alphaGlowOn
            isOff -> alphaGlowOff
            else -> alphaGlowDisabled
        }

        // =========================
        // 1) Glow externe doux
        // =========================
        drawCircle(
            color = baseColor.copy(alpha = glowAlpha),
            radius = outerR,
            center = center
        )

        // =========================
        // 2) Dot principal
        // =========================
        if (isOn) {
            // ✅ ON = cercle plein + présence nette
            drawCircle(
                color = baseColor,
                radius = dotR,
                center = center
            )
        } else {
            // ✅ OFF/DISABLED = cercle vide mais illuminé (anneau)
            val ringAlpha = if (isDisabled) disabledRingAlpha else offRingAlpha
            drawCircle(
                color = baseColor.copy(alpha = ringAlpha),
                radius = dotR,
                center = center,
                style = Stroke(width = ringStroke)
            )

            // ✅ micro “specular” (très léger) pour donner l’effet verre
            // (ça aide OFF à être visible sans devenir un bouton)
            drawCircle(
                color = Color.White.copy(alpha = if (isDisabled) 0.06f else 0.10f),
                radius = dotR,
                center = center,
                style = Stroke(width = ringStroke * 0.55f)
            )
        }
    }
}