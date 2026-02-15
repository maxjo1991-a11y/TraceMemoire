// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/logic/TraceCycleClock.kt
package com.maxjth.tracememoire.ui.tracejour.logic

import java.time.LocalDateTime
import java.time.LocalTime

enum class TraceCycle { NUIT, MATIN, JOUR, SOIR }

object TraceCycleClock {

    // ✅ Horaires verrouillés
    private val MATIN_START: LocalTime = LocalTime.of(5, 0)
    private val JOUR_START: LocalTime  = LocalTime.of(12, 0)
    private val SOIR_START: LocalTime  = LocalTime.of(18, 0)
    // Nuit = 00:00 -> 04:59 (donc “avant MATIN_START”)

    fun currentCycle(now: LocalTime = LocalTime.now()): TraceCycle {
        return when {
            now.isBefore(MATIN_START) -> TraceCycle.NUIT
            now.isBefore(JOUR_START)  -> TraceCycle.MATIN
            now.isBefore(SOIR_START)  -> TraceCycle.JOUR
            else -> TraceCycle.SOIR
        }
    }

    fun currentCycle(now: LocalDateTime): TraceCycle = currentCycle(now.toLocalTime())

    fun subtitleForCycle(cycle: TraceCycle): String = when (cycle) {
        TraceCycle.NUIT  -> "Nuit"
        TraceCycle.MATIN -> "Matin"
        TraceCycle.JOUR  -> "Jour"
        TraceCycle.SOIR  -> "Soir"
    }

    // Cycles passés = verrouillés
    private val ORDER = listOf(TraceCycle.NUIT, TraceCycle.MATIN, TraceCycle.JOUR, TraceCycle.SOIR)

    fun lockedCycles(current: TraceCycle): Set<TraceCycle> {
        val idx = ORDER.indexOf(current)
        return if (idx <= 0) emptySet() else ORDER.subList(0, idx).toSet()
    }
}