// FILE: app/src/main/java/com/maxjth/tracememoire/ui/moteur/cycle/liaison/CycleFacadeHome.kt
package com.maxjth.tracememoire.ui.moteur.cycle.liaison

import com.maxjth.tracememoire.ui.moteur.cycle.logique.ResolutionCycleHome
import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import java.time.LocalDateTime

/**
 * Facade HOME : point d'entrée unique pour la résolution des cycles HOME.
 *
 * Objectif :
 * - HomeScreen et le reste ne manipulent plus de Strings "MATIN/JOUR/SOIR/NUIT".
 * - Tout passe par TypeCycleHome + ResolutionCycleHome.
 */
class CycleFacadeHome(
    private val resolver: ResolutionCycleHome = ResolutionCycleHome()
) {

    /**
     * Résout l'état global des cycles HOME.
     */
    fun resolve(
        now: LocalDateTime,
        completedToday: Set<TypeCycleHome>
    ): ResolutionCycleHome.Resolution {
        return resolver.resolve(now = now, completedToday = completedToday)
    }

    /**
     * Helper pratique : convertir une map de stockage (key String -> Int?)
     * en Set<TypeCycleHome> complétés.
     *
     * Exemple : cyclePercentMap du HOME (keys legacy).
     */
    fun completedFromPercentMap(
        cyclePercentMap: Map<String, Int?>
    ): Set<TypeCycleHome> {
        val out = mutableSetOf<TypeCycleHome>()
        for ((key, value) in cyclePercentMap) {
            if (value != null) {
                val type = TypeCycleHome.fromKey(key)
                if (type != null) out.add(type)
            }
        }
        return out
    }
}