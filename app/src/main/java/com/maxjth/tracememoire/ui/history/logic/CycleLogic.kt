package com.maxjth.tracememoire.ui.logic

import java.util.Calendar

enum class DayCycle {
    MORNING,
    DAY,
    EVENING,
    NIGHT
}

fun currentCycle(): DayCycle {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    return when (hour) {
        in 5..10 -> DayCycle.MORNING
        in 11..16 -> DayCycle.DAY
        in 17..21 -> DayCycle.EVENING
        else -> DayCycle.NIGHT
    }
}

fun cycleLabel(cycle: DayCycle): String {
    return when (cycle) {
        DayCycle.MORNING -> "Matin disponible"
        DayCycle.DAY -> "Cycle du jour actif"
        DayCycle.EVENING -> "Soir disponible"
        DayCycle.NIGHT -> "Nuit active"
    }
}

