package com.maxjth.tracememoire.ui.accueil.rectangle.model

data class AccueilCycleUiModel(
    val label: String,
    val percentForBar: Int,
    val dots: List<Boolean>,
    val rightText: String,
    val midText: String
) {
    companion object {
        fun from(
            label: String,
            value: Int?,
            dots: List<Boolean>
        ): AccueilCycleUiModel {

            val percent = value ?: 0
            val isSaved = value != null

            return AccueilCycleUiModel(
                label = label,
                percentForBar = percent,
                dots = dots,
                rightText = if (isSaved) "$percent / 100" else "— / 100",
                midText = if (isSaved) "Mémoire créée" else "Mémoire absente"
            )
        }
    }
}