// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/logic/TraceCycleClock.kt
package com.maxjth.tracememoire.ui.tracejour.logic

import java.time.LocalDateTime
import java.time.LocalTime

enum class TraceCycle {
    MATIN,
    JOUR,
    SOIR,
    NUIT
}

object TraceCycleClock {

    // Heures verrouillées officiellement
    private val MATIN_START: LocalTime = LocalTime.of(5, 0)
    private val JOUR_START: LocalTime  = LocalTime.of(9, 0)
    private val SOIR_START: LocalTime  = LocalTime.of(17, 0)
    private val NUIT_START: LocalTime  = LocalTime.of(22, 0)

    // ─────────────────────────────────────────────
    // Cycle courant
    // ─────────────────────────────────────────────
    fun currentCycle(now: LocalTime = LocalTime.now()): TraceCycle {
        return when {
            now.isAfter(NUIT_START) || now == NUIT_START || now.isBefore(MATIN_START) -> TraceCycle.NUIT
            now.isAfter(SOIR_START) || now == SOIR_START -> TraceCycle.SOIR
            now.isAfter(JOUR_START) || now == JOUR_START -> TraceCycle.JOUR
            else -> TraceCycle.MATIN
        }
    }

    fun currentCycle(now: LocalDateTime): TraceCycle =
        currentCycle(now.toLocalTime())

    // ─────────────────────────────────────────────
    // Texte UI (sous-titre)
    // ─────────────────────────────────────────────
    fun subtitleForCycle(cycle: TraceCycle): String {
        return when (cycle) {
            TraceCycle.MATIN -> "Matin"
            TraceCycle.JOUR  -> "Jour"
            TraceCycle.SOIR  -> "Soir"
            TraceCycle.NUIT  -> "Nuit"
        }
    }

    // ─────────────────────────────────────────────
    // Ordre officiel + verrouillage
    // ─────────────────────────────────────────────
    private val ORDER: List<TraceCycle> = listOf(
        TraceCycle.MATIN,
        TraceCycle.JOUR,
        TraceCycle.SOIR,
        TraceCycle.NUIT
    )

    fun pastCycles(current: TraceCycle): List<TraceCycle> {
        val index = ORDER.indexOf(current)
        return if (index <= 0) emptyList() else ORDER.subList(0, index)
    }

    fun futureCycles(current: TraceCycle): List<TraceCycle> {
        val index = ORDER.indexOf(current)
        return if (index < 0 || index >= ORDER.lastIndex) emptyList()
        else ORDER.subList(index + 1, ORDER.size)
    }

    // ✅ Cycles passés = verrouillés
    fun lockedCycles(current: TraceCycle): Set<TraceCycle> =
        pastCycles(current).toSet()
}