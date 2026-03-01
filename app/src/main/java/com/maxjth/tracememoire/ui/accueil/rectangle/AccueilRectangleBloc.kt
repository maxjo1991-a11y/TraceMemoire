// FILE: ui/accueil/rectangle/AccueilRectangleBloc.kt
package com.maxjth.tracememoire.ui.accueil.rectangle

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.accueil.rectangle.model.AccueilCycleUiModel

@Composable
fun AccueilRectangleBloc(
    pMatin: Int?,
    pJour: Int?,
    pSoir: Int?,
    pNuit: Int?,
    dotsForCycleKey: (String) -> List<Boolean>,
    modifier: Modifier = Modifier
) {

    val cycles = listOf(
        AccueilCycleUiModel.from("Matin", pMatin, dotsForCycleKey("MATIN")),
        AccueilCycleUiModel.from("Jour", pJour, dotsForCycleKey("JOUR")),
        AccueilCycleUiModel.from("Soir", pSoir, dotsForCycleKey("SOIR")),
        AccueilCycleUiModel.from("Nuit", pNuit, dotsForCycleKey("NUIT"))
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        AccueilRectangleGrid(
            cycles = cycles,
            modifier = Modifier.fillMaxWidth()
        )
    }
}




