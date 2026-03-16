package com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.prefs

import android.content.Context
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.prefs.TraceJourPrefs

object TraceHomePrefs {

    private const val HOME_KEY = "HOME"
    private const val KEY_LAST_PERCENT = "HOME_LAST_PERCENT"

    fun readLastPercent(
        context: Context,
        seed: String
    ): Int? {
        val v = TraceJourPrefs.getInt(
            context = context,
            seedBase = seed,
            cycleKey = HOME_KEY,
            id = KEY_LAST_PERCENT,
            def = -1
        )
        return if (v in 0..100) v else null
    }

    fun saveLastPercent(
        context: Context,
        seed: String,
        value: Int?
    ) {
        TraceJourPrefs.putInt(
            context = context,
            seedBase = seed,
            cycleKey = HOME_KEY,
            id = KEY_LAST_PERCENT,
            value = value ?: -1
        )
    }
}