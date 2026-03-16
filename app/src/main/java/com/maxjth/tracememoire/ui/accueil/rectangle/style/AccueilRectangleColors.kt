package com.maxjth.tracememoire.ui.accueil.rectangle.style

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.maxjth.tracememoire.ui.systeme.cyclecolors.CycleColors
import com.maxjth.tracememoire.ui.theme.BG_DEEP

/**
 * Couleurs officielles du rectangle Accueil
 * (halo externe + bordure + fond interne)
 *
 * IMPORTANT :
 * - aucune taille ici
 * - uniquement les couleurs et brushes
 * - synchronisé avec CycleColors
 */
object AccueilRectangleColors {

    // -----------------------------
    // FOND INTERNE
    // -----------------------------
    val background: Color =
        BG_DEEP.copy(alpha = 0.95f)

    // -----------------------------
    // HALO PAR CYCLE
    // -----------------------------
    fun haloBrush(cycleKey: String?): Brush {

        val (start, end) = cycleColors(cycleKey)

        return Brush.linearGradient(
            colors = listOf(
                start.copy(alpha = 0.48f),
                end.copy(alpha = 0.40f)
            )
        )
    }

    // -----------------------------
    // BORDURE PAR CYCLE
    // -----------------------------
    fun borderBrush(cycleKey: String?): Brush {

        val (start, end) = cycleColors(cycleKey)

        return Brush.linearGradient(
            colors = listOf(
                start.copy(alpha = 0.90f),
                end.copy(alpha = 1.00f)
            )
        )
    }

    // -----------------------------
    // MAPPING CYCLE → COULEUR
    // -----------------------------
    private fun cycleColors(cycleKey: String?): Pair<Color, Color> {

        return when (cycleKey?.trim()?.uppercase()) {

            "MATIN" ->
                CycleColors.MatinStart to CycleColors.MatinEnd

            "JOUR" ->
                CycleColors.JourStart to CycleColors.JourEnd

            "SOIR" ->
                CycleColors.SoirStart to CycleColors.SoirEnd

            "NUIT" ->
                CycleColors.NuitStart to CycleColors.NuitEnd

            else ->
                CycleColors.JourStart to CycleColors.JourEnd
        }
    }
}