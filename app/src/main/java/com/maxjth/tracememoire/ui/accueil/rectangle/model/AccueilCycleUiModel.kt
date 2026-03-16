package com.maxjth.tracememoire.ui.accueil.rectangle.model

import java.time.Instant
import java.time.ZoneId
import kotlin.math.roundToInt

data class AccueilCycleUiModel(
    val label: String,

    // cycle clé (MATIN/JOUR/SOIR/NUIT) pour brancher activeCycle + createdAt
    val cycleKey: String,

    // permet à la Row de mettre le cycle actif en avant
    val isActive: Boolean,

    // barre visuelle en %
    val percentForBar: Int,
    val dots: List<Boolean>,

    // textes
    val rightText: String,
    val midText: String,
    val trajectoryText: String?,

    // heure de création si dispo
    val createdAtMillis: Long?
) {
    companion object {

        private const val CYCLE_MAX = 400

        /**
         * Version principale
         * - cycleValue null => mémoire absente
         * - cycleValue != null + createdAtMillis != null => "Mémoire créée • HH:mm"
         * - un cycle complet vaut 0..400
         * - la barre reste en 0..100%
         */
        fun from(
            label: String,
            cycleKey: String,
            percent: Int?,
            dots: List<Boolean>,
            createdAtMillis: Long? = null,
            isActive: Boolean = false,
            trajectoryText: String? = null
        ): AccueilCycleUiModel {

            val safeCycleValue = percent?.coerceIn(0, CYCLE_MAX)
            val isSaved = safeCycleValue != null

            val percentForBar = if (safeCycleValue != null) {
                ((safeCycleValue / CYCLE_MAX.toFloat()) * 100f)
                    .roundToInt()
                    .coerceIn(0, 100)
            } else {
                0
            }

            val rightText = if (isSaved) {
                safeCycleValue.toString()
            } else {
                "—"
            }

            val timeText = if (isSaved && createdAtMillis != null) {
                formatHourMinute(createdAtMillis)
            } else {
                null
            }

            val midText = if (isSaved) {
                if (timeText != null) "Empreinte enregistrée • $timeText" else "Empreinte enregistrée"
            } else {
                "Aucune empreinte"
            }

            return AccueilCycleUiModel(
                label = label,
                cycleKey = cycleKey.trim().uppercase(),
                isActive = isActive,
                percentForBar = percentForBar,
                dots = dots,
                rightText = rightText,
                midText = midText,
                trajectoryText = if (isSaved) trajectoryText else null,
                createdAtMillis = createdAtMillis
            )
        }

        /**
         * Compat (ancien appel)
         */
        fun from(
            label: String,
            value: Int?,
            dots: List<Boolean>
        ): AccueilCycleUiModel {
            val fallbackKey = label.trim().uppercase()
            return from(
                label = label,
                cycleKey = fallbackKey,
                percent = value,
                dots = dots,
                createdAtMillis = null,
                isActive = false,
                trajectoryText = null
            )
        }

        private fun formatHourMinute(millis: Long): String {
            val zone = ZoneId.systemDefault()
            val dt = Instant.ofEpochMilli(millis).atZone(zone)
            val h = dt.hour.toString().padStart(2, '0')
            val m = dt.minute.toString().padStart(2, '0')
            return "$h:$m"
        }
    }
}