package com.maxjth.tracememoire.ui.time

import com.maxjth.tracememoire.ui.tracejour.cycle.TraceCycle
import java.time.LocalTime

object TraceCycleClock {

    fun currentCycle(now: LocalTime = LocalTime.now()): TraceCycle {
        return TraceCycle.Companion.fromLocalTime(now)
    }

    fun lockedCycles(current: TraceCycle): Set<TraceCycle> {
        return when (current) {
            TraceCycle.NUIT -> setOf(TraceCycle.SOIR, TraceCycle.JOUR, TraceCycle.MATIN)
            TraceCycle.MATIN -> setOf(TraceCycle.NUIT)
            TraceCycle.JOUR -> setOf(TraceCycle.NUIT, TraceCycle.MATIN)
            TraceCycle.SOIR -> setOf(TraceCycle.NUIT, TraceCycle.MATIN, TraceCycle.JOUR)
        }
    }
}