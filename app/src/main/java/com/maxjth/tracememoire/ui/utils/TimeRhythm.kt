package com.maxjth.tracememoire.utils

import java.time.LocalDate

data class MonthlyBreath(
    val minScale: Float,
    val maxScale: Float,
    val durationMs: Int
)

fun currentMonthlyBreath(): MonthlyBreath {
    return when (LocalDate.now().monthValue) {
        1 -> MonthlyBreath(0.93f, 1.05f, 7200)
        2 -> MonthlyBreath(0.94f, 1.05f, 6800)
        3 -> MonthlyBreath(0.95f, 1.06f, 6200)
        4 -> MonthlyBreath(0.96f, 1.07f, 5800)
        5 -> MonthlyBreath(0.97f, 1.08f, 5400)
        6 -> MonthlyBreath(0.96f, 1.07f, 5600)
        7 -> MonthlyBreath(0.97f, 1.08f, 6000)
        8 -> MonthlyBreath(0.95f, 1.06f, 7000)
        9 -> MonthlyBreath(0.96f, 1.06f, 6200)
        10 -> MonthlyBreath(0.95f, 1.05f, 6600)
        11 -> MonthlyBreath(0.94f, 1.05f, 7000)
        else -> MonthlyBreath(0.93f, 1.04f, 7600)
    }
}

