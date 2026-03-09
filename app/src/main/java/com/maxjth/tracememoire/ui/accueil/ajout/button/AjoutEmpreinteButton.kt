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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.accueil.ajout.style.EmpreinteButtonStyleContainer
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

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
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0C0D12),
                            BG_DEEP,
                            Color.Black.copy(alpha = 0.62f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {

            // Halo principal doux
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MAUVE.copy(alpha = 0.11f),
                                TURQUOISE.copy(alpha = 0.09f),
                                Color.Transparent
                            ),
                            radius = 220f
                        )
                    )
            )

            // Profondeur interne
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.28f)
                            ),
                            radius = 150f
                        )
                    )
            )

            // Reflet haut-gauche très subtil
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                WHITE_SOFT.copy(alpha = 0.07f),
                                Color.Transparent
                            ),
                            radius = 42f
                        )
                    )
                    .graphicsLayer(
                        translationX = -10f,
                        translationY = -12f,
                        alpha = 0.95f,
                        blendMode = BlendMode.SrcOver
                    )
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "+",
                    style = TextStyle(
                        fontSize = 31.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        color = Color.White.copy(alpha = 0.94f),
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.18f),
                            offset = Offset.Zero,
                            blurRadius = 7f
                        )
                    )
                )

                Text(
                    text = "Ajouter",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.35.sp,
                    color = Color.White.copy(alpha = 0.74f)
                )

                Text(
                    text = "Empreinte",
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.20.sp,
                        color = Color.White.copy(alpha = 0.93f),
                        shadow = Shadow(
                            color = TURQUOISE.copy(alpha = 0.08f),
                            offset = Offset.Zero,
                            blurRadius = 6f
                        )
                    )
                )
            }
        }
    }
}