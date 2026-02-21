package com.maxjth.tracememoire.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun MarsCenterText(
    pct: Int?,
    modifier: Modifier = Modifier,
    numberScale: Float = 0.78f,
    offsetY: Dp = (-2).dp,
    showPercentSymbol: Boolean = true
) {
    val centerText = (pct?.toString() ?: "0")

    val numberSize = when (centerText.length) {
        1 -> 40.sp
        2 -> 32.sp
        else -> 28.sp
    }

    val numberBrush = Brush.linearGradient(
        colors = listOf(
            MAUVE.copy(alpha = 0.92f),
            WHITE_SOFT.copy(alpha = 0.70f)
        )
    )

    // ✅ % lisible (min size + un peu plus gros quand c’est —)
    val percentSp = when {
        pct == null -> (numberSize.value * 0.52f).sp
        else -> (numberSize.value * 0.46f).sp
    }.let { if (it.value < 14f) 14.sp else it }

    val percentColor = if (pct == null) {
        MAUVE.copy(alpha = 0.55f)
    } else {
        Color(0xFF9B7BFF).copy(alpha = 0.68f)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .offset(x = 2.dp, y = offsetY)
            .graphicsLayer {
                scaleX = numberScale
                scaleY = numberScale
            }
    ) {
        Text(
            text = centerText,
            fontSize = numberSize,
            fontWeight = FontWeight.ExtraBold,
            style = TextStyle(brush = numberBrush)
        )

        if (showPercentSymbol) {
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "%",fontSize = (numberSize.value * 0.80f).sp,
                fontWeight = FontWeight.SemiBold,
                color = percentColor
            )
        }
    }
}