package com.maxjth.tracememoire.ui.tracejour.components.screen.save.coordinator

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import com.maxjth.tracememoire.ui.moteur.cycle.logique.MoteurCycleHome
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.TraceSaveHomeIO
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.date.TraceHomeLogicalDate
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.reset.TraceSaveStoreHome
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.satellites.TraceSaveSatellites
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.state.TraceSaveState
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceValeurStore
import java.time.LocalDateTime

class TraceSaveTickCoordinator(
    private val home: TraceSaveStoreHome,
    private val valeur: TraceValeurStore,
    private val homeIO: TraceSaveHomeIO,
    private val cycleClearAll: () -> Unit,
    private val stateSetter: (TraceSaveState) -> Unit,
    private val yearlyPercentState: MutableState<Int?>,
    private val currentLoadedCycleKeySetter: (String?) -> Unit,
    private val currentLoadedCycleKeyProvider: () -> String?,
    private val luneTick: MutableIntState,
    private val terreTick: MutableIntState,
    private val cyclePercentMapProvider: () -> Map<String, Int>,
    private val lastDeltaToday: MutableState<Int?>,
    private val recomputeTodayValueFromAllCycles: () -> Unit
) {

    private val moteurCycleHome = MoteurCycleHome()

    private fun safeIncrementLuneTick() {
        val next = luneTick.intValue + 1
        luneTick.intValue = next.coerceIn(0, 999_999)
    }

    private fun safeIncrementTerreTick() {
        val next = terreTick.intValue + 1
        terreTick.intValue = next.coerceIn(0, 999_999)
    }

    fun onHomeTick(
        attachedContext: android.content.Context?,
        attachedSeedBase: String?,
        now: LocalDateTime = LocalDateTime.now()
    ) {
        val context = attachedContext ?: return
        val seedBase = attachedSeedBase ?: return

        val logicalToday = TraceHomeLogicalDate.effectiveDate(now).toString()

        val lastProcessedDay = TraceSaveSatellites.readLastProcessedDay(
            context = context,
            seedBase = seedBase
        )

        val newDayDetected = lastProcessedDay != logicalToday

        val currentCycleKey = moteurCycleHome
            .getCurrentCycle(now)
            .name
            .trim()
            .uppercase()

        val loadedCycleKey = currentLoadedCycleKeyProvider()
            ?.trim()
            ?.uppercase()

        val cycleChanged = loadedCycleKey != null && loadedCycleKey != currentCycleKey

        TraceSaveSatellites.archiveYesterdayIfNeeded(
            context = context,
            seedBase = seedBase,
            now = now,
            officialScore = home.officialCurrent.value,
            liveScore = home.lastPercent.value,
            hasCompletedCycle = home.completedCycleMap.values.any { it == true },
            onArchived = {
                safeIncrementTerreTick()
            }
        )

        valeur.syncAtTick(now)

        if (newDayDetected) {
            cycleClearAll()
            stateSetter(TraceSaveState())
            currentLoadedCycleKeySetter(null)
            valeur.resetTodayOnly()
        } else if (cycleChanged) {
            cycleClearAll()
            stateSetter(TraceSaveState(cycleKey = currentCycleKey))
            currentLoadedCycleKeySetter(null)
        }

        home.onHomeTick(now)

        safeIncrementLuneTick()

        homeIO.persistHomeSafe(
            cyclePercentMap = cyclePercentMapProvider(),
            dailyPercentValue = yearlyPercentState.value,
            lastDeltaTodaySetter = { lastDeltaToday.value = it }
        )
    }
}