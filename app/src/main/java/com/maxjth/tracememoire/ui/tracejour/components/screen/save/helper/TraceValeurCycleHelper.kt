package com.maxjth.tracememoire.ui.tracejour.components.screen.save.helper

import android.content.Context
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.logic.TraceValeurLogic
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.prefs.TraceJourPrefs
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.stockage.TraceSaveKeys

object TraceValeurCycleHelper {

    private val validCycles = listOf(
        "NUIT",
        "MATIN",
        "JOUR",
        "SOIR"
    )

    fun normCycleKey(raw: String?): String? {
        val safe = raw?.trim()?.uppercase()
        return if (safe in validCycles) safe else null
    }

    fun readPersistedCycleValue(
        context: Context,
        seedBase: String,
        cycleKey: String
    ): Int {
        val cycle = normCycleKey(cycleKey) ?: return 0

        // ✅ IMPORTANT :
        // on lit la même journée que le stockage du cycle
        val daySeed = TraceJourPrefs.seedForToday(seedBase)

        val sliderMapForCycle = mapOf(
            TraceSaveKeys.SLIDER_HUMEUR to TraceJourPrefs.getInt(
                context = context,
                seedBase = daySeed,
                cycleKey = cycle,
                id = TraceSaveKeys.sliderKey(TraceSaveKeys.SLIDER_HUMEUR),
                def = 0
            ),
            TraceSaveKeys.SLIDER_ENERGIE to TraceJourPrefs.getInt(
                context = context,
                seedBase = daySeed,
                cycleKey = cycle,
                id = TraceSaveKeys.sliderKey(TraceSaveKeys.SLIDER_ENERGIE),
                def = 0
            ),
            TraceSaveKeys.SLIDER_CORPS to TraceJourPrefs.getInt(
                context = context,
                seedBase = daySeed,
                cycleKey = cycle,
                id = TraceSaveKeys.sliderKey(TraceSaveKeys.SLIDER_CORPS),
                def = 0
            ),
            TraceSaveKeys.SLIDER_PRESENCE to TraceJourPrefs.getInt(
                context = context,
                seedBase = daySeed,
                cycleKey = cycle,
                id = TraceSaveKeys.sliderKey(TraceSaveKeys.SLIDER_PRESENCE),
                def = 0
            )
        )

        return TraceValeurLogic.computeCycleValue(sliderMapForCycle)
    }

    fun recomputeTodayValueFromAllCycles(
        context: Context,
        seedBase: String
    ): Int {
        val cycleValues = validCycles.map { cycleKey ->
            readPersistedCycleValue(
                context = context,
                seedBase = seedBase,
                cycleKey = cycleKey
            )
        }

        return cycleValues.sum()
    }
}