// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/engine/TraceSaveConfirmationEngine.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save.engine

import androidx.compose.runtime.MutableState
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.TraceSaveHomeIO
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.reset.TraceSaveStoreHome

/**
 * TraceSaveConfirmationEngine
 *
 * Gère la séquence OFFICIELLE :
 * 1) marqué comme renseigné (touched)
 * 2) verrouillage UI
 * 3) dots FREE (Home)
 * 4) recalcul Soleil LIVE
 * 5) commit officiel (prev/current/delta)
 * 6) snapshot rectangle + heure figée (Home)
 * 7) persist moteur (si possible)
 * 8) persist HOME (safe)
 */
class TraceSaveConfirmationEngine(
    private val home: TraceSaveStoreHome,
    private val homeIO: TraceSaveHomeIO,
    private val verrouillerCarte: (String) -> Unit,
    private val recalculerSoleil: (Boolean) -> Unit
) {

    fun confirmerEtVerrouiller(
        cycleKey: String,
        sliderKey: String,
        lockedMap: Map<String, Boolean>,
        touchedMap: MutableMap<String, Boolean>,
        cyclePercentMap: Map<String, Int>,
        yearlyPercentValue: Int?,
        lastDeltaToday: MutableState<Int?>
    ) {
        val cKey = cycleKey.trim().uppercase()
        val sKey = sliderKey.trim()

        if (sKey.isBlank()) return
        if (lockedMap[sKey] == true) return
        if (!home.isValidCycleKey(cKey)) return

        // 0) considéré comme renseigné
        touchedMap[sKey] = true

        // 1) verrouillage UI
        verrouillerCarte(sKey)

        // 2) dots FREE (Home)
        home.onFreeSliderLocked(cKey, sKey)

        // 3) recalcul Soleil LIVE
        recalculerSoleil(true)

        // 4) validation officielle du Soleil (prev/current/delta)
        home.commitOfficialSoleil()
        lastDeltaToday.value = home.officialDelta.value

        // 5) snapshot rectangle + heure figée (Home: createdAtMillis)
        home.onCycleLocked(
            cycleKey = cKey,
            currentSoleil = home.lastPercent.value
        )

        // 6) persist moteur (cycle percent si possible)
        // (cyclePercentMap est une vue; on persiste ce que le Home a maintenant)
        homeIO.persistCyclePercentIfPossible(cKey, home.cyclePercentMap)

        // 7) persist home (safe)
        homeIO.persistHomeSafe(
            cyclePercentMap = home.cyclePercentMap,
            dailyPercentValue = yearlyPercentValue,
            lastDeltaTodaySetter = { lastDeltaToday.value = it }
        )
    }
}