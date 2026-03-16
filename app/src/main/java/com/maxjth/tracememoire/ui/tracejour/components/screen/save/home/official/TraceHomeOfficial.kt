package com.maxjth.tracememoire.ui.tracejour.components.screen.save.home.official

import android.content.Context
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.prefs.TraceJourPrefs

object TraceHomeOfficial {

    private const val HOME_KEY = "HOME"

    private const val KEY_OFFICIAL_PREV = "HOME_OFFICIAL_PREV"
    private const val KEY_OFFICIAL_CURRENT = "HOME_OFFICIAL_CURRENT"
    private const val KEY_OFFICIAL_DELTA = "HOME_OFFICIAL_DELTA"

    fun readCurrent(
        context: Context,
        seed: String
    ): Int? {

        val v = TraceJourPrefs.getInt(
            context,
            seed,
            HOME_KEY,
            KEY_OFFICIAL_CURRENT,
            -1
        )

        return if (v in 0..100) v else null
    }

    fun readPrev(
        context: Context,
        seed: String
    ): Int? {

        val v = TraceJourPrefs.getInt(
            context,
            seed,
            HOME_KEY,
            KEY_OFFICIAL_PREV,
            -1
        )

        return if (v in 0..100) v else null
    }

    fun save(
        context: Context,
        seed: String,
        prev: Int?,
        current: Int?,
        delta: Int?
    ) {

        TraceJourPrefs.putInt(
            context,
            seed,
            HOME_KEY,
            KEY_OFFICIAL_PREV,
            prev ?: -1
        )

        TraceJourPrefs.putInt(
            context,
            seed,
            HOME_KEY,
            KEY_OFFICIAL_CURRENT,
            current ?: -1
        )

        TraceJourPrefs.putInt(
            context,
            seed,
            HOME_KEY,
            KEY_OFFICIAL_DELTA,
            delta ?: 0
        )
    }
}