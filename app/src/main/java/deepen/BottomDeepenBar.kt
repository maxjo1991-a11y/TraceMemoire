package com.maxjth.tracememoire.ui.deepen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomDeepenBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(999.dp)

    val alpha = if (enabled) 0.85f else 0.35f
    val borderAlpha = if (enabled) 0.14f else 0.08f
    val textAlpha = if (enabled) 0.92f else 0.45f

    val bgBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF0E3B3A).copy(alpha = alpha),
            Color(0xFF2A1740).copy(alpha = alpha)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .background(bgBrush)
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = borderAlpha),
                shape = shape
            )
            // ✅ clickable simple = le plus safe
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Approfondir la mémoire →",
            color = Color.White.copy(alpha = textAlpha),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}