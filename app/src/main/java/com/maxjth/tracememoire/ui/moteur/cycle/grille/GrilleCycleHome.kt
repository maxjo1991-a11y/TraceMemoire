package com.maxjth.tracememoire.ui.moteur.cycle.grille

import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Définit les bornes horaires fixes des cycles HOME.
 *
 * Découpage verrouillé :
 * - Nuit  : 00:00 - 05:59
 * - Matin : 06:00 - 11:59
 * - Jour  : 12:00 - 17:59
 * - Soir  : 18:00 - 23:59
 */
object GrilleCycleHome {

    private val nuitStart = LocalTime.of(0, 0)
    private val matinStart = LocalTime.of(6, 0)
    private val jourStart = LocalTime.of(12, 0)
    private val soirStart = LocalTime.of(18, 0)

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
            time >= soirStart -> TypeCycleHome.SOIR
            time >= jourStart -> TypeCycleHome.JOUR
            time >= matinStart -> TypeCycleHome.MATIN
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
