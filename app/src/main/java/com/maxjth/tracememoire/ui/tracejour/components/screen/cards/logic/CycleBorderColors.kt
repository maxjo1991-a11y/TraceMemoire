package com.maxjth.tracememoire.ui.tracejour.components.screen.cards.logic

import androidx.compose.ui.graphics.Color
import com.maxjth.tracememoire.ui.systeme.cyclecolors.CycleColors
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE

fun cycleBorderColors(
    activeCycleKey: String?,
    isHero: Boolean
): Pair<Color, Color> {

    val alphaStart = if (isHero) 0.88f else 0.80f
    val alphaEnd = if (isHero) 0.72f else 0.64f

    return when (activeCycleKey?.trim()?.uppercase()) {

        "MATIN" -> Pair(
            CycleColors.MatinStart.copy(alpha = alphaStart),
            CycleColors.MatinEnd.copy(alpha = alphaEnd)
        )

        "JOUR" -> Pair(
            CycleColors.JourStart.copy(alpha = alphaStart),
            CycleColors.JourEnd.copy(alpha = alphaEnd)
        )

        "SOIR" -> Pair(
            CycleColors.SoirStart.copy(alpha = alphaStart),
            CycleColors.SoirEnd.copy(alpha = alphaEnd)
        )

        "NUIT" -> Pair(
            CycleColors.NuitStart.copy(alpha = alphaStart),
            CycleColors.NuitEnd.copy(alpha = alphaEnd)
        )

        else -> Pair(
            TURQUOISE.copy(alpha = alphaStart),
            MAUVE.copy(alpha = alphaEnd)
        )
    }
}

