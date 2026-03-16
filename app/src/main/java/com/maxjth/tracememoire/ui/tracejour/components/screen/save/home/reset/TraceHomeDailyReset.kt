package com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.reset

import android.content.Context
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.prefs.TraceJourPrefs
import java.time.LocalDate
import java.time.LocalDateTime

object TraceHomeDailyReset {

    private const val HOME_KEY = "HOME"
    private const val KEY_LAST_DAY_SEEN = "HOME_LAST_DAY_SEEN"

    fun shouldResetToday(
        context: Context,
        seedBase: String,
        now: LocalDateTime
    ): Boolean {

        val today = now.toLocalDate().toString()

        val lastSeen = TraceJourPrefs.getString(
            context = context,
            seedBase = seedBase,
            cycleKey = HOME_KEY,
            id = KEY_LAST_DAY_SEEN,
            def = ""
        )

        return lastSeen != today
    }

    fun markTodaySeen(
        context: Context,
        seedBase: String,
        now: LocalDateTime
    ) {

        val today = now.toLocalDate().toString()

        TraceJourPrefs.putString(
            context = context,
            seedBase = seedBase,
            cycleKey = HOME_KEY,
            id = KEY_LAST_DAY_SEEN,
            value = today
        )
    }

}