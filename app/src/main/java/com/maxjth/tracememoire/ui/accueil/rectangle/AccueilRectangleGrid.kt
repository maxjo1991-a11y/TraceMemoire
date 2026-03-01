package com.maxjth.tracememoire.ui.accueil.rectangle

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maxjth.tracememoire.ui.accueil.rectangle.model.AccueilCycleUiModel
import com.maxjth.tracememoire.ui.accueil.rectangle.row.AccueilRectangleRow
import com.maxjth.tracememoire.ui.accueil.rectangle.row.AccueilRectangleRowDivider


@Composable
fun AccueilRectangleGrid(
    cycles: List<AccueilCycleUiModel>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        cycles.forEachIndexed { index, ui ->

            AccueilRectangleRow(ui = ui)

            if (index != cycles.lastIndex) {
                AccueilRectangleRowDivider()
            }
        }
    }
}