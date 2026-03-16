package com.maxjth.tracememoire.ui.tracejour.components.screen.header

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
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
    val shape = RoundedCornerShape(22.dp)
    val segmentShape = RoundedCornerShape(18.dp)

    val essentialSelected = selectedMode == TraceJourDisplayMode.ESSENTIEL
    val premiumSelected = selectedMode == TraceJourDisplayMode.PREMIUM

    val essentialInteraction = remember { MutableInteractionSource() }
    val premiumInteraction = remember { MutableInteractionSource() }

    val animSpec = tween<Color>(
        durationMillis = 260,
        easing = FastOutSlowInEasing
    )

    val essentialBgStart by animateColorAsState(
        targetValue = if (essentialSelected) TURQUOISE.copy(alpha = 0.24f) else Color.Transparent,
        animationSpec = animSpec,
        label = "essential_bg_start"
    )

    val essentialBgEnd by animateColorAsState(
        targetValue = if (essentialSelected) TURQUOISE.copy(alpha = 0.10f) else Color.Transparent,
        animationSpec = animSpec,
        label = "essential_bg_end"
    )

    val premiumBgStart by animateColorAsState(
        targetValue = if (premiumSelected) MAUVE.copy(alpha = 0.24f) else Color.Transparent,
        animationSpec = animSpec,
        label = "premium_bg_start"
    )

    val premiumBgEnd by animateColorAsState(
        targetValue = if (premiumSelected) MAUVE.copy(alpha = 0.10f) else Color.Transparent,
        animationSpec = animSpec,
        label = "premium_bg_end"
    )

    val essentialTextColor by animateColorAsState(
        targetValue = if (essentialSelected) WHITE_SOFT else WHITE_SOFT.copy(alpha = 0.78f),
        animationSpec = animSpec,
        label = "essential_text"
    )

    val premiumTextColor by animateColorAsState(
        targetValue = if (premiumSelected) WHITE_SOFT else WHITE_SOFT.copy(alpha = 0.78f),
        animationSpec = animSpec,
        label = "premium_text"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        BG_SOFT.copy(alpha = 0.92f),
                        BG_SOFT.copy(alpha = 0.98f)
                    )
                ),
                shape = shape
            )
            .border(
                width = 1.2.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        TURQUOISE.copy(alpha = 0.45f),
                        MAUVE.copy(alpha = 0.45f)
                    )
                ),
                shape = shape
            )
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(essentialBgStart, essentialBgEnd)
                        ),
                        shape = segmentShape
                    )
                    .clickable(
                        interactionSource = essentialInteraction,
                        indication = null
                    ) {
                        onModeChange(TraceJourDisplayMode.ESSENTIEL)
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Essentiel",
                    color = essentialTextColor,
                    fontSize = 16.sp,
                    fontWeight = if (essentialSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(premiumBgStart, premiumBgEnd)
                        ),
                        shape = segmentShape
                    )
                    .clickable(
                        interactionSource = premiumInteraction,
                        indication = null
                    ) {
                        onModeChange(TraceJourDisplayMode.PREMIUM)
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Premium",
                    color = premiumTextColor,
                    fontSize = 16.sp,
                    fontWeight = if (premiumSelected) FontWeight.SemiBold else FontWeight.Medium
                )
            }
        }
    }
}

