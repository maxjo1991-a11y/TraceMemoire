package com.maxjth.tracememoire.ui.tracejour.components.screen.save.logic

object TraceValeurLogic {

    // clés sliders principaux
    private const val HUMEUR = "humeur"
    private const val ENERGIE = "energie"
    private const val CORPS = "corps"
    private const val PRESENCE = "presence"

    private const val MAX_CYCLE_VALUE = 400
    private const val MAX_DAY_VALUE = 1600

    /**
     * Calcule la valeur d’un cycle.
     * Chaque axe vaut 0 → 100.
     * Donc un cycle peut valoir 0 → 400.
     */
    fun computeCycleValue(sliderMap: Map<String, Int>): Int {
        val humeur = sliderMap[HUMEUR]?.coerceIn(0, 100) ?: 0
        val energie = sliderMap[ENERGIE]?.coerceIn(0, 100) ?: 0
        val corps = sliderMap[CORPS]?.coerceIn(0, 100) ?: 0
        val presence = sliderMap[PRESENCE]?.coerceIn(0, 100) ?: 0

        val total = humeur + energie + corps + presence

        return total.coerceIn(0, MAX_CYCLE_VALUE)
    }

    /**
     * Calcule la valeur totale du jour
     * à partir des 4 cycles :
     * NUIT / MATIN / JOUR / SOIR
     *
     * Donc total journalier = 0 → 1600
     */
    fun computeDayValue(cycleValues: List<Int>): Int {
        val total = cycleValues.sum()
        return total.coerceIn(0, MAX_DAY_VALUE)
    }

    /**
     * Sécurise une valeur journalière
     */
    fun clampDayValue(value: Int?): Int {
        return value?.coerceIn(0, MAX_DAY_VALUE) ?: 0
    }
}