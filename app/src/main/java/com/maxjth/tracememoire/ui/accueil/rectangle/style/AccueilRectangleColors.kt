// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/rectangle/AccueilRectangleColors.kt
package com.maxjth.tracememoire.ui.accueil.rectangle

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE

/**
 * Couleurs officielles du rectangle Accueil (wrapper + halo + bordure + fond).
 * Ne pas mettre de tailles ici.
 */
object AccueilRectangleColors {

    // Halo externe (glow doux)
    val haloStart: Color = TURQUOISE.copy(alpha = 0.42f)
    val haloEnd: Color = MAUVE.copy(alpha = 0.42f)

    // Bordure dégradée (contour)
    val borderStart: Color = TURQUOISE
    val borderEnd: Color = MAUVE

    // Fond interne (wrapper)
    val background: Color = BG_DEEP.copy(alpha = 0.95f)

    // ---------- Brushes ----------
    fun haloBrush(): Brush = Brush.linearGradient(
        colors = listOf(haloStart, haloEnd)
    )

    fun borderBrush(): Brush = Brush.linearGradient(
        colors = listOf(borderStart, borderEnd)
    )
}