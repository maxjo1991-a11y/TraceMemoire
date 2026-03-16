package com.maxjth.tracememoire.ui.systeme.saturne

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.systeme.Galaxie.GalaxyBackground
import com.maxjth.tracememoire.ui.systeme.Galaxie.GalaxyOrbitLines
import com.maxjth.tracememoire.ui.systeme.cyclecolors.CycleColors

@Composable
fun SaturneCircle(
    memoire: Int,
    valeurHier: Int,
    valeurMaintenant: Int,
    activeCycleKey: String? = null,
    reactivePercent: Int = 50,
    modifier: Modifier = Modifier
) {
    val (ringStartColor, ringEndColor) = cycleRingColors(activeCycleKey)

    Box(
        modifier = modifier.size(320.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
        ) {
            GalaxyBackground(
                modifier = Modifier.matchParentSize()
            )

            GalaxyOrbitLines(
                modifier = Modifier.matchParentSize(),
                percent = reactivePercent
            )
        }

        SaturneCanvas(
            ringStartColor = ringStartColor,
            ringEndColor = ringEndColor,
            modifier = Modifier.matchParentSize()
        )

        SaturneLayout(
            memoire = memoire,
            valeurHier = valeurHier,
            valeurJournee = valeurMaintenant,
            modifier = Modifier.matchParentSize()
        )
    }
}

private fun cycleRingColors(activeCycleKey: String?): Pair<Color, Color> {
    return when (activeCycleKey?.trim()?.uppercase()) {
        "MATIN" -> CycleColors.MatinStart to CycleColors.MatinEnd
        "JOUR" -> CycleColors.JourStart to CycleColors.JourEnd
        "SOIR" -> CycleColors.SoirStart to CycleColors.SoirEnd
        "NUIT" -> CycleColors.NuitStart to CycleColors.NuitEnd
        else -> CycleColors.JourStart to CycleColors.JourEnd
    }
}