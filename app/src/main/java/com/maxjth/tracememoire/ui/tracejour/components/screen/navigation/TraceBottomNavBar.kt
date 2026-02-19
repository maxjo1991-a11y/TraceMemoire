// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/navigation/TraceBottomNavBar.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

/**
 * ✅ Bottom nav PREMIUM (glow + double border + glass)
 * - 3 actions : Retour / Historique / Approfondir
 * - style néon doux (Trace Mémoire)
 */
@Composable
fun TraceBottomNavBar(
    onBack: () -> Unit,
    onHistory: () -> Unit,
    onDeepen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(18.dp)

    // Palette néon douce (cohérente avec tes screens)
    val TURQ = TURQUOISE
    val MAUV = MAUVE
    val DEEP1 = Color(0xFF071716)
    val DEEP2 = Color(0xFF120A1D)

    // Glow externe (vrai premium)
    val glowBrush = remember {
        Brush.horizontalGradient(
            colors = listOf(
                TURQ.copy(alpha = 0.32f),
                MAUV.copy(alpha = 0.32f)
            )
        )
    }

    // Fond verre interne
    val glassBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                DEEP1.copy(alpha = 0.92f),
                BG_DEEP.copy(alpha = 0.60f),
                DEEP2.copy(alpha = 0.92f)
            )
        )
    }

    // Reflet haut (shine)
    val shineBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.08f),
                Color.Transparent
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        // ✅ Couche glow derrière (sinon ça “s’éteint”)
        Box(
            modifier = Modifier
                .matchParentSize()
                .shadow(elevation = 18.dp, shape = shape, clip = false)
                .clip(shape)
                .background(glowBrush)
        )

        // ✅ Barre devant
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(glassBrush)
                .border(1.dp, MAUV.copy(alpha = 0.28f), shape)      // contour externe mauve
                .border(0.8.dp, TURQ.copy(alpha = 0.16f), shape)    // contour interne turquoise
        ) {
            // Shine overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .background(shineBrush)
            )

            Row(
                modifier = Modifier
                    .matchParentSize()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NavPill(
                    text = "Retour",
                    selected = false,
                    accent = WHITE_SOFT.copy(alpha = 0.86f),
                    onClick = onBack
                )

                Spacer(Modifier.width(10.dp))

                NavPill(
                    text = "Historique",
                    selected = false,
                    accent = WHITE_SOFT.copy(alpha = 0.86f),
                    onClick = onHistory
                )

                Spacer(Modifier.width(10.dp))

                // ✅ Approfondir = pill “selected” premium turquoise
                NavPill(
                    text = "Approfondir",
                    selected = true,
                    accent = TURQ.copy(alpha = 0.95f),
                    onClick = onDeepen
                )
            }
        }
    }
}

@Composable
private fun NavPill(
    text: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    val pillShape = RoundedCornerShape(999.dp)

    val bg = if (selected) {
        Brush.horizontalGradient(
            listOf(
                Color(0xFF0E3B3A).copy(alpha = 0.90f),
                Color(0xFF2A1740).copy(alpha = 0.90f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                BG_DEEP.copy(alpha = 0.30f),
                BG_DEEP.copy(alpha = 0.18f)
            )
        )
    }

    val borderOuter = if (selected) MAUVE.copy(alpha = 0.30f) else MAUVE.copy(alpha = 0.20f)
    val borderInner = if (selected) TURQUOISE.copy(alpha = 0.22f) else TURQUOISE.copy(alpha = 0.10f)

    Row(
        modifier = Modifier
            .height(50.dp)
            .clip(pillShape)
            .background(bg)
            .border(1.dp, borderOuter, pillShape)
            .border(0.8.dp, borderInner, pillShape)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            color = accent,
            letterSpacing = 0.2.sp
        )
    }
}