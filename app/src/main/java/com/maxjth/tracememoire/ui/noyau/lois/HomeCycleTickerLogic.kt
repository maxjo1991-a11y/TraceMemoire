// FILE: app/src/main/java/com/maxjth/tracememoire/ui/noyau/lois/HomeCycleTickerLogic.kt
package com.maxjth.tracememoire.ui.noyau.lois

import com.maxjth.tracememoire.ui.moteur.cycle.grille.GrilleCycleHome
import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import com.maxjth.tracememoire.ui.time.currentMonthlyBreath
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object HomeCycleTickerLogic {

    enum class CycleState {
        ETAT_1, // Nouveau
        ETAT_2, // En cours
        ETAT_3  // Complété
    }

    data class HomeSnapshot(
        val now: LocalDateTime,
        val completedToday: Set<TypeCycleHome>,
        val lastPercent: Int?,
        val lastCycleSaved: TypeCycleHome?,
        val yearlyPercent: Int?
    )

    data class HomeTickerUi(
        val title: String,
        val subtitle: String,
        val rightHint: String,
        val cycleState: CycleState,
        val cycleStateLabel: String,
        val currentCycle: TypeCycleHome
    )

    fun tickerFlow(
        intervalMs: Long,
        provider: () -> HomeSnapshot
    ): Flow<HomeTickerUi> = flow {
        var index = 0
        while (true) {
            val snap = provider()
            emit(buildUi(snap, index))
            index++
            delay(intervalMs.coerceAtLeast(2_000L))
        }
    }

    private fun buildUi(
        s: HomeSnapshot,
        index: Int
    ): HomeTickerUi {

        // ✅ La grille est la seule source de vérité
        val current = GrilleCycleHome.resolve(s.now)
        val activeCycles = GrilleCycleHome.activeCycles()

        val doneCount = s.completedToday
            .count { it in activeCycles }
            .coerceIn(0, activeCycles.size)

        // ✅ FIX : "complété" seulement si confirmé par la dernière sauvegarde
        val state = cycleStateForCurrent(
            current = current,
            completedToday = s.completedToday,
            lastCycleSaved = s.lastCycleSaved
        )

        // ✅ BRANCHEMENT TIME (MonthlyBreath)
        // Ça rend currentMonthlyBreath() "utilisé" + on peut s'en servir comme rythme mensuel.
        val breath = currentMonthlyBreath()

        val lines = buildList {
            add("Cycle ${labelFr(current)} actif.")
            add("Fenêtre temporelle stable.")
            add("Phase en cours maintenue.")
            add("Transition temporelle normale.")
            add("Intervalle actif.")
            add("Temps en progression.")

            add("Cycles du jour : $doneCount/${activeCycles.size}.")

            when (state) {
                CycleState.ETAT_1 -> add("Nouveau cycle disponible.")
                CycleState.ETAT_2 -> add("Cycle en cours.")
                CycleState.ETAT_3 -> add("Cycle complété.")
            }

            if (s.lastPercent != null) {
                add("Score courant : ${s.lastPercent} %.")
                add("Lecture du jour disponible.")
            } else {
                add("Aucune mesure active.")
            }

            if (s.yearlyPercent != null) {
                add("Tendance annuelle : ${s.yearlyPercent} %.")
                add("Lecture globale stable.")
            } else {
                add("Aucune lecture annuelle.")
            }

            // ✅ On utilise le "breath" (sinon Android Studio le re-grise plus tard)
            add("Rythme du mois : ${breath.minScale} → ${breath.maxScale}.")
            add("Durée respiration : ${breath.durationMs} ms.")

            add("Historique conservé.")
            add("Séquence mémorisée.")
            add("État enregistré.")
            add("Lecture calme.")

            if (s.lastCycleSaved != null) {
                val t = s.now.format(DateTimeFormatter.ofPattern("HH:mm"))
                add("Dernière sauvegarde : ${labelFr(s.lastCycleSaved)} à $t.")
            }
        }

        val subtitle = lines[index % lines.size]

        return HomeTickerUi(
            title = "Cycle ACTIF",
            subtitle = subtitle,
            rightHint = labelFr(current),
            cycleState = state,
            cycleStateLabel = stateLabelFr(state),
            currentCycle = current
        )
    }

    /**
     * ✅ Règle PROPRE du ticker (cycle courant)
     * - ETAT_3 seulement si :
     *   1) current est dans completedToday
     *   2) ET lastCycleSaved == current
     * - Sinon ETAT_2
     */
    private fun cycleStateForCurrent(
        current: TypeCycleHome,
        completedToday: Set<TypeCycleHome>,
        lastCycleSaved: TypeCycleHome?
    ): CycleState {
        val done = completedToday.contains(current)
        val confirmed = (lastCycleSaved == current)
        return if (done && confirmed) CycleState.ETAT_3 else CycleState.ETAT_2
    }

    /**
     * ✅ État pour une ligne du rectangle (Matin/Jour/Soir/Nuit)
     * - ETAT_3 : cycle complété (il a un %)
     * - ETAT_2 : cycle actuel (maintenant) et pas complété
     * - ETAT_1 : le reste
     */
    fun cycleStateForRow(
        now: LocalDateTime,
        cycle: TypeCycleHome,
        completedToday: Set<TypeCycleHome>
    ): CycleState {
        if (completedToday.contains(cycle)) return CycleState.ETAT_3
        val current = GrilleCycleHome.resolve(now)
        return if (cycle == current) CycleState.ETAT_2 else CycleState.ETAT_1
    }

    private fun stateLabelFr(state: CycleState): String = when (state) {
        CycleState.ETAT_1 -> "Nouveau cycle disponible"
        CycleState.ETAT_2 -> "Cycle en cours"
        CycleState.ETAT_3 -> "Cycle complété"
    }

    private fun labelFr(type: TypeCycleHome): String = when (type) {
        TypeCycleHome.NUIT -> "Nuit"
        TypeCycleHome.MATIN -> "Matin"
        TypeCycleHome.JOUR -> "Jour"
        TypeCycleHome.SOIR -> "Soir"
    }
}