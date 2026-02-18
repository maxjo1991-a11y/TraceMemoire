package com.maxjth.tracememoire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
fun HomeCycleStatusRect(
    title: String,
    subtitle: String,
    rightHint: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(18.dp)

    // ✅ Modifier clickable propre (évite les combinaisons bizarres)
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .then(clickableModifier)
            .border(1.dp, MAUVE.copy(alpha = 0.22f), shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BG_SOFT.copy(alpha = 0.55f),
                        Color.Black.copy(alpha = 0.10f),
                        BG_SOFT.copy(alpha = 0.35f)
                    )
                ),
                shape = shape
            )
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = WHITE_SOFT.copy(alpha = 0.92f)
            )
            Text(
                text = rightHint,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TURQUOISE.copy(alpha = 0.90f)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = subtitle,
            fontSize = 15.sp,
            color = WHITE_SOFT.copy(alpha = 0.72f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}