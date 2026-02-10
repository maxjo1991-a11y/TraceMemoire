// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/keywords/TraceKeywordChip.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.keywords

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TraceKeywordChip(
    label: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
) {
    val shape = RoundedCornerShape(999.dp)

    val borderColor = if (selected) {
        MAUVE.copy(alpha = if (enabled) 0.55f else 0.22f)
    } else {
        Color.White.copy(alpha = if (enabled) 0.14f else 0.08f)
    }

    val textColor = if (selected) {
        WHITE_SOFT.copy(alpha = if (enabled) 0.95f else 0.55f)
    } else {
        WHITE_SOFT.copy(alpha = if (enabled) 0.82f else 0.50f)
    }

    // ✅ IMPORTANT: pas d’offset, padding suffisant, pas de ripple Material3
    Box(
        modifier = modifier
            .wrapContentWidth()
            .defaultMinSize(minHeight = 34.dp)
            .clip(shape)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .clickable(
                enabled = enabled,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(contentPadding),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}