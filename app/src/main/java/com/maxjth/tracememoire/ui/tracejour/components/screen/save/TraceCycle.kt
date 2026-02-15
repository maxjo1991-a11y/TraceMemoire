// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/save/TraceCycle.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.save

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
enum class TraceCycle {
    NUIT,
    MATIN,
    JOUR,
    SOIR;

    companion object {

        fun fromLocalTime(time: LocalTime): TraceCycle {
            val h = time.hour
            val m = time.minute

            // On compare en minutes depuis minuit pour être clean
            val minutes = h * 60 + m

            return when {
                minutes in (0 * 60 + 0)..(4 * 60 + 59) -> NUIT
                minutes in (5 * 60 + 0)..(11 * 60 + 59) -> MATIN
                minutes in (12 * 60 + 0)..(17 * 60 + 59) -> JOUR
                else -> SOIR // 18:00..23:59
            }
        }

        fun now(): TraceCycle = fromLocalTime(LocalTime.now())
    }
}

/**
 * Libellés UI (sans accent bizarre en enum)
 * -> Tu peux afficher : "Sauvegardé - Matin 10:42"
 */
fun TraceCycle.labelFr(): String = when (this) {
    TraceCycle.NUIT -> "Nuit"
    TraceCycle.MATIN -> "Matin"
    TraceCycle.JOUR -> "Jour"
    TraceCycle.SOIR -> "Soir"
}