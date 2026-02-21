// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/TraceSaveStore.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.maxjth.tracememoire.ui.home.logic.HomeCycleTickerLogic
import com.maxjth.tracememoire.ui.home.logic.HomeScoreLogic
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class TraceSaveStore {

    companion object {
        private const val HOME_KEY = "HOME"
        private val VALID_CYCLES = setOf("NUIT", "MATIN", "JOUR", "SOIR")

        private const val SLIDER_HUMEUR = "humeur"
        private const val SLIDER_ENERGIE = "energie"
        private const val SLIDER_CORPS = "corps"
        private const val SLIDER_PRESENCE = "presence"

        private const val SLIDER_REPOS = "repos"
        private const val SLIDER_ARCHI_EMO = "archi_emo"
        private const val SLIDER_TYPE_JOURNEE = "type_journee"
        private const val SLIDER_MOTIFS = "motifs_psy"
        private const val SLIDER_ENVIRONNEMENT = "environnement"
        private const val SLIDER_CLARTE = "clarte"

        private const val HOME_LAST_PERCENT = "home_lastPercent"
        private const val HOME_PREMIUM_TOUCHED = "home_premium_touched"
    }

    // ─────────────────────────────────────────────
    // ✅ ATTACH (persist HOME même si HomeScreen ne fait rien)
    // ─────────────────────────────────────────────
    private var appContext: Context? = null
    private var seedBaseAttached: String? = null

    // 🔒 Flag anti-bug : empêche d’écraser la valeur sauvegardée pendant un loadFromPrefs()
    private var isLoadingPrefs: Boolean = false

    /**
     * Appelle ça UNE fois au démarrage (MainActivity / AppRoot).
     * IMPORTANT : seedBase doit être EXACTEMENT le même que celui utilisé par ton écran TraceJour.
     */
    fun attach(context: Context, seedBase: String) {
        appContext = context.applicationContext
        seedBaseAttached = seedBase
    }

    private fun persistHomeOnlyIfAttached() {
        if (isLoadingPrefs) return // ✅ ne jamais persister pendant un load (sinon tu écrases 65 par 50)
        val ctx = appContext ?: return
        val seedBase = seedBaseAttached ?: return
        persistHomeOnly(ctx, seedBase)
    }

    private fun persistHomeOnly(context: Context, seedBase: String) {
        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        // lastPercent
        TraceJourPrefs.putInt(
            context,
            daySeed,
            HOME_KEY,
            HOME_LAST_PERCENT,
            lastPercent.value ?: -1
        )

        // cycles done
        VALID_CYCLES.forEach { c ->
            TraceJourPrefs.putBool(
                context,
                daySeed,
                HOME_KEY,
                "home_done_$c",
                completedCycleMap[c] == true
            )
        }

        // premium touched
        TraceJourPrefs.putBool(
            context,
            daySeed,
            HOME_KEY,
            HOME_PREMIUM_TOUCHED,
            premiumTouchedToday.value
        )
    }

    // ─────────────────────────────────────────────
    // STATE
    // ─────────────────────────────────────────────

    val state = mutableStateOf(TraceSaveState())
    val canSaveEnabled = mutableStateOf(true)

    val sliderMap = mutableStateMapOf<String, Int>()
    val noteMap = mutableStateMapOf<String, String>()
    val capturedMap = mutableStateMapOf<String, Boolean>()

    val createdAtMap = mutableStateMapOf<String, Long>()
    val lockedMap = mutableStateMapOf<String, Boolean>()
    val touchedMap = mutableStateMapOf<String, Boolean>()

    val completedCycleMap = mutableStateMapOf<String, Boolean>()
    val lastPercent = mutableStateOf<Int?>(null)
    val yesterdayPercent = mutableStateOf<Int?>(null)
    val premiumTouchedToday = mutableStateOf(false)

    fun setCanSave(enabled: Boolean) { canSaveEnabled.value = enabled }
    fun markDirty() { state.value = state.value.markDirty() }
    fun setSaving() { state.value = state.value.beginSaving() }

    // ─────────────────────────────────────────────
    // ✅ PREMIUM TOUCH LOGIC
    // ─────────────────────────────────────────────

    private fun isPremiumKey(key: String): Boolean {
        return key == SLIDER_REPOS ||
                key == SLIDER_ARCHI_EMO ||
                key == SLIDER_TYPE_JOURNEE ||
                key == SLIDER_MOTIFS ||
                key == SLIDER_ENVIRONNEMENT ||
                key == SLIDER_CLARTE
    }

    fun markSliderTouched(key: String) {
        touchedMap[key] = true
        if (isPremiumKey(key)) {
            premiumTouchedToday.value = true
            persistHomeOnlyIfAttached()
        }
        markDirty()
    }

    fun isSliderTouched(key: String): Boolean = touchedMap[key] == true

    // ─────────────────────────────────────────────
    // ✅ SCORE HOME (Jupiter)
    // ─────────────────────────────────────────────

    fun recomputeHomeScoreFromSliders(includePremiumAxes: Boolean = true) {
        val baseGlobal = sliderMap[SLIDER_HUMEUR]
        val baseEnergy = sliderMap[SLIDER_ENERGIE]
        val baseBody = sliderMap[SLIDER_CORPS]
        val basePresence = sliderMap[SLIDER_PRESENCE]

        var global = baseGlobal
        var energy = baseEnergy
        var body = baseBody
        var presence = basePresence

        val allowPremiumInfluence = includePremiumAxes && premiumTouchedToday.value

        if (allowPremiumInfluence) {
            val pRepos = sliderMap[SLIDER_REPOS]
            val pArchi = sliderMap[SLIDER_ARCHI_EMO]
            val pType = sliderMap[SLIDER_TYPE_JOURNEE]
            val pMotifs = sliderMap[SLIDER_MOTIFS]
            val pEnv = sliderMap[SLIDER_ENVIRONNEMENT]
            val pClarte = sliderMap[SLIDER_CLARTE]

            val premiumValues = listOf(pRepos, pArchi, pType, pMotifs, pEnv, pClarte).filterNotNull()
            val premiumAvg = premiumValues.takeIf { it.isNotEmpty() }?.average()?.toInt()

            energy = blendOrKeep(energy, pRepos, 0.14f)
            presence = blendOrKeep(presence, pClarte, 0.14f)
            body = blendOrKeep(body, pEnv, 0.10f)
            global = blendOrKeep(global, premiumAvg, 0.12f)

            global = blendOrKeep(global, pArchi, 0.06f)
            global = blendOrKeep(global, pMotifs, 0.06f)
            global = blendOrKeep(global, pType, 0.06f)
        }

        val result = HomeScoreLogic.computeHomeScoreFromRaw(
            global = global,
            energy = energy,
            body = body,
            presence = presence,
            includePremiumAxes = allowPremiumInfluence
        )

        // ✅ IMPORTANT: on ne veut PAS écraser une valeur déjà chargée des prefs
        // sauf si on a des sliders réels.
        setLastPercent(result.percent)
    }

    private fun blendOrKeep(base: Int?, target: Int?, alpha: Float): Int? {
        if (base == null) return null
        if (target == null) return base
        val a = alpha.coerceIn(0f, 1f)
        val out = (base * (1f - a) + target * a).toInt()
        return out.coerceIn(0, 100)
    }

    fun setSaved(cycleKey: String) {
        val key = cycleKey.trim().uppercase()
        state.value = state.value.savedNow(LocalDateTime.now(), key)

        // ✅ cycle done
        markCycleCompleted(key)

        // ✅ recalcul
        recomputeHomeScoreFromSliders(includePremiumAxes = true)

        // ✅ PERSIST HOME IMMÉDIAT
        persistHomeOnlyIfAttached()
    }

    fun markCaptured(key: String, nowMillis: Long = System.currentTimeMillis()) {
        capturedMap[key] = true
        if ((createdAtMap[key] ?: 0L) <= 0L) createdAtMap[key] = nowMillis
        markDirty()
    }

    fun lockCard(key: String) {
        lockedMap[key] = true
        markDirty()
        // ✅ si tu veux “verrouillage écran 2 => Home bloqué”, c’est ici qu’on branchera plus tard
    }

    @Deprecated("Use lockCard(key) — on verrouille une carte, pas un cycle.")
    fun lockCycle(key: String) = lockCard(key)

    fun isLocked(key: String): Boolean = lockedMap[key] == true
    fun createdAtMillis(key: String): Long? = createdAtMap[key]?.takeIf { it > 0L }

    // ─────────────────────────────────────────────
    // ✅ Helpers (Home)
    // ─────────────────────────────────────────────

    fun setLastPercent(percent: Int?) {
        lastPercent.value = percent?.coerceIn(0, 100)
        persistHomeOnlyIfAttached()
    }

    fun setYesterdayPercent(percent: Int?) {
        yesterdayPercent.value = percent?.coerceIn(0, 100)
    }

    private fun markCycleCompleted(cycleKey: String) {
        val key = cycleKey.trim().uppercase()
        if (key in VALID_CYCLES) {
            completedCycleMap[key] = true
            persistHomeOnlyIfAttached()
        }
    }

    fun completedTodaySet(): Set<HomeCycleTickerLogic.CycleId> {
        return completedCycleMap
            .filter { it.value }
            .mapNotNull { (k, _) ->
                runCatching { HomeCycleTickerLogic.CycleId.valueOf(k) }.getOrNull()
            }
            .toSet()
    }

    fun progressPercentToday(): Int {
        val done = completedTodaySet().size.coerceIn(0, 4)
        return done * 25
    }

    data class HomeSaveSnapshot(
        val isSaved: Boolean,
        val dirty: Boolean,
        val lastSavedAt: LocalDateTime?,
        val lastSavedCycleKey: String?,
        val capturedCount: Int,
        val lockedCount: Int,
        val lastCapturedAtMillis: Long?,
        val completedToday: Set<HomeCycleTickerLogic.CycleId>,
        val lastPercent: Int?,
        val yesterdayPercent: Int?,
        val premiumTouchedToday: Boolean
    )

    fun capturedCount(): Int = capturedMap.values.count { it }
    fun lockedCount(): Int = lockedMap.values.count { it }

    fun lastCapturedAtMillis(): Long? {
        return createdAtMap.values.filter { it > 0L }.maxOrNull()
    }

    fun buildHomeSnapshot(): HomeSaveSnapshot {
        val st = state.value
        return HomeSaveSnapshot(
            isSaved = (st.status == TraceSaveStatus.SAVED),
            dirty = st.dirty,
            lastSavedAt = st.lastSavedAt,
            lastSavedCycleKey = st.lastSavedCycleKey,
            capturedCount = capturedCount(),
            lockedCount = lockedCount(),
            lastCapturedAtMillis = lastCapturedAtMillis(),
            completedToday = completedTodaySet(),
            lastPercent = lastPercent.value,
            yesterdayPercent = yesterdayPercent.value,
            premiumTouchedToday = premiumTouchedToday.value
        )
    }

    fun buildHomeTickerSnapshot(now: LocalDateTime = LocalDateTime.now()): HomeCycleTickerLogic.HomeSnapshot {
        val st = state.value
        val lastCycle: HomeCycleTickerLogic.CycleId? =
            st.lastSavedCycleKey
                ?.trim()
                ?.uppercase()
                ?.let { runCatching { HomeCycleTickerLogic.CycleId.valueOf(it) }.getOrNull() }

        return HomeCycleTickerLogic.HomeSnapshot(
            now = now,
            completedToday = completedTodaySet(),
            lastPercent = lastPercent.value,
            lastCycleSaved = lastCycle
        )
    }

    // ─────────────────────────────────────────────
    // PERSISTENCE
    // ─────────────────────────────────────────────

    private fun daySeedForYesterday(seedBase: String, zoneId: ZoneId = ZoneId.systemDefault()): String {
        val yesterday = LocalDate.now(zoneId).minusDays(1)
        return TraceJourPrefs.seedForDate(seedBase, yesterday)
    }

    fun loadFromPrefs(
        context: Context,
        seedBase: String,
        cycleKey: String,
        sliderKeys: List<String>
    ) {
        isLoadingPrefs = true
        try {
            val daySeed = TraceJourPrefs.seedForToday(seedBase)

            // Sliders / notes / flags (si fournis)
            sliderKeys.forEach { k ->
                sliderMap[k] = TraceJourPrefs.getInt(context, daySeed, cycleKey, "slider_$k", 50)
                noteMap[k] = TraceJourPrefs.getString(context, daySeed, cycleKey, "note_$k", "")
                capturedMap[k] = TraceJourPrefs.getBool(context, daySeed, cycleKey, "cap_$k", false)
                createdAtMap[k] = TraceJourPrefs.getLong(context, daySeed, cycleKey, "createdAt_$k", 0L)
                lockedMap[k] = TraceJourPrefs.getBool(context, daySeed, cycleKey, "locked_$k", false)
                touchedMap[k] = TraceJourPrefs.getBool(context, daySeed, cycleKey, "touched_$k", false)
            }

            premiumTouchedToday.value = touchedMap.any { (k, v) -> v && isPremiumKey(k) }

            val lastSavedEpoch = TraceJourPrefs.getLong(context, daySeed, cycleKey, "lastSavedEpoch", 0L)
            val lastSavedCycleKey = TraceJourPrefs.getString(context, daySeed, cycleKey, "lastSavedCycleKey", "")
            val isSaved = TraceJourPrefs.getBool(context, daySeed, cycleKey, "isSaved", false)
            val wasDirty = TraceJourPrefs.getBool(context, daySeed, cycleKey, "dirty", false)

            val dt = if (lastSavedEpoch > 0L) {
                LocalDateTime.ofInstant(Instant.ofEpochMilli(lastSavedEpoch), ZoneId.systemDefault())
            } else null

            state.value = state.value.copy(
                status = if (isSaved) TraceSaveStatus.SAVED else TraceSaveStatus.IDLE,
                dirty = wasDirty && !isSaved,
                lastSavedAt = dt,
                lastSavedCycleKey = lastSavedCycleKey.ifBlank { null },
                cycleKey = cycleKey,
                errorMessage = null
            )

            // ✅ HOME Jupiter (on lit, on n’écrase pas)
            lastPercent.value = TraceJourPrefs.getInt(context, daySeed, HOME_KEY, HOME_LAST_PERCENT, -1)
                .let { if (it in 0..100) it else null }

            completedCycleMap.clear()
            VALID_CYCLES.forEach { c ->
                val done = TraceJourPrefs.getBool(context, daySeed, HOME_KEY, "home_done_$c", false)
                if (done) completedCycleMap[c] = true
            }

            val storedPremiumTouched = TraceJourPrefs.getBool(context, daySeed, HOME_KEY, HOME_PREMIUM_TOUCHED, false)
            premiumTouchedToday.value = premiumTouchedToday.value || storedPremiumTouched

            // ✅ Mars (hier)
            val ySeed = daySeedForYesterday(seedBase)
            yesterdayPercent.value = TraceJourPrefs.getInt(context, ySeed, HOME_KEY, HOME_LAST_PERCENT, -1)
                .let { if (it in 0..100) it else null }

            // ✅ CRITIQUE: on RECALC seulement si on a vraiment des sliders (sinon tu te ramasses à 50%)
            val hasRealSliders = sliderKeys.isNotEmpty()
            if (hasRealSliders && lastPercent.value == null) {
                // Si rien n’est sauvegardé pour HOME, on calcule une première fois.
                // (Sinon, on garde la valeur prefs.)
                recomputeHomeScoreFromSliders(includePremiumAxes = true)
            }
        } finally {
            isLoadingPrefs = false
        }
    }

    fun persistAllToPrefs(context: Context, seedBase: String, cycleKey: String) {
        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        sliderMap.forEach { (k, v) -> TraceJourPrefs.putInt(context, daySeed, cycleKey, "slider_$k", v) }
        noteMap.forEach { (k, v) -> TraceJourPrefs.putString(context, daySeed, cycleKey, "note_$k", v) }
        capturedMap.forEach { (k, v) -> TraceJourPrefs.putBool(context, daySeed, cycleKey, "cap_$k", v) }
        createdAtMap.forEach { (k, v) -> TraceJourPrefs.putLong(context, daySeed, cycleKey, "createdAt_$k", v) }
        lockedMap.forEach { (k, v) -> TraceJourPrefs.putBool(context, daySeed, cycleKey, "locked_$k", v) }
        touchedMap.forEach { (k, v) -> TraceJourPrefs.putBool(context, daySeed, cycleKey, "touched_$k", v) }

        val st = state.value
        val epoch = st.lastSavedAt?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: 0L

        TraceJourPrefs.putLong(context, daySeed, cycleKey, "lastSavedEpoch", epoch)
        TraceJourPrefs.putString(context, daySeed, cycleKey, "lastSavedCycleKey", st.lastSavedCycleKey ?: "")
        TraceJourPrefs.putBool(context, daySeed, cycleKey, "isSaved", st.status == TraceSaveStatus.SAVED)
        TraceJourPrefs.putBool(context, daySeed, cycleKey, "dirty", st.dirty)

        // ✅ HOME aussi
        persistHomeOnly(context, seedBase)
    }
}