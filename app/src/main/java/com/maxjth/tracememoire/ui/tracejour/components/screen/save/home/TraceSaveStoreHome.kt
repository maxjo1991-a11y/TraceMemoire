package com.maxjth.tracememoire.ui.tracejour.components.screen.save.home

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.TraceJourPrefs
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.dots.TraceHomeFreeDots
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.official.TraceHomeOfficial
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.prefs.TraceHomePrefs
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.reset.TraceHomeDailyReset
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * TraceSaveStoreHome
 *
 * HOME (autonome) :
 * - lastPercent (Soleil live)
 * - yesterdayPercent (Astra)
 * - yearlyPercent (Orion) (stocké ailleurs si besoin)
 * - cycles complétés (home_done_*)
 * - rectangle snapshot par cycle (home_percent_*)
 * - dots FREE par cycle (home_dots_free_*)
 * - createdAtMillis par cycle (home_created_at_*)
 *
 * ✅ IMPORTANT :
 * - Reset journalier (Option A) : via TraceHomeDailyReset (seedBase STABLE),
 *   puis on persiste le reset dans le daySeed.
 * - Soleil officiel (prev/current/delta) : via TraceHomeOfficial (daySeed).
 * - Dots FREE mapping : via TraceHomeFreeDots (pure helpers).
 *
 * ✅ CONNECTÉ :
 * - HOME_LAST_PERCENT lit/écrit via TraceHomePrefs (donc usages visibles + source unique)
 */
class TraceSaveStoreHome {

    companion object {
        private val VALID_CYCLES = setOf("NUIT", "MATIN", "JOUR", "SOIR")
        private const val HOME_KEY = "HOME"

        // ⚠️ keys HOME (à ne pas renommer côté prefs)
        private const val KEY_PREMIUM_TOUCHED = "home_premium_touched"

        // ✅ dots FREE (bitmask int) : 4 bits
        private fun keyDotsFree(cycle: String) = "home_dots_free_${cycle.trim().uppercase()}"

        // ✅ done + rect + createdAt
        private fun keyDone(cycle: String) = "home_done_${cycle.trim().uppercase()}"
        private fun keyRect(cycle: String) = "home_percent_${cycle.trim().uppercase()}"
        private fun keyCreatedAt(cycle: String) = "home_created_at_${cycle.trim().uppercase()}"
    }

    // -----------------------------
    // NORMALISATION
    // -----------------------------
    private fun normCycleKey(raw: String): String = raw.trim().uppercase()

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
    // STATE HOME exposé
    // -----------------------------
    val lastPercent = mutableStateOf<Int?>(null)
    val yesterdayPercent = mutableStateOf<Int?>(null)
    val yearlyPercent = mutableStateOf<Int?>(null)

    val premiumTouchedToday = mutableStateOf(false)

    // cycle done + rectangle
    val completedCycleMap = mutableStateMapOf<String, Boolean>()
    val cyclePercentMap = mutableStateMapOf<String, Int>()

    // ✅ dots FREE (bitmask par cycle)
    private val cycleDotsFreeMaskMap = mutableStateMapOf<String, Int>()

    // ✅ HEURE FIGÉE (millis) par cycle (rectangle Home)
    private val cycleCreatedAtMillisMap = mutableStateMapOf<String, Long>()

    // 🔥 PATCH OFFICIEL SOLEIL : état exposé
    val officialCurrent = mutableStateOf<Int?>(null)
    val officialPrev = mutableStateOf<Int?>(null)
    val officialDelta = mutableStateOf<Int?>(null)

    // -----------------------------
    // API (appelée par Store)
    // -----------------------------
    fun onPremiumTouched() {
        premiumTouchedToday.value = true
    }

    fun setPremiumTouchedFlag(value: Boolean) {
        premiumTouchedToday.value = value
    }

    fun setLastPercent(value: Int?) {
        lastPercent.value = value?.coerceIn(0, 100)
    }

    // ✅ exposer l’heure figée au HomeScreen
    fun createdAtMillis(cycleKey: String): Long? {
        val c = normCycleKey(cycleKey)
        return cycleCreatedAtMillisMap[c]
    }

    // -----------------------------
    // ✅ DOTS FREE (extrait dans home/dots)
    // -----------------------------
    fun getFreeDotsForCycle(cycleKey: String): List<Boolean> {
        val c = normCycleKey(cycleKey)
        return TraceHomeFreeDots.getFreeDotsForCycle(
            cycleKey = c,
            maskMap = cycleDotsFreeMaskMap
        )
    }

    private fun setFreeDot(cycleKey: String, index: Int, done: Boolean, persist: Boolean = true) {
        val c = normCycleKey(cycleKey)
        if (c !in VALID_CYCLES) return
        if (index !in 0..3) return

        val old = cycleDotsFreeMaskMap[c] ?: 0
        val bit = (1 shl index)
        val next = if (done) (old or bit) else (old and bit.inv())

        if (next != old) {
            cycleDotsFreeMaskMap[c] = next
            if (persist) persistHomeOnlyIfAttached()
        }
    }

    /**
     * ✅ appelée par TraceSaveConfirmationEngine à chaque lock d’un slider FREE.
     */
    fun onFreeSliderLocked(cycleKey: String, sliderKey: String) {
        val idx = TraceHomeFreeDots.freeDotIndexForSliderKey(sliderKey) ?: return
        setFreeDot(cycleKey, idx, done = true, persist = true)
    }

    // -----------------------------
    // ✅ RESET JOURNALIER (Option A)
    // -----------------------------
    private fun resetForNewDay() {
        lastPercent.value = null
        premiumTouchedToday.value = false

        completedCycleMap.clear()
        cyclePercentMap.clear()
        cycleDotsFreeMaskMap.clear()
        cycleCreatedAtMillisMap.clear()

        // reset OFFICIEL (intra-day, en mémoire)
        officialCurrent.value = null
        officialPrev.value = null
        officialDelta.value = null
    }

    fun onHomeTick(now: LocalDateTime = LocalDateTime.now()) {
        val ctx = appContext ?: return
        val seedBase = seedBaseAttached ?: return

        // ✅ détection reset via helper (seedBase STABLE)
        if (TraceHomeDailyReset.shouldResetToday(ctx, seedBase, now)) {
            resetForNewDay()

            // ✅ marque la journée (seedBase STABLE)
            TraceHomeDailyReset.markTodaySeen(ctx, seedBase, now)

            // ✅ persister le HOME du jour (daySeed) en état reset
            persistHomeOnly(ctx, seedBase)
        }

        // refresh Astra (hier)
        yesterdayPercent.value = readYesterdayLastPercent(ctx, seedBase)
    }

    // -----------------------------
    // ✅ LOCK CYCLE (snapshot rectangle + createdAt + persist)
    // -----------------------------
    fun onCycleLocked(cycleKey: String, currentSoleil: Int?) {
        val c = normCycleKey(cycleKey)
        if (c !in VALID_CYCLES) return

        // 1) done
        completedCycleMap[c] = true

        // 2) rectangle snapshot
        val v = currentSoleil?.coerceIn(0, 100) ?: -1
        if (v in 0..100) cyclePercentMap[c] = v

        // 3) heure figée
        if (cycleCreatedAtMillisMap[c] == null) {
            cycleCreatedAtMillisMap[c] = System.currentTimeMillis()
        }

        // 4) persist HOME-only
        persistHomeOnlyIfAttached()
    }

    fun onCycleSaved(cycleKey: String, currentSoleil: Int?) {
        onCycleLocked(cycleKey, currentSoleil)
    }

    // -----------------------------
    // 🔥 SOLEIL OFFICIEL (extrait dans home/official)
    // -----------------------------
    fun commitOfficialSoleil() {
        val ctx = appContext ?: return
        val seedBase = seedBaseAttached ?: return
        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        val newValue = lastPercent.value?.coerceIn(0, 100) ?: return

        val prevValue = TraceHomeOfficial.readCurrent(
            context = ctx,
            seed = daySeed
        )

        officialPrev.value = prevValue
        officialCurrent.value = newValue
        officialDelta.value = if (prevValue != null) (newValue - prevValue) else null

        TraceHomeOfficial.save(
            context = ctx,
            seed = daySeed,
            prev = officialPrev.value,
            current = officialCurrent.value,
            delta = officialDelta.value
        )
    }

    // -----------------------------
    // LOAD HOME-only
    // -----------------------------
    fun loadHomeOnly(context: Context, seedBase: String) {
        attach(context, seedBase)
        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        // ✅ CONNECTÉ: lastPercent via TraceHomePrefs
        lastPercent.value = TraceHomePrefs.readLastPercent(context, daySeed)

        premiumTouchedToday.value =
            TraceJourPrefs.getBool(context, daySeed, HOME_KEY, KEY_PREMIUM_TOUCHED, false)

        completedCycleMap.clear()
        cyclePercentMap.clear()
        cycleDotsFreeMaskMap.clear()
        cycleCreatedAtMillisMap.clear()

        VALID_CYCLES.forEach { c ->
            val done = TraceJourPrefs.getBool(context, daySeed, HOME_KEY, keyDone(c), false)
            if (done) completedCycleMap[c] = true

            val rect = TraceJourPrefs.getInt(context, daySeed, HOME_KEY, keyRect(c), -1)
            if (rect in 0..100) cyclePercentMap[c] = rect

            val mask = TraceJourPrefs.getInt(context, daySeed, HOME_KEY, keyDotsFree(c), 0)
            if (mask in 0..15) cycleDotsFreeMaskMap[c] = mask

            val createdAt = TraceJourPrefs.getLong(context, daySeed, HOME_KEY, keyCreatedAt(c), -1L)
            if (createdAt > 0L) cycleCreatedAtMillisMap[c] = createdAt
        }

        yesterdayPercent.value = readYesterdayLastPercent(context, seedBase)

        // officiel (daySeed)
        officialCurrent.value = TraceHomeOfficial.readCurrent(context, daySeed)
        officialPrev.value = TraceHomeOfficial.readPrev(context, daySeed)

        val rawDelta = TraceJourPrefs.getInt(context, daySeed, HOME_KEY, "HOME_OFFICIAL_DELTA", 0)
        officialDelta.value = if (officialPrev.value == null) null else rawDelta
    }

    // -----------------------------
    // PERSIST HOME-only
    // -----------------------------
    fun persistHomeOnlyIfAttached() {
        val ctx = appContext ?: return
        val seedBase = seedBaseAttached ?: return
        persistHomeOnly(ctx, seedBase)
    }

    fun persistHomeOnly(context: Context, seedBase: String) {
        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        // ✅ CONNECTÉ: lastPercent via TraceHomePrefs
        TraceHomePrefs.saveLastPercent(context, daySeed, lastPercent.value)

        TraceJourPrefs.putBool(
            context = context,
            seedBase = daySeed,
            cycleKey = HOME_KEY,
            id = KEY_PREMIUM_TOUCHED,
            value = premiumTouchedToday.value
        )

        VALID_CYCLES.forEach { c ->
            TraceJourPrefs.putBool(
                context = context,
                seedBase = daySeed,
                cycleKey = HOME_KEY,
                id = keyDone(c),
                value = (completedCycleMap[c] == true)
            )

            val rect = cyclePercentMap[c] ?: -1
            TraceJourPrefs.putInt(
                context = context,
                seedBase = daySeed,
                cycleKey = HOME_KEY,
                id = keyRect(c),
                value = rect
            )

            val mask = (cycleDotsFreeMaskMap[c] ?: 0).coerceIn(0, 15)
            TraceJourPrefs.putInt(
                context = context,
                seedBase = daySeed,
                cycleKey = HOME_KEY,
                id = keyDotsFree(c),
                value = mask
            )

            val createdAt = cycleCreatedAtMillisMap[c] ?: -1L
            TraceJourPrefs.putLong(
                context = context,
                seedBase = daySeed,
                cycleKey = HOME_KEY,
                id = keyCreatedAt(c),
                value = createdAt
            )
        }

        // ✅ OFFICIEL: NE PAS le réécrire ici.
        // Il est géré UNIQUEMENT via TraceHomeOfficial.save() dans commitOfficialSoleil().
    }

    // -----------------------------
    // Helpers
    // -----------------------------
    private fun readYesterdayLastPercent(
        context: Context,
        seedBase: String,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Int? {
        val yesterday = LocalDate.now(zoneId).minusDays(1)
        val ySeed = TraceJourPrefs.seedForDate(seedBase, yesterday)

        // ✅ CONNECTÉ: yesterday via TraceHomePrefs aussi
        return TraceHomePrefs.readLastPercent(context, ySeed)
    }

    fun isValidCycleKey(cycleKey: String): Boolean {
        val key = normCycleKey(cycleKey)
        return key in VALID_CYCLES
    }
}