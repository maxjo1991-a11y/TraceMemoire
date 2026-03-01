package com.maxjth.tracememoire.ui.tracejour.components.screen.save.home

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.TraceJourPrefs
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * TRACE SAVE STORE — HOME (autonome)
 *
 * HOME = persist par jour (daySeed) :
 * - lastPercent (Soleil vivant)
 * - yesterdayPercent (Astra)
 * - yearlyPercent (Orion)  [si tu l’alimentes ailleurs, il est prêt]
 * - cycles complétés (home_done_*)
 * - premiumTouched
 * - rectangle : % par cycle (home_percent_MATIN/JOUR/SOIR/NUIT)
 * - ✅ dots FREE par cycle (home_dots_free_*) => 4 pastilles (humeur/energie/corps/presence)
 *
 * IMPORTANT:
 * - Ce module ne dépend PLUS de TraceSaveStore.
 * - TraceSaveStore ne fait que déléguer à ce HOME.
 */
class TraceSaveStoreHome {

    companion object {
        private val VALID_CYCLES = setOf("NUIT", "MATIN", "JOUR", "SOIR")
        private const val HOME_KEY = "HOME"

        // ⚠️ keys HOME (à ne pas renommer côté prefs)
        private const val KEY_LAST_PERCENT = "HOME_LAST_PERCENT"
        private const val KEY_PREMIUM_TOUCHED = "home_premium_touched"

        // ✅ dots FREE (bitmask int) : 4 bits
        private fun keyDotsFree(cycle: String) = "home_dots_free_${cycle.trim().uppercase()}"

        /**
         * ✅ Mapping sliderKey -> dot index (FREE)
         * On accepte plusieurs formes (parce que tes sliderKey peuvent être "HUMEUR", "humeur", "slider_humeur", etc.)
         */
        private fun freeDotIndexForSliderKey(sliderKey: String): Int? {
            val s = sliderKey.trim().lowercase()

            // On tolère les variantes
            val core = when {
                s.contains("humeur") -> "humeur"
                s.contains("energie") || s.contains("énergie") -> "energie"
                s.contains("corps") -> "corps"
                s.contains("presence") || s.contains("présence") -> "presence"
                else -> null
            } ?: return null

            return when (core) {
                "humeur" -> 0
                "energie" -> 1
                "corps" -> 2
                "presence" -> 3
                else -> null
            }
        }
    }

    // -----------------------------
    // NORMALISATION KEYS
    // -----------------------------
    private fun normCycleKey(raw: String): String = raw.trim().uppercase()

    // -----------------------------
    // ATTACH (pour persist "HOME-only")
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

    // ✅ dots FREE (bitmask par cycle) : "MATIN" -> 0..15
    private val cycleDotsFreeMaskMap = mutableStateMapOf<String, Int>()

    // -----------------------------
    // Keys dérivées (HOME)
    // -----------------------------
    private fun keyDone(cycle: String) = "home_done_${normCycleKey(cycle)}"
    private fun keyRect(cycle: String) = "home_percent_${normCycleKey(cycle)}"

    // -----------------------------
    // API appelée par TraceSaveStore
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

    fun onHomeTick(now: LocalDateTime = LocalDateTime.now()) {
        // refresh Astra (hier)
        val ctx = appContext ?: return
        val seed = seedBaseAttached ?: return
        yesterdayPercent.value = readYesterdayLastPercent(ctx, seed)
    }

    // -----------------------------
    // ✅ Dots FREE — lecture pour HomeScreen
    // Toujours une liste size=4
    // -----------------------------
    fun getFreeDotsForCycle(cycleKey: String): List<Boolean> {
        val c = normCycleKey(cycleKey)
        val mask = cycleDotsFreeMaskMap[c] ?: 0
        return listOf(
            (mask and (1 shl 0)) != 0,
            (mask and (1 shl 1)) != 0,
            (mask and (1 shl 2)) != 0,
            (mask and (1 shl 3)) != 0
        )
    }

    // ✅ set dot FREE par index (0..3)
    fun setFreeDot(cycleKey: String, index: Int, done: Boolean, persist: Boolean = true) {
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

    // ✅ quand un slider FREE est verrouillé => on allume la pastille
    fun onFreeSliderLocked(cycleKey: String, sliderKey: String) {
        val idx = freeDotIndexForSliderKey(sliderKey) ?: return
        setFreeDot(cycleKey, idx, done = true, persist = true)
    }

    fun onCycleLocked(cycleKey: String, currentSoleil: Int?) {
        val c = normCycleKey(cycleKey)
        if (c !in VALID_CYCLES) return

        // 1) done
        completedCycleMap[c] = true

        // 2) rectangle snapshot
        val v = currentSoleil?.coerceIn(0, 100) ?: -1
        if (v in 0..100) cyclePercentMap[c] = v

        // 3) persist HOME-only
        persistHomeOnlyIfAttached()
    }

    fun onCycleSaved(cycleKey: String, currentSoleil: Int?) {
        onCycleLocked(cycleKey, currentSoleil)
    }

    fun loadHomeOnly(context: Context, seedBase: String) {
        attach(context, seedBase)
        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        // lastPercent
        lastPercent.value = TraceJourPrefs
            .getInt(context, daySeed, HOME_KEY, KEY_LAST_PERCENT, -1)
            .let { if (it in 0..100) it else null }

        // premiumTouched
        premiumTouchedToday.value =
            TraceJourPrefs.getBool(context, daySeed, HOME_KEY, KEY_PREMIUM_TOUCHED, false)

        // cycles done + rectangle + dots
        completedCycleMap.clear()
        cyclePercentMap.clear()
        cycleDotsFreeMaskMap.clear()

        VALID_CYCLES.forEach { c ->
            val done = TraceJourPrefs.getBool(context, daySeed, HOME_KEY, keyDone(c), false)
            if (done) completedCycleMap[c] = true

            val rect = TraceJourPrefs.getInt(context, daySeed, HOME_KEY, keyRect(c), -1)
            if (rect in 0..100) cyclePercentMap[c] = rect

            val mask = TraceJourPrefs.getInt(context, daySeed, HOME_KEY, keyDotsFree(c), 0)
            if (mask in 0..15) cycleDotsFreeMaskMap[c] = mask
        }

        // yesterdayPercent (Astra)
        yesterdayPercent.value = readYesterdayLastPercent(context, seedBase)
    }

    fun persistHomeOnlyIfAttached() {
        val ctx = appContext ?: return
        val seedBase = seedBaseAttached ?: return
        persistHomeOnly(ctx, seedBase)
    }

    fun persistHomeOnly(context: Context, seedBase: String) {
        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        // lastPercent
        TraceJourPrefs.putInt(
            context = context,
            seedBase = daySeed,
            cycleKey = HOME_KEY,
            id = KEY_LAST_PERCENT,
            value = lastPercent.value ?: -1
        )

        // premiumTouched
        TraceJourPrefs.putBool(
            context = context,
            seedBase = daySeed,
            cycleKey = HOME_KEY,
            id = KEY_PREMIUM_TOUCHED,
            value = premiumTouchedToday.value
        )

        // cycles done + rectangle + dots
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
        }
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
        val v = TraceJourPrefs.getInt(context, ySeed, HOME_KEY, KEY_LAST_PERCENT, -1)
        return if (v in 0..100) v else null
    }

    fun isValidCycleKey(cycleKey: String): Boolean {
        val key = normCycleKey(cycleKey)
        return key in VALID_CYCLES
    }
}