package com.maxjth.tracememoire.ui.comprendre.blocs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE

@Composable
fun ComprendreButtonBloc(
    onClick: () -> Unit
) {

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .size(32.dp)
            .shadow(6.dp, CircleShape)
            .background(Color.Black, CircleShape)
            .border(
                width = 1.dp,
                color = MAUVE.copy(alpha = 0.6f),
                shape = CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = "i",
            color = MAUVE,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

    }
}