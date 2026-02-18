package com.maxjth.tracememoire.ui.home.logic

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object HomeCycleTickerLogic {

    enum class CycleId { NUIT, MATIN, JOUR, SOIR }

    data class HomeSnapshot(
        val now: LocalDateTime,
        val completedToday: Set<CycleId>,
        val lastPercent: Int?,
        val lastCycleSaved: CycleId?
    )

    data class HomeTickerUi(
        val title: String,
        val subtitle: String,
        val rightHint: String
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

    private fun buildUi(s: HomeSnapshot, index: Int): HomeTickerUi {

        val total = 4
        val done = s.completedToday.size.coerceIn(0, total)
        val right = "$done/$total"

        val current = cycleForTime(s.now)

        val lines = buildList {

            // ─────────────────────────────
            // TEMPS / MOMENT
            // ─────────────────────────────
            addAll(
                listOf(
                    "Cycle en cours.",
                    "Fenêtre active.",
                    "Le temps avance.",
                    "Intervalle ouvert.",
                    "Période stable.",
                    "Transition proche.",
                    "Cycle inchangé.",
                    "Nouveau segment temporel.",
                    "Phase actuelle maintenue.",
                    "Temps enregistré."
                )
            )

            // ─────────────────────────────
            // CYCLES
            // ─────────────────────────────
            add("Cycle ${labelFr(current)} actif.")

            if (current !in s.completedToday) {
                add("Cycle ouvert.")
                add("Cycle disponible.")
            } else {
                add("Cycle complété.")
                add("Cycle verrouillé.")
            }

            add("Prochain cycle prêt.")
            add("Bascule temporelle proche.")

            // ─────────────────────────────
            // MÉMOIRE / TRACE
            // ─────────────────────────────
            if (done == 0) {
                add("Aucune trace enregistrée.")
                add("Nouvelle entrée possible.")
            } else {
                add("Une trace existe aujourd’hui.")
                add("Trace détectée.")
                add("Mémoire du jour en évolution.")
            }

            addAll(
                listOf(
                    "Donnée mémorisée.",
                    "Dernière trace conservée.",
                    "Historique stable.",
                    "Séquence mémorisée.",
                    "État enregistré.",
                    "Aucune modification récente.",
                    "Mémoire intacte."
                )
            )

            // ─────────────────────────────
            // NUMÉRIQUE / DONNÉES
            // ─────────────────────────────
            if (s.lastPercent != null) {
                add("Dernier score : ${s.lastPercent} %.")
                add("Valeur enregistrée.")
                add("Progression mémorisée.")
                add("Lecture disponible.")
            } else {
                addAll(
                    listOf(
                        "Donnée valide.",
                        "Valeur stable.",
                        "Aucune variation.",
                        "Mesure conservée."
                    )
                )
            }

            // ─────────────────────────────
            // ÉTATS NEUTRES / SYSTÈME
            // ─────────────────────────────
            addAll(
                listOf(
                    "Rien de nouveau.",
                    "Observation en cours.",
                    "État actuel maintenu.",
                    "Aucun événement mémorisé.",
                    "Cycle normal.",
                    "Activité stable.",
                    "Signal inchangé.",
                    "Donnée présente.",
                    "Aucune donnée récente.",
                    "Lecture calme."
                )
            )

            // ─────────────────────────────
            // Progression stable (clé UX très forte)
            // ─────────────────────────────
            val percent = done * 25
            add("Cycles complétés : $right ($percent%).")

            if (s.lastCycleSaved != null) {
                val t = s.now.format(DateTimeFormatter.ofPattern("HH:mm"))
                add("Dernière sauvegarde : ${labelFr(s.lastCycleSaved)} à $t.")
            }
        }

        val subtitle = lines[index % lines.size]

        return HomeTickerUi(
            title = "Cycle ACTIF",
            subtitle = subtitle,
            rightHint = right
        )
    }

    // ✅ VERSION TEMPORELLE PROPRE (sans bug)
    private fun cycleForTime(now: LocalDateTime): CycleId {
        val minutes = now.hour * 60 + now.minute

        return when {
            minutes in 0..299 -> CycleId.NUIT        // 00:00 → 04:59
            minutes in 300..719 -> CycleId.MATIN     // 05:00 → 11:59
            minutes in 720..1079 -> CycleId.JOUR     // 12:00 → 17:59
            else -> CycleId.SOIR                     // 18:00 → 23:59
        }
    }

    private fun labelFr(id: CycleId): String = when (id) {
        CycleId.NUIT -> "Nuit"
        CycleId.MATIN -> "Matin"
        CycleId.JOUR -> "Jour"
        CycleId.SOIR -> "Soir"
    }
}