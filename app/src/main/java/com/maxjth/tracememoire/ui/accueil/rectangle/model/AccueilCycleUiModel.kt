// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/rectangle/model/AccueilCycleUiModel.kt
package com.maxjth.tracememoire.ui.accueil.rectangle.model

import java.time.Instant
import java.time.ZoneId

data class AccueilCycleUiModel(
    val label: String,

    // ✅ cycle clé (MATIN/JOUR/SOIR/NUIT) pour brancher activeCycle + createdAt
    val cycleKey: String,

    // ✅ permet à la Row de mettre le cycle actif en avant
    val isActive: Boolean,

    // barre
    val percentForBar: Int,
    val dots: List<Boolean>,

    // textes
    val rightText: String,
    val midText: String,

    // ✅ heure de création si dispo
    val createdAtMillis: Long?
) {
    companion object {

        /**
         * ✅ Version principale
         * - percent null => mémoire absente
         * - percent != null + createdAtMillis != null => "Mémoire créée • HH:mm"
         */
        fun from(
            label: String,
            cycleKey: String,
            percent: Int?,
            dots: List<Boolean>,
            createdAtMillis: Long? = null,
            isActive: Boolean = false
        ): AccueilCycleUiModel {

            val safePercent = percent?.coerceIn(0, 100)
            val isSaved = safePercent != null

            val rightText = if (isSaved) "${safePercent} / 100" else "— / 100"

            val timeText = if (isSaved && createdAtMillis != null) {
                formatHourMinute(createdAtMillis) // ex: "14:32"
            } else null

            val midText = if (isSaved) {
                if (timeText != null) "Mémoire créée • $timeText" else "Mémoire créée"
            } else {
                "Mémoire absente"
            }

            return AccueilCycleUiModel(
                label = label,
                cycleKey = cycleKey.trim().uppercase(),
                isActive = isActive,
                percentForBar = safePercent ?: 0,
                dots = dots,
                rightText = rightText,
                midText = midText,
                createdAtMillis = createdAtMillis
            )
        }

        /**
         * ✅ Compat (ancien appel)
         * - évite de casser le code pendant que tu migres le reste
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
                isActive = false
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