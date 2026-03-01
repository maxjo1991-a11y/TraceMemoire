// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/modele/AccueilUiState.kt
package com.maxjth.tracememoire.ui.accueil.modele

/**
 * AccueilUiState = état UI "pur" pour Home/Accueil.
 * - Pas de Context
 * - Pas de prefs
 * - Pas de logique métier
 * Juste ce que l’écran doit afficher.
 */
data class AccueilUiState(
    // Texte
    val titre: String = "Trace\nMémoire",
    val sousTitre: String = "",

    // LUNE (compteurs)
    val luneCount: Int = 0,
    val luneDeltaToday: Int = 0,

    // SOLEIL (vivant)
    val soleilPercent: Int? = null,

    // TERRE (annuel)
    val yearlyPercent: Int? = null,

    // RECTANGLE (snapshot par cycle)
    val pMatin: Int? = null,
    val pJour: Int? = null,
    val pSoir: Int? = null,
    val pNuit: Int? = null,

    // Dots FREE (4 pastilles) par cycle
    // Toujours taille = 4
    val dotsMatin: List<Boolean> = listOf(false, false, false, false),
    val dotsJour: List<Boolean> = listOf(false, false, false, false),
    val dotsSoir: List<Boolean> = listOf(false, false, false, false),
    val dotsNuit: List<Boolean> = listOf(false, false, false, false),
)