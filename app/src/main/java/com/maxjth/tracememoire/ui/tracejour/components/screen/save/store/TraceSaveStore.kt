package com.maxjth.tracememoire.ui.tracejour.components.screen.save.store

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.maxjth.tracememoire.ui.moteur.cycle.logique.MoteurCycleHome
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.coordinator.TraceSaveTickCoordinator
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.engine.TraceSaveConfirmationEngine
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.engine.TraceSaveCycleEngine
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.helper.TraceValeurCycleHelper
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.TraceSaveHomeIO
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.reset.TraceSaveStoreHome
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.state.TraceSaveState
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.stockage.TraceSaveCycleStorage
import java.time.LocalDateTime

class TraceSaveStore {

    // ─────────────────────────────────────────────
    // DEPENDENCIES
    // ─────────────────────────────────────────────

    internal val home = TraceSaveStoreHome()
    internal val valeur = TraceValeurStore()

    private val moteurCycleHome = MoteurCycleHome()
    internal val homeIO = TraceSaveHomeIO(home, moteurCycleHome)

    internal val cycleStorage = TraceSaveCycleStorage()
    internal val cycle = TraceSaveCycleEngine()

    internal val confirmation = TraceSaveConfirmationEngine(
        home = home,
        homeIO = homeIO,
        verrouillerCarte = { k -> lockCard(k) },
        recalculerSoleil = { includePremium ->
            recomputeHomeScoreFromSliders(includePremium)
        }
    )

    // ─────────────────────────────────────────────
    // ATTACH MEMORY
    // ─────────────────────────────────────────────

    internal var attachedContext: Context? = null
    internal var attachedSeedBase: String? = null
    internal var currentLoadedCycleKey: String? = null

    // ─────────────────────────────────────────────
    // EXPOSITIONS POUR HOME
    // ─────────────────────────────────────────────

    val lastPercent = home.lastPercent
    val cyclePercentMap = home.cyclePercentMap
    val yearlyPercent = home.yearlyPercent

    val todayValue = valeur.todayValue
    val yesterdayValue = valeur.yesterdayValue

    val lastDeltaToday = mutableStateOf<Int?>(null)

    val luneTick = mutableIntStateOf(0)
    val terreTick = mutableIntStateOf(0)

    // ─────────────────────────────────────────────
    // STATE GENERAL
    // ─────────────────────────────────────────────

    val state = mutableStateOf(TraceSaveState())
    val canSaveEnabled = mutableStateOf(true)

    // ─────────────────────────────────────────────
    // COORDINATOR
    // ─────────────────────────────────────────────

    internal val tickCoordinator = TraceSaveTickCoordinator(
        home = home,
        valeur = valeur,
        homeIO = homeIO,
        cycleClearAll = { cycle.clearAll() },
        stateSetter = { state.value = it },
        yearlyPercentState = yearlyPercent,
        currentLoadedCycleKeySetter = { currentLoadedCycleKey = it },
        luneTick = luneTick,
        terreTick = terreTick,
        cyclePercentMapProvider = { cyclePercentMap },
        lastDeltaToday = lastDeltaToday,
        recomputeTodayValueFromAllCycles = {
            recomputeTodayValueFromAllCycles()
        }
    )

    // ─────────────────────────────────────────────
    // VALEUR JOUR / HIER
    // ─────────────────────────────────────────────

    internal fun recomputeTodayValueFromAllCycles() {

        val context = attachedContext ?: return
        val seedBase = attachedSeedBase ?: return

        val total =
            TraceValeurCycleHelper.recomputeTodayValueFromAllCycles(
                context = context,
                seedBase = seedBase
            )

        valeur.setTodayValue(total)
    }

    fun getFreeDotsForCycle(cycleKey: String): List<Boolean> {
        return home.getFreeDotsForCycle(cycleKey)
    }

    // ─────────────────────────────────────────────
    // ATTACH
    // ─────────────────────────────────────────────

    fun attach(context: Context, seedBase: String) {
        TraceSaveStoreAttach.attach(this, context, seedBase)
    }

    fun setCanSave(enabled: Boolean) {
        TraceSaveStoreAttach.setCanSave(this, enabled)
    }

    fun markDirty() {
        TraceSaveStoreAttach.markDirty(this)
    }

    fun setSaving() {
        TraceSaveStoreAttach.setSaving(this)
    }

    // ─────────────────────────────────────────────
    // MAPS (CycleEngine)
    // ─────────────────────────────────────────────

    val sliderMap
        get() = TraceSaveStoreCycle.sliderMap(this)

    val noteMap
        get() = TraceSaveStoreCycle.noteMap(this)

    val capturedMap
        get() = TraceSaveStoreCycle.capturedMap(this)

    val createdAtMap
        get() = TraceSaveStoreCycle.createdAtMap(this)

    val lockedMap
        get() = TraceSaveStoreCycle.lockedMap(this)

    val touchedMap
        get() = TraceSaveStoreCycle.touchedMap(this)

    fun markCaptured(key: String) {
        TraceSaveStoreCycle.markCaptured(this, key)
    }

    fun lockCard(key: String) {
        TraceSaveStoreCycle.lockCard(this, key)
    }

    fun isLocked(key: String): Boolean {
        return TraceSaveStoreCycle.isLocked(this, key)
    }

    fun createdAtMillis(key: String): Long? {
        return TraceSaveStoreCycle.createdAtMillis(this, key)
    }

    fun markSliderTouched(key: String) {
        TraceSaveStoreCycle.markSliderTouched(this, key)
    }

    fun isSliderTouched(key: String): Boolean {
        return TraceSaveStoreCycle.isSliderTouched(this, key)
    }

    // ─────────────────────────────────────────────
    // HOME TICK
    // ─────────────────────────────────────────────

    fun onHomeTick(now: LocalDateTime = LocalDateTime.now()) {
        tickCoordinator.onHomeTick(
            attachedContext = attachedContext,
            attachedSeedBase = attachedSeedBase,
            now = now
        )
    }

    // ─────────────────────────────────────────────
    // DEBUG MIDNIGHT
    // ─────────────────────────────────────────────

    fun debugForceMidnight(
        context: Context,
        seedBase: String
    ) {
        TraceSaveStoreDebug.debugForceMidnight(
            this,
            context,
            seedBase
        )
    }

    // ─────────────────────────────────────────────
    // SCORE → SOLEIL
    // ─────────────────────────────────────────────

    fun recomputeHomeScoreFromSliders(
        includePremiumAxes: Boolean = true
    ) {
        TraceSaveStoreScore.recomputeHomeScoreFromSliders(
            this,
            includePremiumAxes
        )
    }

    // ─────────────────────────────────────────────
    // LOCK → COMMIT OFFICIEL
    // ─────────────────────────────────────────────

    fun handleLockForHomeAndRect(
        cycleKey: String,
        sliderKey: String
    ) {
        TraceSaveStoreCommit.handleLockForHomeAndRect(
            this,
            cycleKey,
            sliderKey
        )
    }

    // ─────────────────────────────────────────────
    // LOAD HOME (HomeScreen)
    // ─────────────────────────────────────────────

    fun loadHomeOnlyForHomeScreen(
        context: Context,
        seedBase: String
    ) {
        TraceSaveStorePersist.loadHomeOnlyForHomeScreen(
            this,
            context,
            seedBase
        )
    }

    // ─────────────────────────────────────────────
    // DEBUG
    // ─────────────────────────────────────────────

    fun debugClearSeedAndReload(
        context: Context,
        seedBase: String,
        cycleKey: String,
        sliderKeys: List<String>
    ) {
        TraceSaveStoreDebug.debugClearSeedAndReload(
            this,
            context,
            seedBase,
            cycleKey,
            sliderKeys
        )
    }

    // ─────────────────────────────────────────────
    // LOAD / SAVE CYCLE
    // ─────────────────────────────────────────────

    fun loadFromPrefs(
        context: Context,
        seedBase: String,
        cycleKey: String,
        sliderKeys: List<String>
    ) {
        TraceSaveStorePersist.loadFromPrefs(
            this,
            context,
            seedBase,
            cycleKey,
            sliderKeys
        )
    }

    fun persistAllToPrefs(
        context: Context,
        seedBase: String,
        cycleKey: String
    ) {
        TraceSaveStorePersist.persistAllToPrefs(
            this,
            context,
            seedBase,
            cycleKey
        )
    }
}