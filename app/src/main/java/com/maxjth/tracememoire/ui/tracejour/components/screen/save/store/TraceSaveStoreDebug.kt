// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/store/TraceSaveStoreDebug.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save.store

import android.content.Context
import com.maxjth.tracememoire.ui.moteur.cycle.grille.GrilleCycleHome
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.helper.TraceValeurCycleHelper
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.prefs.TraceJourPrefs
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.satellites.TraceSaveSatellites
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.state.TraceSaveState
import java.time.LocalDateTime

/**
 * Bloc debug du TraceSaveStore :
 * - simulation de minuit
 * - reset complet d’un seed + reload
 */
internal object TraceSaveStoreDebug {

    fun debugForceMidnight(
        store: TraceSaveStore,
        context: Context,
        seedBase: String
    ) {
        store.attach(context, seedBase)

        // 1) s’assurer que la valeur du jour courant est bien calculée et persistée
        store.recomputeTodayValueFromAllCycles()
        store.valeur.persist(context, seedBase)

        // 2) simuler le nouveau jour AVANT transfert
        val simulatedNow = LocalDateTime.now()
            .plusDays(1)
            .withHour(0)
            .withMinute(1)
            .withSecond(0)
            .withNano(0)

        // 3) FORCER le transfert Soleil -> Terre AVANT le reset minuit
        // IMPORTANT : on passe la date simulée pour aligner lastSeenDay
        store.valeur.debugForceTransferToYesterday(simulatedNow)
        store.terreTick.intValue += 1

        // 4) lancer le tick simulé
        store.tickCoordinator.onHomeTick(
            attachedContext = store.attachedContext,
            attachedSeedBase = store.attachedSeedBase,
            now = simulatedNow
        )

        // 5) recharger l’écran 2 sur le cycle simulé (NUIT)
        val simulatedHomeCycle = GrilleCycleHome.resolve(simulatedNow)
        val newCycleKey = TraceValeurCycleHelper.normCycleKey(
            simulatedHomeCycle.storageKey()
        ) ?: "NUIT"

        store.currentLoadedCycleKey = newCycleKey

        val allSliderKeys = listOf(
            "humeur", "energie", "corps", "presence",
            "repos", "archi_emo", "type_journee", "motifs_psy", "environnement", "clarte"
        )

        store.cycle.initDefaults(allSliderKeys)

        store.cycleStorage.loadCycle(
            context = context,
            seedBase = seedBase,
            cycleKey = newCycleKey,
            sliderKeys = allSliderKeys,
            sliderMap = store.sliderMap,
            noteMap = store.noteMap,
            capturedMap = store.capturedMap,
            createdAtMap = store.createdAtMap,
            lockedMap = store.lockedMap,
            touchedMap = store.touchedMap
        )

        store.cycle.applyVisualZeroForUntouched(allSliderKeys)
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

    fun debugClearSeedAndReload(
        store: TraceSaveStore,
        context: Context,
        seedBase: String,
        cycleKey: String,
        sliderKeys: List<String>
    ) {
        TraceJourPrefs.clearSeed(
            context = context,
            seedBase = seedBase
        )

        TraceSaveSatellites.clearLastProcessedDay(
            context = context,
            seedBase = seedBase
        )

        store.loadFromPrefs(
            context = context,
            seedBase = seedBase,
            cycleKey = cycleKey,
            sliderKeys = sliderKeys
        )
    }
}