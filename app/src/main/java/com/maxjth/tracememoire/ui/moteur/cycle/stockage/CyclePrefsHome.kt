// FILE: app/src/main/java/com/maxjth/tracememoire/ui/moteur/cycle/stockage/CyclePrefsHome.kt
package com.maxjth.tracememoire.ui.moteur.cycle.stockage

import android.content.Context
import android.content.SharedPreferences
import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome

/**
 * CyclePrefsHome
 * - Stockage HOME-only (cycles Matin/Jour/Soir/Nuit + % + done + scores HOME)
 *
 * IMPORTANT (verrouillé) :
 * - NE PAS renommer ces clés :
 *   - HOME_LAST_PERCENT
 *   - HOME_DAILY_PERCENT
 *   - home_done_*
 *   - home_percent_*
 */
object CyclePrefsHome {

    // ✅ Fichier prefs (HOME-only). Tu peux changer le nom du fichier si tu as déjà un standard.
    private fun prefsName(seedBase: String): String = "${seedBase}_CYCLE_HOME"

    private fun prefs(context: Context, seedBase: String): SharedPreferences {
        return context.getSharedPreferences(prefsName(seedBase), Context.MODE_PRIVATE)
    }

    // ✅ Keys verrouillées
    const val HOME_LAST_PERCENT: String = "HOME_LAST_PERCENT"
    const val HOME_DAILY_PERCENT: String = "HOME_DAILY_PERCENT"

    // ✅ AJOUTS (non verrouillés, safe)
    private const val HOME_LAST_PERCENT_PREV: String = "HOME_LAST_PERCENT_PREV"
    private const val HOME_LAST_DELTA_TODAY: String = "HOME_LAST_DELTA_TODAY"

    // (Optionnel) si tu veux un stockage yearly ici aussi (ne touche pas aux autres keys existantes)
    private const val HOME_YEARLY_PERCENT: String = "HOME_YEARLY_PERCENT"

    // ✅ Prefix verrouillés
    private const val PREFIX_DONE: String = "home_done_"
    private const val PREFIX_PERCENT: String = "home_percent_"

    // ─────────────────────────────────────────────
    // Keys helpers (NE PAS changer les prefixes)
    // ─────────────────────────────────────────────
    fun keyDone(cycleKey: String): String = PREFIX_DONE + cycleKey
    fun keyPercent(cycleKey: String): String = PREFIX_PERCENT + cycleKey

    fun keyDone(type: TypeCycleHome): String = keyDone(type.storageKey())
    fun keyPercent(type: TypeCycleHome): String = keyPercent(type.storageKey())

    // ─────────────────────────────────────────────
    // HOME scores (last/daily/yearly)
    // ─────────────────────────────────────────────
    fun readHomeLastPercent(context: Context, seedBase: String): Int? {
        val p = prefs(context, seedBase)
        return if (p.contains(HOME_LAST_PERCENT)) p.getInt(HOME_LAST_PERCENT, 0) else null
    }

    fun writeHomeLastPercent(context: Context, seedBase: String, value: Int?) {
        val p = prefs(context, seedBase)
        p.edit().apply {
            if (value == null) remove(HOME_LAST_PERCENT)
            else putInt(HOME_LAST_PERCENT, value.coerceIn(0, 100))
        }.apply()
    }

    // ✅ PREV (ancien %)
    fun readHomeLastPercentPrev(context: Context, seedBase: String): Int? {
        val p = prefs(context, seedBase)
        return if (p.contains(HOME_LAST_PERCENT_PREV)) p.getInt(HOME_LAST_PERCENT_PREV, 0) else null
    }

    fun writeHomeLastPercentPrev(context: Context, seedBase: String, value: Int?) {
        val p = prefs(context, seedBase)
        p.edit().apply {
            if (value == null) remove(HOME_LAST_PERCENT_PREV)
            else putInt(HOME_LAST_PERCENT_PREV, value.coerceIn(0, 100))
        }.apply()
    }

    // ✅ DELTA stable (new - old) : peut être négatif
    fun readHomeLastDeltaToday(context: Context, seedBase: String): Int? {
        val p = prefs(context, seedBase)
        return if (p.contains(HOME_LAST_DELTA_TODAY)) p.getInt(HOME_LAST_DELTA_TODAY, 0) else null
    }

    fun writeHomeLastDeltaToday(context: Context, seedBase: String, value: Int?) {
        val p = prefs(context, seedBase)
        p.edit().apply {
            if (value == null) remove(HOME_LAST_DELTA_TODAY)
            else putInt(HOME_LAST_DELTA_TODAY, value) // ✅ delta peut être négatif, PAS de coerceIn
        }.apply()
    }

    fun readHomeDailyPercent(context: Context, seedBase: String): Int? {
        val p = prefs(context, seedBase)
        return if (p.contains(HOME_DAILY_PERCENT)) p.getInt(HOME_DAILY_PERCENT, 0) else null
    }

    fun writeHomeDailyPercent(context: Context, seedBase: String, value: Int?) {
        val p = prefs(context, seedBase)
        p.edit().apply {
            if (value == null) remove(HOME_DAILY_PERCENT)
            else putInt(HOME_DAILY_PERCENT, value.coerceIn(0, 100))
        }.apply()
    }

    fun readHomeYearlyPercent(context: Context, seedBase: String): Int? {
        val p = prefs(context, seedBase)
        return if (p.contains(HOME_YEARLY_PERCENT)) p.getInt(HOME_YEARLY_PERCENT, 0) else null
    }

    fun writeHomeYearlyPercent(context: Context, seedBase: String, value: Int?) {
        val p = prefs(context, seedBase)
        p.edit().apply {
            if (value == null) remove(HOME_YEARLY_PERCENT)
            else putInt(HOME_YEARLY_PERCENT, value.coerceIn(0, 100))
        }.apply()
    }

    // ─────────────────────────────────────────────
    // Cycle percents
    // ─────────────────────────────────────────────
    fun readCyclePercent(context: Context, seedBase: String, cycleKey: String): Int? {
        val p = prefs(context, seedBase)
        val k = keyPercent(cycleKey)
        return if (p.contains(k)) p.getInt(k, 0).coerceIn(0, 100) else null
    }

    fun writeCyclePercent(context: Context, seedBase: String, cycleKey: String, percent: Int?) {
        val p = prefs(context, seedBase)
        val k = keyPercent(cycleKey)
        p.edit().apply {
            if (percent == null) remove(k)
            else putInt(k, percent.coerceIn(0, 100))
        }.apply()
    }

    fun readCyclePercentMap(context: Context, seedBase: String): Map<String, Int> {
        val p = prefs(context, seedBase)
        val out = linkedMapOf<String, Int>()
        for (t in TypeCycleHome.values()) {
            val k = keyPercent(t)
            if (p.contains(k)) {
                out[t.storageKey()] = p.getInt(k, 0).coerceIn(0, 100)
            }
        }
        return out
    }

    /**
     * ✅ AJOUT IMPORTANT:
     * Écrit la map des percents cycles (home_percent_*) sans renommer aucune key verrouillée.
     *
     * Règle:
     * - map attend des clés "storageKey()" (ex: "MATIN", "JOUR", "SOIR", "NUIT")
     * - on écrit uniquement pour TypeCycleHome.values()
     * - si un cycle n'est pas dans map => on supprime sa key home_percent_*
     */
    fun writeCyclePercentMap(
        context: Context,
        seedBase: String,
        map: Map<String, Int>
    ) {
        val p = prefs(context, seedBase)
        val e = p.edit()

        for (t in TypeCycleHome.values()) {
            val cycleKey = t.storageKey()              // ex "MATIN"
            val prefKey = keyPercent(t)               // ex "home_percent_MATIN"
            val v = map[cycleKey]

            if (v == null) {
                e.remove(prefKey)
            } else {
                e.putInt(prefKey, v.coerceIn(0, 100))
            }
        }

        e.apply()
    }

    // ─────────────────────────────────────────────
    // Done flags
    // ─────────────────────────────────────────────
    fun readCycleDone(context: Context, seedBase: String, cycleKey: String): Boolean {
        val p = prefs(context, seedBase)
        return p.getBoolean(keyDone(cycleKey), false)
    }

    fun writeCycleDone(context: Context, seedBase: String, cycleKey: String, done: Boolean) {
        val p = prefs(context, seedBase)
        p.edit().putBoolean(keyDone(cycleKey), done).apply()
    }

    fun readCompletedToday(context: Context, seedBase: String): Set<TypeCycleHome> {
        val p = prefs(context, seedBase)
        val out = linkedSetOf<TypeCycleHome>()
        for (t in TypeCycleHome.values()) {
            // ✅ Compat: "done" OU "percent présent" = complété
            val doneKey = keyDone(t)
            val percentKey = keyPercent(t)
            val isDone = p.getBoolean(doneKey, false)
            val hasPercent = p.contains(percentKey)
            if (isDone || hasPercent) out.add(t)
        }
        return out
    }

    // ─────────────────────────────────────────────
    // Reset / clear HOME day data
    // ─────────────────────────────────────────────
    fun clearHomeDay(context: Context, seedBase: String) {
        val p = prefs(context, seedBase)
        p.edit().apply {
            // on garde HOME_LAST_PERCENT (souvent utile) mais tu peux l’enlever si tu veux
            remove(HOME_DAILY_PERCENT)

            // ✅ reset aussi prev/delta (sinon delta “reste collé” d’hier)
            remove(HOME_LAST_PERCENT_PREV)
            remove(HOME_LAST_DELTA_TODAY)

            // cycles
            for (t in TypeCycleHome.values()) {
                remove(keyDone(t))
                remove(keyPercent(t))
            }
        }.apply()
    }

    fun clearAll(context: Context, seedBase: String) {
        prefs(context, seedBase).edit().clear().apply()
    }
}