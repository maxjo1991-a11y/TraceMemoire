// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/cycle/TraceCycle.kt
package com.maxjth.tracememoire.ui.tracejour.cycle

import java.time.LocalDate
import java.time.LocalTime

/**
 * TraceCycle — Cycle verrouillé (Nuit / Matin / Jour / Soir)
 *
 * Règles verrouillées :
 * - Nuit  : 00:00 - 04:59
 * - Matin : 05:00 - 11:59
 * - Jour  : 12:00 - 17:59
 * - Soir  : 18:00 - 23:59
 */
enum class TraceCycle(
    val start: LocalTime,
    val end: LocalTime
) {
    NUIT(LocalTime.of(0, 0), LocalTime.of(4, 59)),
    MATIN(LocalTime.of(5, 0), LocalTime.of(11, 59)),
    JOUR(LocalTime.of(12, 0), LocalTime.of(17, 59)),
    SOIR(LocalTime.of(18, 0), LocalTime.of(23, 59));

    companion object {

        fun fromLocalTime(time: LocalTime): TraceCycle {
            val minutes = time.hour * 60 + time.minute
            return when {
                minutes in (0 * 60 + 0)..(4 * 60 + 59) -> NUIT
                minutes in (5 * 60 + 0)..(11 * 60 + 59) -> MATIN
                minutes in (12 * 60 + 0)..(17 * 60 + 59) -> JOUR
                else -> SOIR
            }
        }

        fun now(): TraceCycle = fromLocalTime(LocalTime.now())

        fun fromKey(key: String?): TraceCycle? = when (key?.trim()?.uppercase()) {
            "NUIT" -> NUIT
            "MATIN" -> MATIN
            "JOUR" -> JOUR
            "SOIR" -> SOIR
            else -> null
        }

        // =========================================================
        // ✅ CLÉS DATE + CYCLE (pour éviter “figé à hier”)
        // =========================================================

        /** Exemple: 2026-02-26__MATIN */
        fun dateCycleKey(date: LocalDate, cycle: TraceCycle): String {
            return "${date}__${cycle.idKey()}"
        }

        fun todayCycleKey(cycle: TraceCycle): String = dateCycleKey(LocalDate.now(), cycle)

        fun nowDateCycleKey(): String = dateCycleKey(LocalDate.now(), now())

        /** Parse "YYYY-MM-DD__CYCLE" -> Pair(date, cycle) */
        fun parseDateCycleKey(key: String?): Pair<LocalDate, TraceCycle>? {
            if (key.isNullOrBlank()) return null
            val parts = key.split("__")
            if (parts.size != 2) return null

            val date = runCatching { LocalDate.parse(parts[0]) }.getOrNull() ?: return null
            val cycle = fromKey(parts[1]) ?: return null
            return date to cycle
        }
    }
}

/** Libellés UI */
fun TraceCycle.labelFr(): String = when (this) {
    TraceCycle.NUIT -> "Nuit"
    TraceCycle.MATIN -> "Matin"
    TraceCycle.JOUR -> "Jour"
    TraceCycle.SOIR -> "Soir"
}

/** Clé stable stockage (cycle seul) */
fun TraceCycle.idKey(): String = when (this) {
    TraceCycle.NUIT -> "NUIT"
    TraceCycle.MATIN -> "MATIN"
    TraceCycle.JOUR -> "JOUR"
    TraceCycle.SOIR -> "SOIR"
}

/** Test fenêtre */
fun TraceCycle.contains(time: LocalTime): Boolean {
    val t = time.hour * 60 + time.minute
    val s = start.hour * 60 + start.minute
    val e = end.hour * 60 + end.minute
    return t in s..e
}

/** Prochain cycle */
fun TraceCycle.next(): TraceCycle = when (this) {
    TraceCycle.NUIT -> TraceCycle.MATIN
    TraceCycle.MATIN -> TraceCycle.JOUR
    TraceCycle.JOUR -> TraceCycle.SOIR
    TraceCycle.SOIR -> TraceCycle.NUIT
}

/** Label plage */
fun TraceCycle.rangeLabelFr(): String {
    fun LocalTime.hhmm(): String = "%02d:%02d".format(hour, minute)
    return "${start.hhmm()}–${end.hhmm()}"
}