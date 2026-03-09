// FILE: app/src/main/java/com/maxjth/tracememoire/ui/moteur/cycle/grille/GrilleCycleHome.kt
package com.maxjth.tracememoire.ui.moteur.cycle.grille

import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Définit les bornes horaires fixes des cycles HOME.
 *
 * IMPORTANT :
 * - Cette classe ne connaît pas le stockage.
 * - Elle ne connaît pas le moteur.
 * - Elle ne fait que découper le temps.
 */
object GrilleCycleHome {

    private val matinStart = LocalTime.of(5, 0)
    private val jourStart = LocalTime.of(10, 0)
    private val soirStart = LocalTime.of(17, 0)
    private val nuitStart = LocalTime.of(22, 0)

    /**
     * Cycles actifs (HOME = 4 cycles).
     */
    fun activeCycles(): List<TypeCycleHome> {
        return listOf(
            TypeCycleHome.NUIT,
            TypeCycleHome.MATIN,
            TypeCycleHome.JOUR,
            TypeCycleHome.SOIR
        )
    }

    /**
     * Retourne le cycle correspondant à l'heure donnée.
     */
    fun resolve(time: LocalTime): TypeCycleHome {
        return when {
            time >= nuitStart || time < matinStart -> TypeCycleHome.NUIT
            time >= matinStart && time < jourStart -> TypeCycleHome.MATIN
            time >= jourStart && time < soirStart -> TypeCycleHome.JOUR
            time >= soirStart && time < nuitStart -> TypeCycleHome.SOIR
            else -> TypeCycleHome.NUIT
        }
    }

    /**
     * Overload pratique : LocalDateTime -> LocalTime.
     */
    fun resolve(now: LocalDateTime): TypeCycleHome {
        return resolve(now.toLocalTime())
    }
}