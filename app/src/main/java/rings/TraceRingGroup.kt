package rings

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing

/**
 * Spécifications d’animation de l’anneau.
 * Simple, stable, compilable.
 */
data class RingBreathSpec(
    val breatheMs: Int = 2200,                 // respiration (aller/retour)
    val breatheEasing: Easing = FastOutSlowInEasing,
    val wobbleMs: Int = 9000,                  // micro wobble (boucle)
    val baseAlpha: Float = 0.34f,              // alpha de base
    val ampAlpha: Float = 0.22f,               // amplitude alpha
    val ampScale: Float = 0.028f,              // amplitude scale
    val wobbleAmp: Float = 0.015f,             // micro variation rayon
    val strokeRatioBase: Float = 0.014f,       // ratio épaisseur (diamètre * ratio)
    val strokeRatioAmp: Float = 0.006f         // +/-
)