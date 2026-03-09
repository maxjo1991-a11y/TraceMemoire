// FILE: app/src/main/java/com/maxjth/tracememoire/ui/moteur/cycle/logique/ResolutionCycleHome.kt
package com.maxjth.tracememoire.ui.moteur.cycle.logique

import com.maxjth.tracememoire.ui.moteur.cycle.grille.GrilleCycleHome
import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import java.time.LocalDateTime

/**
 * Résolution pure des cycles HOME.
 *
 * IMPORTANT :
 * - Pas d'UI.
 * - Pas de stockage.
 * - Pas de Flow.
 *
 * Sert de "source de vérité" pour :
 * - cycle courant
 * - état ETAT_1 / ETAT_2 / ETAT_3 par cycle
 * - doneCount
 */
class ResolutionCycleHome {

    enum class CycleStateHome {
        ETAT_1, // Nouveau (pas encore fait)
        ETAT_2, // En cours (cycle courant)
        ETAT_3  // Complété
    }

    data class Resolution(
        val now: LocalDateTime,
        val currentCycle: TypeCycleHome,
        val activeCycles: List<TypeCycleHome>,
        val completedToday: Set<TypeCycleHome>,
        val statesByCycle: Map<TypeCycleHome, CycleStateHome>,
        val doneCount: Int
    )

    fun resolve(
        now: LocalDateTime,
        completedToday: Set<TypeCycleHome>
    ): Resolution {

        val current = GrilleCycleHome.resolve(now)
        val active = GrilleCycleHome.activeCycles()

        val doneCount = completedToday.count { it in active }.coerceIn(0, active.size)

        val states = buildMap<TypeCycleHome, CycleStateHome> {
            for (cycle in active) {
                val state = when {
                    completedToday.contains(cycle) -> CycleStateHome.ETAT_3
                    cycle == current -> CycleStateHome.ETAT_2
                    else -> CycleStateHome.ETAT_1
                }
                put(cycle, state)
            }
        }

        return Resolution(
            now = now,
            currentCycle = current,
            activeCycles = active,
            completedToday = completedToday,
            statesByCycle = states,
            doneCount = doneCount
        )
    }
}