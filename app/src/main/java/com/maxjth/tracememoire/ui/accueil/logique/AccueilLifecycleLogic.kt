// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/logique/AccueilLogic.kt
package com.maxjth.tracememoire.ui.accueil.logique

import com.maxjth.tracememoire.ui.accueil.modele.AccueilUiState
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore
import java.util.Calendar

/**
 * AccueilLogic = logique "pure" de construction de l'état UI de l'accueil.
 *
 * Règles :
 * - Zéro Compose
 * - Zéro Context
 * - Zéro prefs
 * - On lit seulement les valeurs exposées par saveStore + les paramètres fournis.
 */
object AccueilLogic {

    private const val CYCLE_MATIN = "MATIN"
    private const val CYCLE_JOUR = "JOUR"
    private const val CYCLE_SOIR = "SOIR"
    private const val CYCLE_NUIT = "NUIT"

    fun buildUiState(
        saveStore: TraceSaveStore,
        luneCount: Int,
        luneDeltaToday: Int,
        year: Int = Calendar.getInstance().get(Calendar.YEAR)
    ): AccueilUiState {

        val soleilPercent = saveStore.lastPercent.value?.coerceIn(0, 100)

        val pMatin = saveStore.cyclePercentMap[CYCLE_MATIN]?.coerceIn(0, 100)
        val pJour = saveStore.cyclePercentMap[CYCLE_JOUR]?.coerceIn(0, 100)
        val pSoir = saveStore.cyclePercentMap[CYCLE_SOIR]?.coerceIn(0, 100)
        val pNuit = saveStore.cyclePercentMap[CYCLE_NUIT]?.coerceIn(0, 100)

        // Dots FREE (4 pastilles)
        val dMatin = safeDots(saveStore.getFreeDotsForCycle(CYCLE_MATIN))
        val dJour = safeDots(saveStore.getFreeDotsForCycle(CYCLE_JOUR))
        val dSoir = safeDots(saveStore.getFreeDotsForCycle(CYCLE_SOIR))
        val dNuit = safeDots(saveStore.getFreeDotsForCycle(CYCLE_NUIT))

        return AccueilUiState(
            sousTitre = sousTitrePourAnnee(year),

            luneCount = luneCount,
            luneDeltaToday = luneDeltaToday,

            soleilPercent = soleilPercent,
            yearlyPercent = saveStore.yearlyPercent.value?.coerceIn(0, 100),

            pMatin = pMatin,
            pJour = pJour,
            pSoir = pSoir,
            pNuit = pNuit,

            dotsMatin = dMatin,
            dotsJour = dJour,
            dotsSoir = dSoir,
            dotsNuit = dNuit
        )
    }

    private fun safeDots(raw: List<Boolean>?): List<Boolean> {
        val base = raw ?: emptyList()
        // Force EXACTEMENT 4 items
        return listOf(
            base.getOrNull(0) == true,
            base.getOrNull(1) == true,
            base.getOrNull(2) == true,
            base.getOrNull(3) == true
        )
    }

    private fun sousTitrePourAnnee(year: Int): String {
        return when (year) {
            2026 -> "Le temps n'explique pas. Il mémorise"
            2027 -> "Ce qui revient laisse une trace."
            2028 -> "La mémoire révèle ce qui insistait."
            2029 -> "Ce qui a été noté ne disparaît plus."
            else -> "Le temps fait la mémoire."
        }
    }
}

