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
        val safeCycleKey = TraceValeurCycleHelper.normCycleKey(cycleKey) ?: cycleKey.trim().uppercase()

        store.attach(context, seedBase)
        store.currentLoadedCycleKey = safeCycleKey

        store.cycle.initDefaults(sliderKeys)

        store.cycleStorage.loadCycle(
            context = context,
            seedBase = seedBase,
            cycleKey = safeCycleKey,
            sliderKeys = sliderKeys,
            sliderMap = store.sliderMap,
            noteMap = store.noteMap,
            capturedMap = store.capturedMap,
            createdAtMap = store.createdAtMap,
            lockedMap = store.lockedMap,
            touchedMap = store.touchedMap
        )

        store.cycle.applyVisualZeroForUntouched(sliderKeys)
        store.state.value = TraceSaveState()

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
        val safeCycleKey = TraceValeurCycleHelper.normCycleKey(cycleKey) ?: cycleKey.trim().uppercase()

        store.currentLoadedCycleKey = safeCycleKey

        store.cycleStorage.persistCycle(
            context = context,
            seedBase = seedBase,
            cycleKey = safeCycleKey,
            sliderMap = store.sliderMap,
            noteMap = store.noteMap,
            capturedMap = store.capturedMap,
            createdAtMap = store.createdAtMap,
            lockedMap = store.lockedMap,
            touchedMap = store.touchedMap
        )

        store.recomputeTodayValueFromAllCycles()
        store.valeur.persist(context, seedBase)

        store.homeIO.persistHomeSafe(
            cyclePercentMap = store.cyclePercentMap,
            dailyPercentValue = store.yearlyPercent.value,
            lastDeltaTodaySetter = { store.lastDeltaToday.value = it }
        )

        store.homeIO.persistCyclePercentIfPossible(safeCycleKey, store.cyclePercentMap)
    }
}