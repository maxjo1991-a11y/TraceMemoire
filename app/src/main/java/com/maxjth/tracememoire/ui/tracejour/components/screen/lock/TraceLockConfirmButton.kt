package com.maxjth.tracememoire.ui.tracejour.components.screen.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TraceLockConfirmButton(
    text: String = "Confirmer le verrouillage",
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    val alphaBoost = if (pressed && enabled) 0.55f else 0.35f
    val borderAlpha = if (enabled) 0.40f else 0.18f
    val textAlpha = if (enabled) 0.96f else 0.35f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp) // ✅ légèrement plus confortable au doigt
            .clip(shape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        TURQUOISE.copy(alpha = if (enabled) alphaBoost else 0.10f),
                        MAUVE.copy(alpha = if (enabled) alphaBoost else 0.10f)
                    )
                )
            )
            .border(
                width = 1.4.dp,
                color = WHITE_SOFT.copy(alpha = borderAlpha),
                shape = shape
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = WHITE_SOFT.copy(alpha = textAlpha),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}