package com.maxjth.tracememoire.ui.tracejour.components.screen.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

/**
 * ✅ Bottom nav SAFE (pas de ripple, pas de legacy)
 * - 3 actions : Retour / Historique / Approfondir
 * - style calme, premium, non agressif
 */
@Composable
fun TraceBottomNavBar(
    onBack: () -> Unit,
    onHistory: () -> Unit,
    onDeepen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        BG_DEEP.copy(alpha = 0.70f),
                        BG_DEEP.copy(alpha = 0.45f),
                        BG_DEEP.copy(alpha = 0.70f)
                    )
                ),
                shape = shape
            )
            .border(1.dp, MAUVE.copy(alpha = 0.20f), shape)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        NavPill(
            text = "Retour",
            toneColor = WHITE_SOFT.copy(alpha = 0.85f),
            onClick = onBack
        )

        Spacer(Modifier.width(10.dp))

        NavPill(
            text = "Historique",
            toneColor = WHITE_SOFT.copy(alpha = 0.85f),
            onClick = onHistory
        )

        Spacer(Modifier.width(10.dp))

        NavPill(
            text = "Approfondir",
            toneColor = TURQUOISE.copy(alpha = 0.90f),
            onClick = onDeepen
        )
    }
}

@Composable
private fun NavPill(
    text: String,
    toneColor: Color,
    onClick: () -> Unit
) {
    val pillShape = RoundedCornerShape(999.dp)

    Row(
        modifier = Modifier
            .height(36.dp)
            .background(
                color = BG_DEEP.copy(alpha = 0.22f),
                shape = pillShape
            )
            .border(1.dp, MAUVE.copy(alpha = 0.18f), pillShape)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            // ✅ CLICKABLE SAFE: interactionSource + indication=null
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = toneColor,
            letterSpacing = 0.2.sp
        )
    }
}