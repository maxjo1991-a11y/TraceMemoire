// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/store/TraceSaveStoreScore.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save.store

import com.maxjth.tracememoire.ui.moteur.validation.TraceScoreValidator
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.logic.TraceHomeScoreLogic
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.stockage.TraceSaveKeys

internal object TraceSaveStoreScore {

    fun recomputeHomeScoreFromSliders(
        store: TraceSaveStore,
        includePremiumAxes: Boolean = true
    ) {
        val previousHomeScore =
            store.home.lastPercent.value?.coerceIn(0, 100)

        val touchedOnly =
            store.cycle.buildTouchedOnlySliderMap()

        if (touchedOnly.isEmpty()) {
            safeSetLastPercent(store, 0)
            store.yearlyPercent.value = 0

            if (!store.homeIO.isLoadingPrefs()) {
                store.homeIO.persistHomeSafe(
                    cyclePercentMap = store.cyclePercentMap,
                    dailyPercentValue = store.yearlyPercent.value,
                    lastDeltaTodaySetter = { store.lastDeltaToday.value = it }
                )
            }

            val computedDelta = previousHomeScore?.let { 0 - it }

            TraceScoreValidator.validateAll(
                previousJour = previousHomeScore,
                currentJour = 0,
                shownJourDelta = computedDelta,
                previousTraces = null,
                currentTraces = null,
                humeur = null,
                energie = null,
                corps = null,
                presence = null
            )

            store.recomputeTodayValueFromAllCycles()
            return
        }

        val resultPercent =
            TraceHomeScoreLogic.recomputeHomeScoreFromSliders(
                sliderMap = touchedOnly,
                premiumTouchedToday = store.home.premiumTouchedToday.value,
                includePremiumAxes = includePremiumAxes
            )

        val humeur = touchedOnly[TraceSaveKeys.SLIDER_HUMEUR]
        val energie = touchedOnly[TraceSaveKeys.SLIDER_ENERGIE]
        val corps = touchedOnly[TraceSaveKeys.SLIDER_CORPS]
        val presence = touchedOnly[TraceSaveKeys.SLIDER_PRESENCE]

        val computedDelta = if (
            previousHomeScore != null && resultPercent != null
        ) {
            resultPercent - previousHomeScore
        } else {
            null
        }

        TraceScoreValidator.validateAll(
            previousJour = previousHomeScore,
            currentJour = resultPercent,
            shownJourDelta = computedDelta,
            previousTraces = null,
            currentTraces = null,
            humeur = humeur,
            energie = energie,
            corps = corps,
            presence = presence
        )

        safeSetLastPercent(store, resultPercent)

        if (!store.homeIO.isLoadingPrefs()) {
            store.homeIO.persistHomeSafe(
                cyclePercentMap = store.cyclePercentMap,
                dailyPercentValue = store.yearlyPercent.value,
                lastDeltaTodaySetter = { store.lastDeltaToday.value = it }
            )
        }

        store.recomputeTodayValueFromAllCycles()
    }

    private fun safeSetLastPercent(
        store: TraceSaveStore,
        percent: Int?
    ) {
        val safePercent = percent?.coerceIn(0, 100) ?: 0
        store.home.setLastPercent(safePercent)
    }
}