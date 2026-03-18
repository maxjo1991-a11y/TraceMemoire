package com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.engine

import android.content.Context
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.dots.TraceHomeFreeDots
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.official.TraceHomeOfficial
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.reset.TraceHomeDailyReset
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.state.TraceHomeState
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.storage.TraceHomeStorage
import java.time.LocalDateTime

/**
 * TraceHomeEngine
 *
 * Gère la logique métier HOME :
 * - state en mémoire
 * - attach context / seedBase
 * - dots FREE
 * - lock cycle
 * - reset journalier
 * - tick HOME
 * - commit officiel Soleil
 *
 * Aucun stockage prefs direct ici :
 * tout passe par TraceHomeStorage.
 */
class TraceHomeEngine(
    private val state: TraceHomeState = TraceHomeState(),
    private val storage: TraceHomeStorage = TraceHomeStorage()
) {

    companion object {
        private val VALID_CYCLES = setOf("NUIT", "MATIN", "JOUR", "SOIR")
    }

    // -----------------------------
    // ATTACH (HOME-only)
    // -----------------------------
    private var appContext: Context? = null
    private var seedBaseAttached: String? = null

    fun attach(context: Context, seedBase: String) {
        appContext = context.applicationContext
        seedBaseAttached = seedBase
    }

    // -----------------------------
    // EXPOSITION STATE
    // -----------------------------
    val lastPercent get() = state.lastPercent
    val yesterdayPercent get() = state.yesterdayPercent
    val yearlyPercent get() = state.yearlyPercent

    val todayValue get() = state.todayValue
    val yesterdayValue get() = state.yesterdayValue
    val premiumTouchedToday get() = state.premiumTouchedToday

    val completedCycleMap get() = state.completedCycleMap
    val cyclePercentMap get() = state.cyclePercentMap

    val officialCurrent get() = state.officialCurrent
    val officialPrev get() = state.officialPrev
    val officialDelta get() = state.officialDelta

    fun getState(): TraceHomeState = state

    // -----------------------------
    // NORMALISATION
    // -----------------------------
    private fun normCycleKey(raw: String): String = raw.trim().uppercase()

    fun isValidCycleKey(cycleKey: String): Boolean {
        val key = normCycleKey(cycleKey)
        return key in VALID_CYCLES
    }

    // -----------------------------
    // API simple
    // -----------------------------
    fun onPremiumTouched() {
        state.premiumTouchedToday.value = true
    }

    fun setPremiumTouchedFlag(value: Boolean) {
        state.premiumTouchedToday.value = value
    }

    fun setLastPercent(value: Int?) {
        state.lastPercent.value = value?.coerceIn(0, 100)
    }

    fun setTodayValue(value: Int) {
        state.todayValue.value = value.coerceAtLeast(0)
        persistHomeOnlyIfAttached()
    }

    fun setYesterdayValue(value: Int) {
        state.yesterdayValue.value = value.coerceAtLeast(0)
        persistHomeOnlyIfAttached()
    }

    fun createdAtMillis(cycleKey: String): Long? {
        val c = normCycleKey(cycleKey)
        return state.cycleCreatedAtMillisMap[c]
    }

    // -----------------------------
    // DOTS FREE
    // -----------------------------
    fun getFreeDotsForCycle(cycleKey: String): List<Boolean> {
        val c = normCycleKey(cycleKey)
        return TraceHomeFreeDots.getFreeDotsForCycle(
            cycleKey = c,
            maskMap = state.cycleDotsFreeMaskMap
        )
    }

    private fun setFreeDot(
        cycleKey: String,
        index: Int,
        done: Boolean,
        persist: Boolean = true
    ) {
        val c = normCycleKey(cycleKey)
        if (c !in VALID_CYCLES) return
        if (index !in 0..3) return

        val old = state.cycleDotsFreeMaskMap[c] ?: 0
        val bit = (1 shl index)
        val next = if (done) (old or bit) else (old and bit.inv())

        if (next != old) {
            state.cycleDotsFreeMaskMap[c] = next
            if (persist) persistHomeOnlyIfAttached()
        }
    }

    /**
     * appelée à chaque lock d’un slider FREE.
     */
    fun onFreeSliderLocked(cycleKey: String, sliderKey: String) {
        val idx = TraceHomeFreeDots.freeDotIndexForSliderKey(sliderKey) ?: return
        setFreeDot(cycleKey, idx, done = true, persist = true)
    }

    // -----------------------------
    // LOAD / PERSIST
    // -----------------------------
    fun loadHomeOnly(context: Context, seedBase: String) {
        attach(context, seedBase)
        storage.loadHomeOnly(context, seedBase, state)
    }

    fun persistHomeOnlyIfAttached() {
        val ctx = appContext ?: return
        val seedBase = seedBaseAttached ?: return
        storage.persistHomeOnly(ctx, seedBase, state)
    }

    fun persistHomeOnly(
        context: Context,
        seedBase: String,
        now: LocalDateTime = LocalDateTime.now()
    ) {
        storage.persistHomeOnly(context, seedBase, state, now)
    }

    // -----------------------------
    // RESET / TICK
    // -----------------------------
    fun onHomeTick(now: LocalDateTime = LocalDateTime.now()) {
        val ctx = appContext ?: return
        val seedBase = seedBaseAttached ?: return

        if (TraceHomeDailyReset.shouldResetToday(ctx, seedBase, now)) {
            state.yesterdayValue.value = state.todayValue.value.coerceAtLeast(0)

            state.resetForNewDay()

            TraceHomeDailyReset.markTodaySeen(ctx, seedBase, now)

            storage.persistHomeOnly(ctx, seedBase, state, now)
        }

        state.yesterdayPercent.value = storage.readYesterdayLastPercent(ctx, seedBase)

        if (state.yesterdayValue.value <= 0) {
            state.yesterdayValue.value = storage.readYesterdayValue(ctx, seedBase)
        }
    }

    // -----------------------------
    // LOCK CYCLE
    // -----------------------------
    fun onCycleLocked(cycleKey: String, currentSoleil: Int?) {
        val c = normCycleKey(cycleKey)
        if (c !in VALID_CYCLES) return

        state.completedCycleMap[c] = true

        val v = currentSoleil?.coerceIn(0, 100) ?: -1
        if (v in 0..100) {
            state.cyclePercentMap[c] = v
        }

        if (state.cycleCreatedAtMillisMap[c] == null) {
            state.cycleCreatedAtMillisMap[c] = System.currentTimeMillis()
        }

        persistHomeOnlyIfAttached()
    }

    fun onCycleSaved(cycleKey: String, currentSoleil: Int?) {
        onCycleLocked(cycleKey, currentSoleil)
    }

    // -----------------------------
    // OFFICIEL SOLEIL
    // -----------------------------
    fun commitOfficialSoleil() {
        val ctx = appContext ?: return
        val seedBase = seedBaseAttached ?: return

        val newValue = state.lastPercent.value?.coerceIn(0, 100) ?: return
        val daySeed = com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.date.TraceHomeLogicalDate.effectiveSeed(seedBase)

        val prevValue = TraceHomeOfficial.readCurrent(
            context = ctx,
            seed = daySeed
        )

        state.officialPrev.value = prevValue
        state.officialCurrent.value = newValue
        state.officialDelta.value = if (prevValue != null) (newValue - prevValue) else null

        TraceHomeOfficial.save(
            context = ctx,
            seed = daySeed,
            prev = state.officialPrev.value,
            current = state.officialCurrent.value,
            delta = state.officialDelta.value
        )
    }
}