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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TracePercentBadge(
    percent: Int,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    sizeDp: Int = 52 // ✅ plus gros (avant 44)
) {
    val pct = percent.coerceIn(0, 100)

    // ✅ Fond un peu plus profond pour faire ressortir le mauve
    val bg = BG_SOFT.copy(alpha = if (enabled) 0.32f else 0.22f)

    // ✅ Bord plus épais + plus mauve
    val ringWidth = if (enabled) 3.5.dp else 2.dp

    // ✅ Mauve plus visible + léger “lift” turquoise (très discret)
    val ringBrush = Brush.linearGradient(
        colors = listOf(
            MAUVE.copy(alpha = if (enabled) 0.95f else 0.40f),
            TURQUOISE.copy(alpha = if (enabled) 0.22f else 0.08f),
            MAUVE.copy(alpha = if (enabled) 0.90f else 0.35f)
        )
    )

    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(bg)
            // ✅ petit contour interne sombre (effet “propre”, pas flashy)
            .border(
                width = 1.dp,
                color = Color.Black.copy(alpha = if (enabled) 0.35f else 0.25f),
                shape = CircleShape
            )
            // ✅ contour principal mauve plus “présent”
            .border(
                width = ringWidth,
                brush = ringBrush,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$pct%",
            color = WHITE_SOFT.copy(alpha = if (enabled) 0.96f else 0.58f),
            fontSize = 16.sp, // ✅ un poil plus lisible
            fontWeight = FontWeight.Bold
        )
    }
}