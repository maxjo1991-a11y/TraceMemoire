package com.maxjth.tracememoire.ui.systeme.lune.trace

import android.content.Context
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.TraceJourPrefs

/**
 * LUNE — TRACE STORE (Source de vérité)
 *
 * Version béton :
 * - prefsName FIXE (ne dépend plus du seedBase)
 * - migration automatique depuis l'ancien prefsName "${seedBase}_LUNE_PREFS"
 * - 1 incrément MAX par cycle et par jour (flag par daySeed)
 * - delta du jour pour afficher "X ─ +Y"
 */
object LuneTraceStore {

    // ✅ TIROIR FIXE : ne change plus jamais
    private const val PREFS_NAME_V1 = "tm_lune_prefs_v1"

    // ✅ clé unique total (mémoire longue)
    private const val KEY_TOTAL = "lune_total"

    // ✅ delta du jour : on garde daySeed dans la clé
    private fun keyDayDelta(daySeed: String) = "${daySeed}_lune_day_delta"

    // ✅ normalisation cycleKey (évite caractères spéciaux si tu passes "JOUR|slider_x")
    private fun normCycleKey(raw: String): String {
        return raw
            .trim()
            .uppercase()
            .replace("|", "_")
            .replace(" ", "_")
            .replace("__", "_")
    }

    // ✅ flag anti-double : on garde daySeed + cycleKey
    private fun keyCounted(daySeed: String, cycleKey: String) =
        "${daySeed}_counted_${normCycleKey(cycleKey)}"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME_V1, Context.MODE_PRIVATE)

    // ─────────────────────────────────────────────
    // MIGRATION (depuis ton ancien système)
    // ─────────────────────────────────────────────
    private fun legacyPrefsName(seedBase: String) = "${seedBase}_LUNE_PREFS"
    private fun legacyCountKey(seedBase: String) = "${seedBase}_LUNE_TRACE_COUNT"
    private fun legacyDayCountKey(daySeed: String) = "${daySeed}_LUNE_DAY_DELTA"

    private fun migrateIfNeeded(context: Context, seedBase: String) {
        val p = prefs(context)

        // Déjà initialisé → on touche plus
        if (p.contains(KEY_TOTAL)) return

        val legacy = context.getSharedPreferences(legacyPrefsName(seedBase), Context.MODE_PRIVATE)

        val oldTotal = legacy.getInt(legacyCountKey(seedBase), 0).coerceAtLeast(0)

        val daySeed = TraceJourPrefs.seedForToday(seedBase)
        val oldDelta = legacy.getInt(legacyDayCountKey(daySeed), 0).coerceAtLeast(0)

        // Init + tentative de récupération ancien total/delta (1 seul commit)
        p.edit()
            .putInt(KEY_TOTAL, oldTotal)
            .putInt(keyDayDelta(daySeed), oldDelta)
            .apply()

        // IMPORTANT :
        // Les flags "COUNTED" legacy ont un autre nom exact → on ne peut pas les reconstruire proprement
        // sans connaitre tous les cycleKeys déjà utilisés. Pas grave : ça ne casse rien.
    }

    // ─────────────────────────────────────────────
    // READ/WRITE
    // ─────────────────────────────────────────────
    fun read(context: Context, seedBase: String): Int {
        migrateIfNeeded(context, seedBase)
        return prefs(context).getInt(KEY_TOTAL, 0).coerceAtLeast(0)
    }

    private fun write(context: Context, seedBase: String, value: Int) {
        migrateIfNeeded(context, seedBase)
        prefs(context).edit().putInt(KEY_TOTAL, value.coerceAtLeast(0)).apply()
    }

    fun readDeltaToday(context: Context, seedBase: String): Int {
        migrateIfNeeded(context, seedBase)
        val daySeed = TraceJourPrefs.seedForToday(seedBase)
        return prefs(context).getInt(keyDayDelta(daySeed), 0).coerceAtLeast(0)
    }

    fun increment(context: Context, seedBase: String): Int {
        val next = read(context, seedBase) + 1
        write(context, seedBase, next)
        return next
    }

    /**
     * 1 incrément max / cycle / jour
     * + incrémente le delta du jour
     */
    fun incrementOncePerCycleToday(
        context: Context,
        seedBase: String,
        cycleKey: String
    ): Int {
        migrateIfNeeded(context, seedBase)

        val daySeed = TraceJourPrefs.seedForToday(seedBase)
        val p = prefs(context)

        val countedKey = keyCounted(daySeed, cycleKey)
        if (p.getBoolean(countedKey, false)) return read(context, seedBase)

        val nextTotal = p.getInt(KEY_TOTAL, 0).coerceAtLeast(0) + 1
        val nextDelta = p.getInt(keyDayDelta(daySeed), 0).coerceAtLeast(0) + 1

        p.edit()
            .putInt(KEY_TOTAL, nextTotal)
            .putInt(keyDayDelta(daySeed), nextDelta)
            .putBoolean(countedKey, true)
            .apply()

        return nextTotal
    }
}