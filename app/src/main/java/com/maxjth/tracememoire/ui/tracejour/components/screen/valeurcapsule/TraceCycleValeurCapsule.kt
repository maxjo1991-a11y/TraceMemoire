package com.maxjth.tracememoire.ui.tracejour.components.screen.valeurcapsule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TraceCycleValeurCapsule(
    cycleLabel: String,
    value: Int,
    max: Int,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)

    val bgBrush = Brush.horizontalGradient(
        colors = listOf(
            MAUVE.copy(alpha = 0.14f),
            Color.Black.copy(alpha = 0.68f),
            TURQUOISE.copy(alpha = 0.12f)
        )
    )

    val borderBrush = Brush.horizontalGradient(
        colors = listOf(
            MAUVE.copy(alpha = 0.62f),
            TURQUOISE.copy(alpha = 0.62f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(bgBrush, shape)
            .border(
                width = 1.25.dp,
                brush = borderBrush,
                shape = shape
            )
            .padding(horizontal = 18.dp, vertical = 15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$cycleLabel • en cours",
                color = WHITE_SOFT.copy(alpha = 0.94f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Valeur $value / $max",
                color = WHITE_SOFT,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}