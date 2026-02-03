package com.maxjth.tracememoire.ui.tags

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TagChip(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    mauve: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)

    val bg = if (selected) mauve.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.06f)
    val border = if (selected) mauve.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.08f)
    val label = if (selected) textColor.copy(alpha = 0.95f) else textColor.copy(alpha = 0.86f)

    Row(
        modifier = Modifier
            .defaultMinSize(minHeight = 36.dp)
            .clip(shape)
            .background(bg)
            .border(BorderStroke(1.dp, border), shape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null // ✅ look calme (pas de ripple agressif)
            ) { onClick() }
            .padding(PaddingValues(horizontal = 14.dp, vertical = 8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = label
        )
    }
}