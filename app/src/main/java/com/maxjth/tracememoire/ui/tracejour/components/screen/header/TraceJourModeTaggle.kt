package com.maxjth.tracememoire.ui.tracejour.components.screen.header

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

enum class TraceJourDisplayMode {
    ESSENTIEL,
    PREMIUM
}

@Composable
fun TraceJourModeToggle(
    selectedMode: TraceJourDisplayMode,
    onModeChange: (TraceJourDisplayMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerShape = RoundedCornerShape(24.dp)
    val segmentShape = RoundedCornerShape(20.dp)

    val essentialSelected = selectedMode == TraceJourDisplayMode.ESSENTIEL
    val premiumSelected = selectedMode == TraceJourDisplayMode.PREMIUM

    val animSpec = tween<Color>(260, easing = FastOutSlowInEasing)

    val essentialStart by animateColorAsState(
        if (essentialSelected) TURQUOISE.copy(alpha = 0.28f) else Color.Transparent,
        animSpec, label = ""
    )

    val essentialEnd by animateColorAsState(
        if (essentialSelected) TURQUOISE.copy(alpha = 0.08f) else Color.Transparent,
        animSpec, label = ""
    )

    val premiumStart by animateColorAsState(
        if (premiumSelected) MAUVE.copy(alpha = 0.28f) else Color.Transparent,
        animSpec, label = ""
    )

    val premiumEnd by animateColorAsState(
        if (premiumSelected) MAUVE.copy(alpha = 0.08f) else Color.Transparent,
        animSpec, label = ""
    )

    val essentialText by animateColorAsState(
        if (essentialSelected) WHITE_SOFT else WHITE_SOFT.copy(alpha = 0.65f),
        animSpec, label = ""
    )

    val premiumText by animateColorAsState(
        if (premiumSelected) WHITE_SOFT else WHITE_SOFT.copy(alpha = 0.65f),
        animSpec, label = ""
    )

    val essentialInteraction = remember { MutableInteractionSource() }
    val premiumInteraction = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MAUVE.copy(alpha = 0.10f),
                        Color.Black.copy(alpha = 0.80f),
                        TURQUOISE.copy(alpha = 0.08f)
                    )
                ),
                shape = containerShape
            )
            .border(
                width = 1.3.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MAUVE.copy(alpha = 0.55f),
                        TURQUOISE.copy(alpha = 0.55f)
                    )
                ),
                shape = containerShape
            )
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            // ESSENTIEL
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(essentialStart, essentialEnd)
                        ),
                        shape = segmentShape
                    )
                    .clickable(
                        interactionSource = essentialInteraction,
                        indication = null
                    ) {
                        onModeChange(TraceJourDisplayMode.ESSENTIEL)
                    }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Essentiel",
                    color = essentialText,
                    fontSize = 16.sp,
                    fontWeight = if (essentialSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }

            // PREMIUM
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(premiumStart, premiumEnd)
                        ),
                        shape = segmentShape
                    )
                    .clickable(
                        interactionSource = premiumInteraction,
                        indication = null
                    ) {
                        onModeChange(TraceJourDisplayMode.PREMIUM)
                    }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Premium",
                    color = premiumText,
                    fontSize = 16.sp,
                    fontWeight = if (premiumSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}