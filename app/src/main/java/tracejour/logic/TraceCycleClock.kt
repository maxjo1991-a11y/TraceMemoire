package com.maxjth.tracememoire.ui.tracejour.logic

import java.time.LocalDateTime
import java.time.LocalTime

enum class TraceCycle { NUIT, MATIN, JOUR, SOIR }

object TraceCycleClock {

    private val MATIN_START: LocalTime = LocalTime.of(5, 0)
    private val JOUR_START: LocalTime  = LocalTime.of(12, 0)
    private val SOIR_START: LocalTime  = LocalTime.of(18, 0)

    fun now(): LocalDateTime = LocalDateTime.now()

    fun currentCycle(now: LocalTime = LocalTime.now()): TraceCycle {
        return when {
            now.isBefore(MATIN_START) -> TraceCycle.NUIT
            now.isBefore(JOUR_START)  -> TraceCycle.MATIN
            now.isBefore(SOIR_START)  -> TraceCycle.JOUR
            else -> TraceCycle.SOIR
        }
    }

    fun cycleForDateTime(now: LocalDateTime = LocalDateTime.now()): TraceCycle {
        return currentCycle(now.toLocalTime())
    }

    fun subtitleForCycle(cycle: TraceCycle): String = when (cycle) {
        TraceCycle.NUIT  -> "Nuit"
        TraceCycle.MATIN -> "Matin"
        TraceCycle.JOUR  -> "Jour"
        TraceCycle.SOIR  -> "Soir"
    }

    fun formatStamp(dt: LocalDateTime): String {
        val day = dt.dayOfMonth
        val month = dt.month.name.lowercase().replaceFirstChar { it.uppercase() }
        val year = dt.year

        val hour = dt.hour
        val minute = dt.minute.toString().padStart(2, '0')

        val ampm = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }

        return "$day $month $year $displayHour:$minute $ampm"
    }

    private val ORDER = listOf(TraceCycle.NUIT, TraceCycle.MATIN, TraceCycle.JOUR, TraceCycle.SOIR)

    fun lockedCycles(current: TraceCycle): Set<TraceCycle> {
        val idx = ORDER.indexOf(current)
        return if (idx <= 0) emptySet() else ORDER.subList(0, idx).toSet()
    }
}