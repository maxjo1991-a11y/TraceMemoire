// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/ajout/style/EmpreinteButtonStyle.kt
package com.maxjth.tracememoire.ui.accueil.ajout.style

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE

@Composable
fun EmpreinteButtonStyleContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            // Glow subtil vers le bas
            .shadow(
                elevation = 12.dp,
                shape = CircleShape,
                ambientColor = MAUVE.copy(alpha = 20.4f),
                spotColor = TURQUOISE.copy(alpha = 3.6f)
            )
            // Contour dégradé
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MAUVE,
                        TURQUOISE
                    )
                ),
                shape = CircleShape
            )
            // Épaisseur de l'anneau
            .padding(1.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color.Transparent,
                    shape = CircleShape
                )
        ) {
            content()
        }
    }
}

