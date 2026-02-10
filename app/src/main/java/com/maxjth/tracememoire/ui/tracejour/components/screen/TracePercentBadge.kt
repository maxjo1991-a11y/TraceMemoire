// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/common/TracePercentBadge.kt
package com.maxjth.tracememoire.ui.tracejour.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TracePercentBadge(
    percent: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    sizeDp: Int = 44
) {
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(BG_SOFT.copy(alpha = 0.35f))
            .border(
                width = 2.dp,
                color = MAUVE.copy(alpha = if (enabled) 0.90f else 0.35f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$percent%",
            color = WHITE_SOFT.copy(alpha = if (enabled) 0.95f else 0.55f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}