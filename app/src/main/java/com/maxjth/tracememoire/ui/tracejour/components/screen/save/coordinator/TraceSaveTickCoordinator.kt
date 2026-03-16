package com.maxjth.tracememoire.ui.tracejour.components.screen.save.coordinator

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.TraceSaveHomeIO
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
    private val luneTick: MutableIntState,
    private val terreTick: MutableIntState,
    private val cyclePercentMapProvider: () -> Map<String, Int>,
    private val lastDeltaToday: MutableState<Int?>,
    private val recomputeTodayValueFromAllCycles: () -> Unit
) {

    private fun safeIncrementLuneTick() {
        val next = luneTick.intValue + 1
        luneTick.intValue = next.coerceIn(0, 999_999)
    }

    fun onHomeTick(
        attachedContext: android.content.Context?,
        attachedSeedBase: String?,
        now: LocalDateTime = LocalDateTime.now()
    ) {
        val context = attachedContext ?: return
        val seedBase = attachedSeedBase ?: return

        val lastProcessedDay = TraceSaveSatellites.readLastProcessedDay(
            context = context,
            seedBase = seedBase
        )

        val newDayDetected =
            lastProcessedDay != now.toLocalDate().toString()

        // 1) archive Soleil -> Terre
        TraceSaveSatellites.archiveYesterdayIfNeeded(
            context = context,
            seedBase = seedBase,
            now = now,
            officialScore = home.officialCurrent.value,
            liveScore = home.lastPercent.value,
            hasCompletedCycle = home.completedCycleMap.values.any { it == true },
            onArchived = {
                terreTick.intValue += 1
            }
        )

        // 2) sync Valeur (minuit)
        valeur.syncAtTick(now)

        // 3) reset écran 2 si nouveau jour
        if (newDayDetected) {
            cycleClearAll()
            stateSetter(TraceSaveState())
            yearlyPercentState.value = 0
            currentLoadedCycleKeySetter(null)

            // IMPORTANT :
            // au nouveau jour, la valeur du jour doit repartir à zéro
            valeur.resetTodayOnly()
        }

        // 4) refresh HOME après le reset
        home.onHomeTick(now)

        // 5) tick Lune
        safeIncrementLuneTick()

        // 6) persist HOME
        homeIO.persistHomeSafe(
            cyclePercentMap = cyclePercentMapProvider(),
            dailyPercentValue = yearlyPercentState.value,
            lastDeltaTodaySetter = { lastDeltaToday.value = it }
        )

        // On ne recalcul plus la valeur ici.
        // La valeur doit être recalculée seulement quand
        // les sliders changent ou quand un cycle est sauvegardé.
    }
}