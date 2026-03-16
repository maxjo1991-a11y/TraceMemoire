package com.maxjth.tracememoire.ui.tracejour.components.screen.save.store

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.logic.TraceValeurLogic
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.prefs.TraceValeurPrefs
import java.time.LocalDateTime
import java.time.ZoneId

class TraceValeurStore {

    private var appContext: Context? = null
    private var seedBaseAttached: String? = null

    val todayValue = mutableStateOf(0)
    val yesterdayValue = mutableStateOf(0)

    fun attach(context: Context, seedBase: String) {
        appContext = context.applicationContext
        seedBaseAttached = seedBase
    }

    fun setTodayValue(value: Int) {
        todayValue.value = TraceValeurLogic.clampDayValue(value)
        persistIfAttached()
    }

    fun setYesterdayValue(value: Int) {
        yesterdayValue.value = TraceValeurLogic.clampDayValue(value)
        persistIfAttached()
    }

    fun recomputeTodayValueFromCurrentCycle(sliderMap: Map<String, Int>) {
        val cycleValue = TraceValeurLogic.computeCycleValue(sliderMap)
        todayValue.value = TraceValeurLogic.clampDayValue(cycleValue)
        persistIfAttached()
    }

    fun recomputeTodayValueFromAllCycles(cycleValues: List<Int>) {
        todayValue.value = TraceValeurLogic.computeDayValue(cycleValues)
        persistIfAttached()
    }

    fun onMidnightTransfer(
        now: LocalDateTime = LocalDateTime.now()
    ) {
        val context = appContext ?: return
        val seedBase = seedBaseAttached ?: return

        val safeToday = TraceValeurLogic.clampDayValue(todayValue.value)

        yesterdayValue.value = safeToday
        todayValue.value = 0

        saveYesterdayFallbackIfAttached(safeToday)

        TraceValeurPrefs.saveLastSeenDay(
            context = context,
            seedBase = seedBase,
            date = now.toLocalDate()
        )

        persistIfAttached()
    }

    fun resetTodayOnly() {
        todayValue.value = 0
        persistIfAttached()
    }

    fun load(context: Context, seedBase: String) {
        attach(context, seedBase)

        val loadedToday = TraceValeurPrefs.readTodayValue(
            context = context,
            seedBase = seedBase
        )

        val loadedYesterday = TraceValeurPrefs.readYesterdayValue(
            context = context,
            seedBase = seedBase
        )

        val fallbackYesterday = TraceValeurPrefs.readYesterdayTodayValue(
            context = context,
            seedBase = seedBase
        )

        todayValue.value = TraceValeurLogic.clampDayValue(loadedToday)

        yesterdayValue.value = if (loadedYesterday > 0) {
            TraceValeurLogic.clampDayValue(loadedYesterday)
        } else {
            TraceValeurLogic.clampDayValue(fallbackYesterday)
        }

        println(
            "DEBUG_MIDNIGHT_3 load -> " +
                    "today=${todayValue.value} " +
                    "yesterday=${yesterdayValue.value} " +
                    "loadedToday=$loadedToday " +
                    "loadedYesterday=$loadedYesterday " +
                    "fallbackYesterday=$fallbackYesterday"
        )
    }

    fun persist(context: Context, seedBase: String) {
        TraceValeurPrefs.saveTodayValue(
            context = context,
            seedBase = seedBase,
            value = TraceValeurLogic.clampDayValue(todayValue.value)
        )

        TraceValeurPrefs.saveYesterdayValue(
            context = context,
            seedBase = seedBase,
            value = TraceValeurLogic.clampDayValue(yesterdayValue.value)
        )
    }

    fun persistIfAttached() {
        val context = appContext ?: return
        val seedBase = seedBaseAttached ?: return
        persist(context, seedBase)
    }

    private fun saveYesterdayFallbackIfAttached(value: Int) {
        val context = appContext ?: return
        val seedBase = seedBaseAttached ?: return

        TraceValeurPrefs.saveYesterdayTodayValue(
            context = context,
            seedBase = seedBase,
            value = TraceValeurLogic.clampDayValue(value)
        )
    }

    fun syncAtTick(
        now: LocalDateTime = LocalDateTime.now(),
        zoneId: ZoneId = ZoneId.systemDefault()
    ) {
        val context = appContext ?: return
        val seedBase = seedBaseAttached ?: return

        val today = now.toLocalDate()
        val lastSeenDay = TraceValeurPrefs.readLastSeenDay(
            context = context,
            seedBase = seedBase
        )

        println(
            "DEBUG_MIDNIGHT_1 before -> " +
                    "today=${todayValue.value} " +
                    "yesterday=${yesterdayValue.value} " +
                    "lastSeenDay=$lastSeenDay " +
                    "todayDate=$today"
        )

        if (lastSeenDay == null) {
            TraceValeurPrefs.saveLastSeenDay(
                context = context,
                seedBase = seedBase,
                date = today
            )
            persist(context, seedBase)
            return
        }

        if (lastSeenDay != today) {
            val safeToday = TraceValeurLogic.clampDayValue(todayValue.value)

            yesterdayValue.value = safeToday
            todayValue.value = 0

            println(
                "DEBUG_MIDNIGHT_2 transfer -> " +
                        "safeToday=$safeToday " +
                        "today=${todayValue.value} " +
                        "yesterday=${yesterdayValue.value}"
            )

            TraceValeurPrefs.saveYesterdayTodayValue(
                context = context,
                seedBase = seedBase,
                value = safeToday
            )

            TraceValeurPrefs.saveLastSeenDay(
                context = context,
                seedBase = seedBase,
                date = today
            )

            persist(context, seedBase)
            return
        }

        if (yesterdayValue.value <= 0) {
            val safeYesterday = TraceValeurPrefs.readYesterdayTodayValue(
                context = context,
                seedBase = seedBase,
                zoneId = zoneId
            )

            if (safeYesterday > 0) {
                yesterdayValue.value = TraceValeurLogic.clampDayValue(safeYesterday)
                persist(context, seedBase)
            }
        }
    }

    fun debugForceTransferToYesterday(
        now: LocalDateTime = LocalDateTime.now()
    ) {
        val context = appContext ?: return
        val seedBase = seedBaseAttached ?: return

        val safeToday = TraceValeurLogic.clampDayValue(todayValue.value)

        yesterdayValue.value = safeToday
        todayValue.value = 0

        saveYesterdayFallbackIfAttached(safeToday)

        TraceValeurPrefs.saveLastSeenDay(
            context = context,
            seedBase = seedBase,
            date = now.toLocalDate()
        )

        persistIfAttached()
    }
}