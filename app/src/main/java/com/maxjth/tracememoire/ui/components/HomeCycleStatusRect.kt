// FILE: app/src/main/java/com/maxjth/tracememoire/ui/components/HomeCycleStatusRect.kt
package com.maxjth.tracememoire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.screen.utils.safeClickable

@Composable
fun HomeCycleStatusRect(
    title: String,
    subtitle: String,
    rightHint: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(18.dp)

    // ✅ 1) Halo + présent (comme ta “belle” version)
    val glowBrush = remember {
        Brush.linearGradient(
            colors = listOf(
                MAUVE.copy(alpha = 0.42f),
                Color.Transparent,
                TURQUOISE.copy(alpha = 0.26f)
            )
        )
    }

    // ✅ 2) Fond interne plus contrasté (moins fade)
    val cardBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                // haut
                androidx.compose.ui.graphics.lerp(BG_DEEP, MAUVE, 0.16f).copy(alpha = 0.98f),
                // milieu
                BG_SOFT.copy(alpha = 0.86f),
                // bas
                androidx.compose.ui.graphics.lerp(BG_DEEP, TURQUOISE, 0.14f).copy(alpha = 0.96f)
            )
        )
    }

    // ✅ 3) Bordure plus vivante (toujours douce)
    val borderColor = MAUVE.copy(alpha = 0.32f)

    Column(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.safeClickable(enabled = true) { onClick() }
                } else Modifier
            )
            // halo externe
            .clip(shape)
            .background(glowBrush)
            .padding(1.5.dp) // un peu plus épais = plus “présent”
            // carte interne
            .clip(shape)
            .background(cardBrush)
            .border(1.2.dp, borderColor, shape)
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
                color = WHITE_SOFT.copy(alpha = 0.96f)
            )
            Text(
                text = rightHint,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TURQUOISE.copy(alpha = 0.94f)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = subtitle,
            fontSize = 15.sp,
            color = WHITE_SOFT.copy(alpha = 0.80f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}