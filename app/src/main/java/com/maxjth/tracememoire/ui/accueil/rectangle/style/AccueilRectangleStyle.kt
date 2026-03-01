// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/rectangle/AccueilRectangleSizes.kt
package com.maxjth.tracememoire.ui.accueil.rectangle

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tailles officielles du rectangle Accueil (wrapper + halo + border + paddings + height).
 * Ne pas mettre de couleurs ici.
 */
object AccueilRectangleSizes {

    // Wrapper global autour du rectangle
    val wrapperPaddingVertical: Dp = 8.dp

    // Halo (glow)
    val haloPadding: Dp = 8.dp
    val haloCorner: Dp = 38.dp
    val haloBlur: Dp = 30.dp

    // Contour + fond
    val corner: Dp = 30.dp
    val borderWidth: Dp = 2.8.dp
    val innerPadding: Dp = 2.dp

    // Hauteurs du contenu (HomeTemporalGridRect)
    val contentMinHeight: Dp = 270.dp
    val contentMaxHeight: Dp = 530.dp
}