// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/rectangle/style/AccueilRectangleColors.kt
package com.maxjth.tracememoire.ui.accueil.rectangle.style

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE

/**
 * Couleurs officielles du rectangle Accueil
 * (halo externe + bordure + fond interne)
 *
 * IMPORTANT :
 * - aucune taille ici
 * - uniquement les couleurs et brushes
 */
object AccueilRectangleColors {

    // -----------------------------
    // HALO EXTERNE (GLOW)
    // -----------------------------
    // turquoise légèrement dominant
    val haloStart: Color = TURQUOISE.copy(alpha = 0.48f)

    // mauve légèrement plus doux
    val haloEnd: Color = MAUVE.copy(alpha = 0.40f)


    // -----------------------------
    // BORDURE DÉGRADÉE
    // -----------------------------
    val borderStart: Color = TURQUOISE.copy(alpha = 0.90f)
    val borderEnd: Color = MAUVE.copy(alpha = 1.00f)


    // -----------------------------
    // FOND INTERNE
    // -----------------------------
    val background: Color =
        BG_DEEP.copy(alpha = 0.95f)


    // -----------------------------
    // BRUSH HALO
    // -----------------------------
    fun haloBrush(): Brush = Brush.linearGradient(
        colors = listOf(
            haloStart,
            haloEnd
        )
    )


    // -----------------------------
    // BRUSH BORDURE
    // -----------------------------
    fun borderBrush(): Brush = Brush.linearGradient(
        colors = listOf(
            borderStart,
            borderEnd
        )
    )
}