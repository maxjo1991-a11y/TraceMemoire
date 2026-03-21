package com.maxjth.tracememoire.ui.tracejour.components.screen.save.rebuild

class TraceCycleValueResolver {

    /**
     * Retourne la valeur PURE d’un cycle (ex: JOUR = 50, SOIR = 55)
     * Ne retourne JAMAIS le total du jour.
     */
    fun getCycleValue(
        cycleKey: String,
        cyclePercentMap: Map<String, Int>
    ): Int {
        return cyclePercentMap[cycleKey] ?: 0
    }
}