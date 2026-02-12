package com.maxjth.tracememoire.ui.tracejour.components.screen.slider

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun PhrasePillFullWidth(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    breathePhase: Float,
    respect: Float
) {
    val bg = BG_SOFT.copy(alpha = 0.16f)
    val border1 = TURQUOISE.copy(alpha = ((0.14f + 0.18f * breathePhase) * respect).coerceIn(0f, 1f))
    val border2 = MAUVE.copy(alpha = ((0.16f + 0.12f * breathePhase) * respect).coerceIn(0f, 1f))
    val glow = MAUVE.copy(alpha = (0.06f + 0.05f * breathePhase) * respect)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(glow, bg, glow)), RoundedCornerShape(999.dp))
            .border(1.6.dp, border2, RoundedCornerShape(999.dp))
            .border(2.0.dp, border1, RoundedCornerShape(999.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = WHITE_SOFT.copy(alpha = if (enabled) 0.90f else 0.55f),
            fontSize = 15.5.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

