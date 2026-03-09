package com.maxjth.tracememoire.utils

data class MonthlyBreath(
    val minScale: Float,
    val maxScale: Float,
    val durationMs: Int
)

fun currentMonthlyBreath(): MonthlyBreath {
    return MonthlyBreath(
        minScale = 0.985f,
        maxScale = 1.015f,
        durationMs = 5200
    )
}
