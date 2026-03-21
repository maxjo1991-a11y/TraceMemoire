package com.maxjth.tracememoire.ui.moteur.cycle.logique

import android.content.Context
import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import com.maxjth.tracememoire.ui.moteur.cycle.stockage.CyclePrefsHome
import java.time.LocalDateTime
import java.time.LocalTime

class MoteurCycleHome {

    // ─────────────────────────────────────────────
    // LOAD
    // ─────────────────────────────────────────────

    fun loadPercentMap(
        context: Context,
        seedBase: String
    ): MutableMap<String, Int> {
        return CyclePrefsHome
            .readCyclePercentMap(context, seedBase)
            .toMutableMap()
    }

    fun loadCompletedToday(
        context: Context,
        seedBase: String
    ): MutableSet<TypeCycleHome> {
        return CyclePrefsHome
            .readCompletedToday(context, seedBase)
            .toMutableSet()
    }

    fun loadHomeLastPercent(
        context: Context,
        seedBase: String
    ): Int? {
        return CyclePrefsHome.readHomeLastPercent(context, seedBase)
    }

    fun loadHomeLastPercentPrev(
        context: Context,
        seedBase: String
    ): Int? {
        return CyclePrefsHome.readHomeLastPercentPrev(context, seedBase)
    }

    fun loadHomeLastDeltaToday(
        context: Context,
        seedBase: String
    ): Int? {
        return CyclePrefsHome.readHomeLastDeltaToday(context, seedBase)
    }

    fun loadHomeDailyPercent(
        context: Context,
        seedBase: String
    ): Int? {
        return CyclePrefsHome.readHomeDailyPercent(context, seedBase)
    }

    fun loadHomeYearlyPercent(
        context: Context,
        seedBase: String
    ): Int? {
        return CyclePrefsHome.readHomeYearlyPercent(context, seedBase)
    }

    // ─────────────────────────────────────────────
    // CYCLE COURANT
    // ─────────────────────────────────────────────

    fun getCurrentCycle(
        now: LocalDateTime = LocalDateTime.now()
    ): TypeCycleHome {
        val time = now.toLocalTime()

        return when {
            time < LocalTime.of(6, 0) -> TypeCycleHome.NUIT
            time < LocalTime.of(12, 0) -> TypeCycleHome.MATIN
            time < LocalTime.of(18, 0) -> TypeCycleHome.JOUR
            else -> TypeCycleHome.SOIR
        }
    }

    // ─────────────────────────────────────────────
    // SAVE
    // ─────────────────────────────────────────────

    fun persistCyclePercent(
        context: Context,
        seedBase: String,
        type: TypeCycleHome,
        percent: Int?
    ) {
        CyclePrefsHome.writeCyclePercent(
            context = context,
            seedBase = seedBase,
            cycleKey = type.storageKey(),
            percent = percent
        )

        if (percent != null) {
            CyclePrefsHome.writeCycleDone(
                context = context,
                seedBase = seedBase,
                cycleKey = type.storageKey(),
                done = true
            )
        }
    }

    fun persistCyclePercentMap(
        context: Context,
        seedBase: String,
        map: Map<String, Int>
    ) {
        CyclePrefsHome.writeCyclePercentMap(
            context = context,
            seedBase = seedBase,
            map = map
        )
    }

    fun persistHomeLastPercent(
        context: Context,
        seedBase: String,
        percent: Int?
    ) {
        val prev = CyclePrefsHome.readHomeLastPercent(context, seedBase)

        CyclePrefsHome.writeHomeLastPercentPrev(
            context = context,
            seedBase = seedBase,
            value = prev
        )

        val delta = if (prev != null && percent != null) (percent - prev) else null
        CyclePrefsHome.writeHomeLastDeltaToday(
            context = context,
            seedBase = seedBase,
            value = delta
        )

        CyclePrefsHome.writeHomeLastPercent(
            context = context,
            seedBase = seedBase,
            value = percent
        )
    }

    fun persistHomeDailyPercent(
        context: Context,
        seedBase: String,
        percent: Int?
    ) {
        CyclePrefsHome.writeHomeDailyPercent(
            context = context,
            seedBase = seedBase,
            value = percent
        )
    }

    fun persistHomeYearlyPercent(
        context: Context,
        seedBase: String,
        percent: Int?
    ) {
        CyclePrefsHome.writeHomeYearlyPercent(
            context = context,
            seedBase = seedBase,
            value = percent
        )
    }

    // ─────────────────────────────────────────────
    // RESET JOURNALIER
    // ─────────────────────────────────────────────

    fun resetHomeDay(
        context: Context,
        seedBase: String
    ) {
        CyclePrefsHome.clearHomeDay(context, seedBase)
    }

    fun resetCyclesOnly(
        context: Context,
        seedBase: String
    ) {
        CyclePrefsHome.writeCyclePercentMap(
            context = context,
            seedBase = seedBase,
            map = emptyMap()
        )

        TypeCycleHome.entries.forEach { type ->
            CyclePrefsHome.writeCycleDone(
                context = context,
                seedBase = seedBase,
                cycleKey = type.storageKey(),
                done = false
            )
        }
    }

    fun resetNewDay(
        context: Context,
        seedBase: String
    ) {
        resetHomeDay(context, seedBase)
        resetCyclesOnly(context, seedBase)
    }
}