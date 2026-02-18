// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/TraceSaveStore.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import com.maxjth.tracememoire.ui.home.logic.HomeCycleTickerLogic
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class TraceSaveStore {

    companion object {
        // ✅ Home ne doit PAS dépendre du cycleKey (sinon ça se split par cycle).
        private const val HOME_KEY = "HOME"

        private val VALID_CYCLES = setOf("NUIT", "MATIN", "JOUR", "SOIR")
    }

    val state = mutableStateOf(TraceSaveState())

    // autorisation “cycle non verrouillé”
    val canSaveEnabled = mutableStateOf(true)

    // données UI à conserver (cartes)
    val sliderMap = mutableStateMapOf<String, Int>()       // "humeur" -> 78
    val noteMap = mutableStateMapOf<String, String>()      // "humeur" -> "..."
    val capturedMap = mutableStateMapOf<String, Boolean>() // "humeur" -> true

    // timestamp de création par carte (quand captured devient true)
    val createdAtMap = mutableStateMapOf<String, Long>()   // "humeur" -> epochMillis

    // verrouillage par carte
    val lockedMap = mutableStateMapOf<String, Boolean>()   // "humeur" -> true/false

    // ─────────────────────────────────────────────
    // ✅ HOME / carré qui parle + progression
    // ─────────────────────────────────────────────

    // cycles complétés aujourd’hui (NUIT/MATIN/JOUR/SOIR)
    // clé = "NUIT" etc.
    val completedCycleMap = mutableStateMapOf<String, Boolean>()

    // dernier % à afficher dans le carré
    val lastPercent = mutableStateOf<Int?>(null)

    fun setCanSave(enabled: Boolean) {
        canSaveEnabled.value = enabled
    }

    fun markDirty() {
        state.value = state.value.markDirty()
    }

    fun setSaving() {
        state.value = state.value.beginSaving()
    }

    /**
     * Appelé quand l’utilisateur enregistre (sur Écran 2).
     * cycleKey attendu = "NUIT" / "MATIN" / "JOUR" / "SOIR"
     */
    fun setSaved(cycleKey: String) {
        val key = cycleKey.trim().uppercase()
        state.value = state.value.savedNow(LocalDateTime.now(), key)

        // ✅ on marque le cycle complété
        markCycleCompleted(key)
    }

    // on marque "mémoire créée" avec une date si absent
    fun markCaptured(key: String, nowMillis: Long = System.currentTimeMillis()) {
        capturedMap[key] = true
        if ((createdAtMap[key] ?: 0L) <= 0L) {
            createdAtMap[key] = nowMillis
        }
        markDirty()
    }

    // IMPORTANT: c’est une carte qu’on verrouille (clé = "humeur", "energie", etc.)
    fun lockCard(key: String) {
        lockedMap[key] = true
        markDirty()
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
    }

    private fun markCycleCompleted(cycleKey: String) {
        val key = cycleKey.trim().uppercase()
        if (key in VALID_CYCLES) {
            completedCycleMap[key] = true
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

    /**
     * ✅ Progression simple pour ton cercle Home:
     * 0/4=0, 1/4=25, 2/4=50, 3/4=75, 4/4=100
     */
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
        val lastPercent: Int?
    )

    fun capturedCount(): Int = capturedMap.values.count { it }
    fun lockedCount(): Int = lockedMap.values.count { it }

    fun lastCapturedAtMillis(): Long? {
        return createdAtMap.values
            .filter { it > 0L }
            .maxOrNull()
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
            lastPercent = lastPercent.value
        )
    }

    /**
     * ✅ Pont direct vers HomeCycleTickerLogic
     */
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
    // PERSISTENCE (reset quotidien via daySeed)
    // ─────────────────────────────────────────────

    fun loadFromPrefs(
        context: Context,
        seedBase: String,
        cycleKey: String,
        sliderKeys: List<String>
    ) {
        // ✅ scope quotidien => reset naturel chaque jour
        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        // cartes (dépendent du cycleKey)
        sliderKeys.forEach { k ->
            sliderMap[k] = TraceJourPrefs.getInt(context, daySeed, cycleKey, "slider_$k", 50)
            noteMap[k] = TraceJourPrefs.getString(context, daySeed, cycleKey, "note_$k", "")
            capturedMap[k] = TraceJourPrefs.getBool(context, daySeed, cycleKey, "cap_$k", false)

            createdAtMap[k] = TraceJourPrefs.getLong(context, daySeed, cycleKey, "createdAt_$k", 0L)
            lockedMap[k] = TraceJourPrefs.getBool(context, daySeed, cycleKey, "locked_$k", false)
        }

        // état save global (dépend du cycleKey)
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

        // ✅ HOME (ne dépend PAS du cycleKey)
        lastPercent.value = TraceJourPrefs.getInt(context, daySeed, HOME_KEY, "home_lastPercent", -1)
            .let { if (it in 0..100) it else null }

        completedCycleMap.clear()
        VALID_CYCLES.forEach { c ->
            val done = TraceJourPrefs.getBool(context, daySeed, HOME_KEY, "home_done_$c", false)
            if (done) completedCycleMap[c] = true
        }
    }

    fun persistAllToPrefs(context: Context, seedBase: String, cycleKey: String) {
        // ✅ scope quotidien => reset naturel chaque jour
        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        // cartes (dépendent du cycleKey)
        sliderMap.forEach { (k, v) ->
            TraceJourPrefs.putInt(context, daySeed, cycleKey, "slider_$k", v)
        }
        noteMap.forEach { (k, v) ->
            TraceJourPrefs.putString(context, daySeed, cycleKey, "note_$k", v)
        }
        capturedMap.forEach { (k, v) ->
            TraceJourPrefs.putBool(context, daySeed, cycleKey, "cap_$k", v)
        }
        createdAtMap.forEach { (k, v) ->
            TraceJourPrefs.putLong(context, daySeed, cycleKey, "createdAt_$k", v)
        }
        lockedMap.forEach { (k, v) ->
            TraceJourPrefs.putBool(context, daySeed, cycleKey, "locked_$k", v)
        }

        // état save global (dépend du cycleKey)
        val st = state.value
        val epoch = st.lastSavedAt?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli() ?: 0L

        TraceJourPrefs.putLong(context, daySeed, cycleKey, "lastSavedEpoch", epoch)
        TraceJourPrefs.putString(context, daySeed, cycleKey, "lastSavedCycleKey", st.lastSavedCycleKey ?: "")
        TraceJourPrefs.putBool(context, daySeed, cycleKey, "isSaved", st.status == TraceSaveStatus.SAVED)
        TraceJourPrefs.putBool(context, daySeed, cycleKey, "dirty", st.dirty)

        // ✅ HOME (ne dépend PAS du cycleKey)
        TraceJourPrefs.putInt(context, daySeed, HOME_KEY, "home_lastPercent", lastPercent.value ?: -1)

        VALID_CYCLES.forEach { c ->
            TraceJourPrefs.putBool(context, daySeed, HOME_KEY, "home_done_$c", completedCycleMap[c] == true)
        }
    }
}