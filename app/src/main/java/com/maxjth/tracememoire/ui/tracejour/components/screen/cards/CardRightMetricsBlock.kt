package com.maxjth.tracememoire.ui.tracejour.components.screen.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.maxjth.tracememoire.ui.tracejour.components.screen.dots.TraceDotState
import com.maxjth.tracememoire.ui.tracejour.components.screen.dots.TraceStatusDot
import com.maxjth.tracememoire.ui.tracejour.components.screen.percent.PercentBadge

@Composable
internal fun CardRightMetricsBlock(
    shownPercent: Int?,
    dotState: TraceDotState,
    rightTopPadding: Dp,
    isHero: Boolean
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .padding(top = rightTopPadding)
            .widthIn(min = 66.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (shownPercent != null) {
                PercentBadge(
                    percent = shownPercent,
                    fontSizeSp = 17
                )
            }

            TraceStatusDot(
                state = dotState,
                glowSize = if (isHero) 18.dp else 16.dp
            )
        }
    }
}

