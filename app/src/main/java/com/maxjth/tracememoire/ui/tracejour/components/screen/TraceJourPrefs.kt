// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/TraceJourPrefs.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save

import android.content.Context

object TraceJourPrefs {

    private const val PREFS_NAME = "trace_jour_prefs"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun k(seedBase: String, cycleKey: String, id: String) =
        "${seedBase}_${cycleKey}_$id"

    // ─────────────────────────────────────────────
    // ASYNC (apply) — OK la plupart du temps
    // ─────────────────────────────────────────────
    fun putInt(context: Context, seedBase: String, cycleKey: String, id: String, value: Int) {
        prefs(context).edit().putInt(k(seedBase, cycleKey, id), value).apply()
    }

    fun putString(context: Context, seedBase: String, cycleKey: String, id: String, value: String) {
        prefs(context).edit().putString(k(seedBase, cycleKey, id), value).apply()
    }

    fun putBool(context: Context, seedBase: String, cycleKey: String, id: String, value: Boolean) {
        prefs(context).edit().putBoolean(k(seedBase, cycleKey, id), value).apply()
    }

    fun putLong(context: Context, seedBase: String, cycleKey: String, id: String, value: Long) {
        prefs(context).edit().putLong(k(seedBase, cycleKey, id), value).apply()
    }

    // ─────────────────────────────────────────────
    // SYNC (commit) — plus solide si tu quittes/crash vite
    // ─────────────────────────────────────────────
    fun putIntSync(context: Context, seedBase: String, cycleKey: String, id: String, value: Int): Boolean {
        return prefs(context).edit().putInt(k(seedBase, cycleKey, id), value).commit()
    }

    fun putStringSync(context: Context, seedBase: String, cycleKey: String, id: String, value: String): Boolean {
        return prefs(context).edit().putString(k(seedBase, cycleKey, id), value).commit()
    }

    fun putBoolSync(context: Context, seedBase: String, cycleKey: String, id: String, value: Boolean): Boolean {
        return prefs(context).edit().putBoolean(k(seedBase, cycleKey, id), value).commit()
    }

    fun putLongSync(context: Context, seedBase: String, cycleKey: String, id: String, value: Long): Boolean {
        return prefs(context).edit().putLong(k(seedBase, cycleKey, id), value).commit()
    }

    // ─────────────────────────────────────────────
    // READ
    // ─────────────────────────────────────────────
    fun getInt(context: Context, seedBase: String, cycleKey: String, id: String, def: Int): Int =
        prefs(context).getInt(k(seedBase, cycleKey, id), def)

    fun getString(context: Context, seedBase: String, cycleKey: String, id: String, def: String): String =
        prefs(context).getString(k(seedBase, cycleKey, id), def) ?: def

    fun getBool(context: Context, seedBase: String, cycleKey: String, id: String, def: Boolean): Boolean =
        prefs(context).getBoolean(k(seedBase, cycleKey, id), def)

    fun getLong(context: Context, seedBase: String, cycleKey: String, id: String, def: Long): Long =
        prefs(context).getLong(k(seedBase, cycleKey, id), def)

    // ─────────────────────────────────────────────
    // DEBUG / DIAG
    // ─────────────────────────────────────────────
    fun contains(context: Context, seedBase: String, cycleKey: String, id: String): Boolean =
        prefs(context).contains(k(seedBase, cycleKey, id))

    fun dumpCycle(context: Context, seedBase: String, cycleKey: String): Map<String, Any?> {
        val all = prefs(context).all
        val prefix = "${seedBase}_${cycleKey}_"
        return all.filterKeys { it.startsWith(prefix) }
    }
}