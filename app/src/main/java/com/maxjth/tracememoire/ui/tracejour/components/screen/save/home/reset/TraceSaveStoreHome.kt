package com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.reset

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateMap
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.engine.TraceHomeEngine
import java.time.LocalDateTime

/**
 * TraceSaveStoreHome
 *
 * Wrapper mince branché sur la nouvelle architecture HOME :
 * - date    -> TraceHomeLogicalDate
 * - state   -> TraceHomeState
 * - storage -> TraceHomeStorage
 * - engine  -> TraceHomeEngine
 *
 * Ce fichier reste comme façade compatible pour éviter de casser
 * le reste du projet pendant le ménage.
 */
class TraceSaveStoreHome(
    private val engine: TraceHomeEngine = TraceHomeEngine()
) {

    // -----------------------------
    // ATTACH
    // -----------------------------
    fun attach(context: Context, seedBase: String) {
        engine.attach(context, seedBase)
    }

    // -----------------------------
    // STATE exposé (compat façade)
    // -----------------------------
    val lastPercent: MutableState<Int?> get() = engine.lastPercent as MutableState<Int?>
    val yesterdayPercent: MutableState<Int?> get() = engine.yesterdayPercent as MutableState<Int?>
    val yearlyPercent: MutableState<Int?> get() = engine.yearlyPercent as MutableState<Int?>

    val todayValue: MutableState<Int> get() = engine.todayValue as MutableState<Int>
    val yesterdayValue: MutableState<Int> get() = engine.yesterdayValue as MutableState<Int>

    val premiumTouchedToday: MutableState<Boolean>
        get() = engine.premiumTouchedToday as MutableState<Boolean>

    val completedCycleMap: SnapshotStateMap<String, Boolean> get() = engine.completedCycleMap
    val cyclePercentMap: SnapshotStateMap<String, Int> get() = engine.cyclePercentMap

    val officialCurrent: MutableState<Int?> get() = engine.officialCurrent as MutableState<Int?>
    val officialPrev: MutableState<Int?> get() = engine.officialPrev as MutableState<Int?>
    val officialDelta: MutableState<Int?> get() = engine.officialDelta as MutableState<Int?>

    // -----------------------------
    // API façade
    // -----------------------------
    fun onPremiumTouched() {
        engine.onPremiumTouched()
    }

    fun setPremiumTouchedFlag(value: Boolean) {
        engine.setPremiumTouchedFlag(value)
    }

    fun setLastPercent(value: Int?) {
        engine.setLastPercent(value)
    }

    fun setTodayValue(value: Int) {
        engine.setTodayValue(value)
    }

    fun setYesterdayValue(value: Int) {
        engine.setYesterdayValue(value)
    }

    fun createdAtMillis(cycleKey: String): Long? {
        return engine.createdAtMillis(cycleKey)
    }

    fun getFreeDotsForCycle(cycleKey: String): List<Boolean> {
        return engine.getFreeDotsForCycle(cycleKey)
    }

    fun onFreeSliderLocked(cycleKey: String, sliderKey: String) {
        engine.onFreeSliderLocked(cycleKey, sliderKey)
    }

    fun onHomeTick(now: LocalDateTime = LocalDateTime.now()) {
        engine.onHomeTick(now)
    }

    fun onCycleLocked(cycleKey: String, currentSoleil: Int?) {
        engine.onCycleLocked(cycleKey, currentSoleil)
    }

    fun onCycleSaved(cycleKey: String, currentSoleil: Int?) {
        engine.onCycleSaved(cycleKey, currentSoleil)
    }

    fun commitOfficialSoleil() {
        engine.commitOfficialSoleil()
    }

    // -----------------------------
    // LOAD / PERSIST façade
    // -----------------------------
    fun loadHomeOnly(context: Context, seedBase: String) {
        engine.loadHomeOnly(context, seedBase)
    }

    fun persistHomeOnlyIfAttached() {
        engine.persistHomeOnlyIfAttached()
    }

    fun persistHomeOnly(
        context: Context,
        seedBase: String,
        now: LocalDateTime = LocalDateTime.now()
    ) {
        engine.persistHomeOnly(context, seedBase, now)
    }

    // -----------------------------
    // HELPERS façade
    // -----------------------------
    fun isValidCycleKey(cycleKey: String): Boolean {
        return engine.isValidCycleKey(cycleKey)
    }
}