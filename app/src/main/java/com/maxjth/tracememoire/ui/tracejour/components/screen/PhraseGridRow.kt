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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun PhraseGridRow(
    phrase: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val rowBg = if (selected) {
        TURQUOISE.copy(alpha = 0.10f)
    } else {
        BG_SOFT.copy(alpha = 0.10f)
    }

    val rowBorder = if (selected) {
        TURQUOISE.copy(alpha = 0.45f)
    } else {
        MAUVE.copy(alpha = 0.14f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg, RoundedCornerShape(12.dp))
            .border(1.dp, rowBorder, RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = phrase,
            color = WHITE_SOFT.copy(alpha = if (enabled) 0.88f else 0.55f),
            fontSize = 14.5.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

