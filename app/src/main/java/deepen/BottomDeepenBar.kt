// FILE: app/src/main/java/com/maxjth/tracememoire/ui/deepen/BottomDeepenBar.kt
package com.maxjth.tracememoire.ui.deepen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomDeepenBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val shape = RoundedCornerShape(999.dp)

    val alpha = if (enabled) 0.90f else 0.30f
    val glowAlpha = if (enabled) 0.55f else 0.16f
    val borderAlpha = if (enabled) 0.26f else 0.10f
    val innerBorderAlpha = if (enabled) 0.18f else 0.07f
    val textAlpha = if (enabled) 0.94f else 0.45f

    // Palette Trace Mémoire (néon doux)
    val TURQ = Color(0xFF2FD6CF)
    val MAUV = Color(0xFF7A63C6)
    val DEEP1 = Color(0xFF071716)
    val DEEP2 = Color(0xFF120A1D)

    // ✅ Glow “réel” : pas de transparent au centre (sinon ça s’éteint)
    val glowBrush = remember(enabled) {
        Brush.horizontalGradient(
            colors = listOf(
                TURQ.copy(alpha = glowAlpha),
                MAUV.copy(alpha = glowAlpha)
            )
        )
    }

    // ✅ Fond interne néon
    val bgBrush = remember(enabled) {
        Brush.horizontalGradient(
            colors = listOf(
                Color(0xFF0E3B3A).copy(alpha = alpha),
                Color(0xFF2A1740).copy(alpha = alpha)
            )
        )
    }

    // ✅ Shine (reflet haut)
    val shineBrush = remember(enabled) {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = if (enabled) 0.10f else 0.04f),
                Color.Transparent
            )
        )
    }

    // =========================
    // COUCHE A : GLOW derrière
    // =========================
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        // Glow derrière (ne pas clipper trop tôt)
        Box(
            modifier = Modifier
                .matchParentSize()
                // shadow = vrai effet premium (le glow respire)
                .shadow(
                    elevation = if (enabled) 18.dp else 6.dp,
                    shape = shape,
                    clip = false
                )
                .clip(shape)
                .background(glowBrush)
        )

        // =========================
        // COUCHE B : BARRE devant
        // =========================
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)

                // 1) base sombre (pour que le glow ressorte)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            DEEP1.copy(alpha = 0.95f),
                            DEEP2.copy(alpha = 0.95f)
                        )
                    )
                )
                .padding(1.2.dp)

                // 2) fond néon principal
                .clip(shape)
                .background(bgBrush)

                // 3) bord externe mauve
                .border(
                    width = 1.dp,
                    color = MAUV.copy(alpha = borderAlpha),
                    shape = shape
                )

                // 4) bord interne turquoise (subtil)
                .border(
                    width = 0.8.dp,
                    color = TURQ.copy(alpha = innerBorderAlpha),
                    shape = shape
                )

                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            // Shine overlay (reflet)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .background(shineBrush)
            )

            Text(
                text = "Approfondir la mémoire →",
                color = Color.White.copy(alpha = textAlpha),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}