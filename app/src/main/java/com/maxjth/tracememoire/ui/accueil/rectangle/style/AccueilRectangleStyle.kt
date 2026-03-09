// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/rectangle/style/AccueilRectangleSizes.kt
package com.maxjth.tracememoire.ui.accueil.rectangle.style

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Tailles officielles du rectangle Accueil
 * (halo + bordure + padding + hauteurs).
 *
 * IMPORTANT :
 * - aucune couleur ici
 * - uniquement tailles et distances
 */
object AccueilRectangleSizes {

    // -----------------------------
    // WRAPPER GLOBAL
    // -----------------------------
    // espace autour du rectangle dans l'écran Home
    val wrapperPaddingVertical: Dp = 10.dp

    // hauteur minimum globale du wrapper (utilisé dans AccueilRectangleBloc)
    val wrapperMinHeight: Dp = 440.dp


    // -----------------------------
    // HALO (GLOW EXTERNE)
    // -----------------------------
    // espace pour laisser respirer le glow
    val haloPadding: Dp = 14.dp

    // rayon du halo (légèrement plus grand que le rectangle)
    val haloCorner: Dp = 47.dp

    // puissance du glow
    val haloBlur: Dp = 44.dp


    // -----------------------------
    // RECTANGLE PRINCIPAL
    // -----------------------------
    val corner: Dp = 30.dp

    // bordure visible premium
    val borderWidth: Dp = 1.dp

    // padding interne très léger
    val innerPadding: Dp = 20.dp


    // -----------------------------
    // HAUTEUR DU CONTENU
    // -----------------------------
    // hauteur minimum du rectangle interne
    val contentMinHeight: Dp = 320.dp

    // hauteur max si contenu plus grand
    val contentMaxHeight: Dp = 740.dp
}