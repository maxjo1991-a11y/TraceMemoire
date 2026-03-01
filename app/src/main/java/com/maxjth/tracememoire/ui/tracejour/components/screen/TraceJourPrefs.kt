// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/TraceJourPrefs.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save

import android.content.Context
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TraceJourPrefs {

    private const val PREFS_NAME = "trace_jour_prefs"

    private val DAY_FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun seedForDate(seedBase: String, date: LocalDate): String =
        "${seedBase}_${date.format(DAY_FMT)}"

    fun seedForToday(
        seedBase: String,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): String = seedForDate(seedBase, LocalDate.now(zoneId))

    fun seedForEpochMillis(
        seedBase: String,
        epochMillis: Long,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): String {
        val d = Instant.ofEpochMilli(epochMillis).atZone(zoneId).toLocalDate()
        return seedForDate(seedBase, d)
    }

    private fun k(seedBase: String, cycleKey: String, id: String) =
        "${seedBase}_${cycleKey}_$id"

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

    fun getInt(context: Context, seedBase: String, cycleKey: String, id: String, def: Int): Int =
        prefs(context).getInt(k(seedBase, cycleKey, id), def)

    fun getString(context: Context, seedBase: String, cycleKey: String, id: String, def: String): String =
        prefs(context).getString(k(seedBase, cycleKey, id), def) ?: def

    fun getBool(context: Context, seedBase: String, cycleKey: String, id: String, def: Boolean): Boolean =
        prefs(context).getBoolean(k(seedBase, cycleKey, id), def)

    fun getLong(context: Context, seedBase: String, cycleKey: String, id: String, def: Long): Long =
        prefs(context).getLong(k(seedBase, cycleKey, id), def)

    fun clearCycle(context: Context, seedBase: String, cycleKey: String) {
        val p = prefs(context)
        val prefix = "${seedBase}_${cycleKey}_"
        val keys = p.all.keys.filter { it.startsWith(prefix) }
        if (keys.isEmpty()) return

        val e = p.edit()
        keys.forEach { e.remove(it) }
        e.apply()
    }
}