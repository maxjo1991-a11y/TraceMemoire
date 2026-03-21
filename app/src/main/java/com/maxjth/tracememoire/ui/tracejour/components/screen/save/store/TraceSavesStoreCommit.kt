package com.maxjth.tracememoire.ui.tracejour.components.screen.save.store

internal object TraceSaveStoreCommit {

    fun handleLockForHomeAndRect(
        store: TraceSaveStore,
        cycleKey: String,
        sliderKey: String
    ) {
        val safeCycleKey = cycleKey.trim().uppercase()

        // 1) verrouillage / commit Home (fige le cycle)
        store.confirmation.confirmerEtVerrouiller(
            cycleKey = safeCycleKey,
            sliderKey = sliderKey,
            lockedMap = store.lockedMap,
            touchedMap = store.touchedMap,
            cyclePercentMap = store.cyclePercentMap,
            yearlyPercentValue = store.yearlyPercent.value,
            lastDeltaToday = store.lastDeltaToday
        )

        val context = store.attachedContext
        val seedBase = store.attachedSeedBase

        if (context != null && seedBase != null) {
            // 2) persistance du cycle courant
            store.persistAllToPrefs(
                context = context,
                seedBase = seedBase,
                cycleKey = safeCycleKey
            )

            // 3) recalcul propre APRÈS persistance
            store.recomputeTodayValueFromAllCycles()

            // 4) on repersiste la valeur recalculée
            store.valeur.persist(context, seedBase)

            // 5) on resynchronise HOME avec la valeur finale
            store.homeIO.persistHomeSafe(
                cyclePercentMap = store.cyclePercentMap,
                dailyPercentValue = store.yearlyPercent.value,
                lastDeltaTodaySetter = { store.lastDeltaToday.value = it }
            )
        }
    }
}