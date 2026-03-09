// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/cycle/TraceCycle.kt
package com.maxjth.tracememoire.ui.tracejour.cycle

import java.time.LocalDate
import java.time.LocalTime

/**
 * TraceCycle — Cycle verrouillé (Nuit / Matin / Jour / Soir)
 *
 * Règles verrouillées (alignées avec HOME : 05 / 10 / 17 / 22) :
 * - Matin : 05:00 - 09:59
 * - Jour  : 10:00 - 16:59
 * - Soir  : 17:00 - 21:59
 * - Nuit  : 22:00 - 04:59  (traverse minuit)
 */
enum class TraceCycle(
    val start: LocalTime,
    val end: LocalTime
) {
    MATIN(LocalTime.of(5, 0), LocalTime.of(9, 59)),
    JOUR(LocalTime.of(10, 0), LocalTime.of(16, 59)),
    SOIR(LocalTime.of(17, 0), LocalTime.of(21, 59)),
    NUIT(LocalTime.of(22, 0), LocalTime.of(4, 59));

    companion object {

        fun fromLocalTime(time: LocalTime): TraceCycle {
            val minutes = time.hour * 60 + time.minute
            return when {
                minutes in (5 * 60 + 0)..(9 * 60 + 59) -> MATIN
                minutes in (10 * 60 + 0)..(16 * 60 + 59) -> JOUR
                minutes in (17 * 60 + 0)..(21 * 60 + 59) -> SOIR
                else -> NUIT // 22:00–23:59 + 00:00–04:59
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

/** Test fenêtre (gère Nuit qui traverse minuit) */
fun TraceCycle.contains(time: LocalTime): Boolean {
    val t = time.hour * 60 + time.minute
    val s = start.hour * 60 + start.minute
    val e = end.hour * 60 + end.minute

    return if (s <= e) {
        // fenêtre normale
        t in s..e
    } else {
        // traverse minuit (ex: 22:00 -> 04:59)
        t >= s || t <= e
    }
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