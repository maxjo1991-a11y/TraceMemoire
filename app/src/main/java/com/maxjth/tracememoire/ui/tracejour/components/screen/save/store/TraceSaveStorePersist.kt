package com.maxjth.tracememoire.ui.tracejour.components.screen.save.store

import android.content.Context
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.helper.TraceValeurCycleHelper
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.state.TraceSaveState

internal object TraceSaveStorePersist {

    fun loadHomeOnlyForHomeScreen(
        store: TraceSaveStore,
        context: Context,
        seedBase: String
    ) {
        store.attach(context, seedBase)

        store.homeIO.loadHomeOnlyForHomeScreen(
            context = context,
            seedBase = seedBase,
            cyclePercentMap = store.cyclePercentMap,
            dailyPercentSetter = { store.yearlyPercent.value = it },
            lastDeltaTodaySetter = { store.lastDeltaToday.value = it }
        )

        store.valeur.load(context, seedBase)
        store.currentLoadedCycleKey = null
    }

    fun loadFromPrefs(
        store: TraceSaveStore,
        context: Context,
        seedBase: String,
        cycleKey: String,
        sliderKeys: List<String>
    ) {
        val persistedCycleKey = cycleKey.trim()

        store.attach(context, seedBase)
        store.currentLoadedCycleKey = persistedCycleKey

        store.cycle.initDefaults(sliderKeys)

        store.cycleStorage.loadCycle(
            context = context,
            seedBase = seedBase,
            cycleKey = persistedCycleKey,
            sliderKeys = sliderKeys,
            sliderMap = store.sliderMap,
            noteMap = store.noteMap,
            capturedMap = store.capturedMap,
            createdAtMap = store.createdAtMap,
            lockedMap = store.lockedMap,
            touchedMap = store.touchedMap
        )

        store.cycle.applyVisualZeroForUntouched(sliderKeys)
        store.state.value = TraceSaveState(
            cycleKey = persistedCycleKey
        )

        store.homeIO.loadHomeOnlyForHomeScreen(
            context = context,
            seedBase = seedBase,
            cyclePercentMap = store.cyclePercentMap,
            dailyPercentSetter = { store.yearlyPercent.value = it },
            lastDeltaTodaySetter = { store.lastDeltaToday.value = it }
        )

        store.valeur.load(context, seedBase)
    }

    fun persistAllToPrefs(
        store: TraceSaveStore,
        context: Context,
        seedBase: String,
        cycleKey: String
    ) {
        val persistedCycleKey = cycleKey.trim()

        store.currentLoadedCycleKey = persistedCycleKey

        // 1) persist du cycle courant daté
        store.cycleStorage.persistCycle(
            context = context,
            seedBase = seedBase,
            cycleKey = persistedCycleKey,
            sliderMap = store.sliderMap,
            noteMap = store.noteMap,
            capturedMap = store.capturedMap,
            createdAtMap = store.createdAtMap,
            lockedMap = store.lockedMap,
            touchedMap = store.touchedMap
        )

        // 2) persist de la valeur du jour
        store.valeur.persist(context, seedBase)

        // 3) persist HOME
        store.homeIO.persistHomeSafe(
            cyclePercentMap = store.cyclePercentMap,
            dailyPercentValue = store.yearlyPercent.value,
            lastDeltaTodaySetter = { store.lastDeltaToday.value = it }
        )

        // 4) persist du percent figé HOME avec clé normalisée si nécessaire
        val normalizedForHome = TraceValeurCycleHelper.normCycleKey(cycleKey)
            ?: cycleKey.trim().uppercase()

        store.homeIO.persistCyclePercentIfPossible(
            normalizedForHome,
            store.cyclePercentMap
        )
    }
}