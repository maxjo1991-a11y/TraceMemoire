package com.maxjth.tracememoire.ui.tracejour.components.screen.save.store

internal object TraceSaveStoreCommit {

    fun handleLockForHomeAndRect(
        store: TraceSaveStore,
        cycleKey: String,
        sliderKey: String
    ) {
        val safeCycleKey = cycleKey.trim().uppercase()

        // 1) verrouillage / commit Home
        store.confirmation.confirmerEtVerrouiller(
            cycleKey = safeCycleKey,
            sliderKey = sliderKey,
            lockedMap = store.lockedMap,
            touchedMap = store.touchedMap,
            cyclePercentMap = store.cyclePercentMap,
            yearlyPercentValue = store.yearlyPercent.value,
            lastDeltaToday = store.lastDeltaToday
        )

        // 2) recalcul immédiat de la valeur journée
        store.recomputeTodayValueFromAllCycles()

        // 3) persistance OFFICIELLE du cycle courant
        val context = store.attachedContext
        val seedBase = store.attachedSeedBase

        if (context != null && seedBase != null) {
            store.persistAllToPrefs(
                context = context,
                seedBase = seedBase,
                cycleKey = safeCycleKey
            )
        }
    }
}