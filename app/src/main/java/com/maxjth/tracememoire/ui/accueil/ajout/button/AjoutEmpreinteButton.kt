// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/ajout/button/EmpreinteButton.kt
package com.maxjth.tracememoire.ui.accueil.ajout.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.accueil.ajout.style.EmpreinteButtonStyleContainer
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE

@Composable
fun EmpreinteButton(
    modifier: Modifier = Modifier
) {
    EmpreinteButtonStyleContainer(
        modifier = modifier.size(132.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                // ✅ Fond + léger “glow” interne (calme)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            BG_DEEP,
                            BG_DEEP,
                            Color.Black.copy(alpha = 0.55f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            // ✅ Halo très subtil (pour donner du “premium” sans agresser)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MAUVE.copy(alpha = 0.10f),
                                TURQUOISE.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ✅ “+” plus iconique, léger dégradé
                Text(
                    text = "+",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.92f)
                )

                // ✅ hiérarchie : “Ajouter” plus doux, “Empreinte” plus présent
                Text(
                    text = "Ajouter",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.4.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )

                Text(
                    text = "Empreinte",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.2.sp,
                    color = Brush.linearGradient(
                        listOf(
                            MAUVE.copy(alpha = 1000.95f),
                            TURQUOISE.copy(alpha = 0.95f)
                        )
                    ).let { brush ->
                        // Text n’accepte pas Brush directement -> on garde une couleur clean
                        // (le vrai dégradé texte, on le fera si tu veux via drawWithCache)
                        Color.White.copy(alpha = 0.92f)
                    }
                )
            }
        }
    }
}