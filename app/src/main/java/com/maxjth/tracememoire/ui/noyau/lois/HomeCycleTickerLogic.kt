package com.maxjth.tracememoire.ui.noyau.lois

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object HomeCycleTickerLogic {

    enum class CycleId { NUIT, MATIN, JOUR, SOIR }

    data class HomeSnapshot(
        val now: LocalDateTime,

        // ✅ Toujours utile pour Soleil
        val completedToday: Set<CycleId>,

        // ✅ Devient multi-échelle (plus "jour uniquement")
        val lastPercent: Int?,

        // ✅ Important pour cohérence temporelle
        val lastCycleSaved: CycleId?,

        // ✅ NOUVEAU : Score annuel Terre
        val yearlyPercent: Int?
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

        val current = cycleForTime(s.now)

        val lines = buildList {

            // ─────────────────────────────
            // TEMPS
            // ─────────────────────────────
            add("Cycle ${labelFr(current)} actif.")
            add("Fenêtre temporelle stable.")
            add("Phase en cours maintenue.")
            add("Transition temporelle normale.")
            add("Intervalle actif.")
            add("Temps en progression.")

            // ─────────────────────────────
            // JOUR (Soleil)
            // ─────────────────────────────
            val done = s.completedToday.size.coerceIn(0, 4)
            add("Cycles du jour : $done/4.")

            if (s.lastPercent != null) {
                add("Score courant : ${s.lastPercent} %.")
                add("Lecture du jour disponible.")
            } else {
                add("Aucune mesure active.")
            }

            // ─────────────────────────────
            // ANNÉE (Terre)
            // ─────────────────────────────
            if (s.yearlyPercent != null) {
                add("Tendance annuelle : ${s.yearlyPercent} %.")
                add("Lecture globale stable.")
            } else {
                add("Aucune lecture annuelle.")
            }

            // ─────────────────────────────
            // MÉMOIRE / ÉTAT
            // ─────────────────────────────
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
            rightHint = labelFr(current)
        )
    }

    private fun cycleForTime(now: LocalDateTime): CycleId {
        val minutes = now.hour * 60 + now.minute

        return when {
            minutes in 0..299 -> CycleId.NUIT
            minutes in 300..719 -> CycleId.MATIN
            minutes in 720..1079 -> CycleId.JOUR
            else -> CycleId.SOIR
        }
    }

    private fun labelFr(id: CycleId): String = when (id) {
        CycleId.NUIT -> "Nuit"
        CycleId.MATIN -> "Matin"
        CycleId.JOUR -> "Jour"
        CycleId.SOIR -> "Soir"
    }
}