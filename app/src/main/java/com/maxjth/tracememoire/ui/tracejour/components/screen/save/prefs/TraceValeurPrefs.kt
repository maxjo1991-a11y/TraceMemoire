package com.maxjth.tracememoire.ui.tracejour.components.screen.save.prefs

import android.content.Context
import java.time.LocalDate
import java.time.ZoneId

object TraceValeurPrefs {

    private const val PREF_NAME = "trace_memoire_valeur"
    private const val MAX_DAY_VALUE = 1600

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    private fun todayKey(seedBase: String) =
        "today_value_$seedBase"

    private fun yesterdayKey(seedBase: String) =
        "yesterday_value_$seedBase"

    private fun lastSeenDayKey(seedBase: String) =
        "last_seen_day_$seedBase"

    private fun yesterdayTodayKey(seedBase: String) =
        "yesterday_today_value_$seedBase"

    fun saveTodayValue(
        context: Context,
        seedBase: String,
        value: Int
    ) {
        prefs(context)
            .edit()
            .putInt(todayKey(seedBase), value.coerceIn(0, MAX_DAY_VALUE))
            .apply()
    }

    fun readTodayValue(
        context: Context,
        seedBase: String
    ): Int {
        return prefs(context)
            .getInt(todayKey(seedBase), 0)
            .coerceIn(0, MAX_DAY_VALUE)
    }

    fun saveYesterdayValue(
        context: Context,
        seedBase: String,
        value: Int
    ) {
        prefs(context)
            .edit()
            .putInt(yesterdayKey(seedBase), value.coerceIn(0, MAX_DAY_VALUE))
            .apply()
    }

    fun readYesterdayValue(
        context: Context,
        seedBase: String
    ): Int {
        return prefs(context)
            .getInt(yesterdayKey(seedBase), 0)
            .coerceIn(0, MAX_DAY_VALUE)
    }

    fun saveLastSeenDay(
        context: Context,
        seedBase: String,
        date: LocalDate
    ) {
        prefs(context)
            .edit()
            .putString(lastSeenDayKey(seedBase), date.toString())
            .apply()
    }

    fun readLastSeenDay(
        context: Context,
        seedBase: String
    ): LocalDate? {

        val raw = prefs(context)
            .getString(lastSeenDayKey(seedBase), null)

        return try {
            raw?.let { LocalDate.parse(it) }
        } catch (_: Exception) {
            null
        }
    }

    fun saveYesterdayTodayValue(
        context: Context,
        seedBase: String,
        value: Int
    ) {
        prefs(context)
            .edit()
            .putInt(yesterdayTodayKey(seedBase), value.coerceIn(0, MAX_DAY_VALUE))
            .apply()
    }

    fun readYesterdayTodayValue(
        context: Context,
        seedBase: String,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): Int {
        return prefs(context)
            .getInt(yesterdayTodayKey(seedBase), 0)
            .coerceIn(0, MAX_DAY_VALUE)
    }
}