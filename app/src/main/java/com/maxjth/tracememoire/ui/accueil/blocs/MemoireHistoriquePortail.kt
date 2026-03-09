package com.maxjth.tracememoire.ui.accueil.blocs

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun MemoireHistoriquePortail(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    titleTop: String = "Mémoire",
    titleBottom: String = "Historique",
    accent: Color = TURQUOISE
) {
    val inf = rememberInfiniteTransition(label = "memoire_portail")

    // Respiration (halo + glow)
    val breathe by inf.animateFloat(
        initialValue = 0.55f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // Shimmer très lent
    val sweep by inf.animateFloat(
        initialValue = -1.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    // Présence visuelle
    val haloAlpha = 0.10f + (0.14f * breathe)
    val haloSize = 52.dp + (16.dp * breathe)
    val ringAlpha = 0.22f + (0.14f * breathe)

    // Click safe
    val interaction = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interaction,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 8.dp)
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp)
        ) {

            // Petites étoiles mauves discrètes
            Text(
                text = "✦",
                fontSize = 30.sp,
                color = MAUVE.copy(alpha = 0.40f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.offset(x = (-34).dp, y = (-18).dp)
            )



            Text(
                text = "✦",
                fontSize = 30.sp,
                color = MAUVE.copy(alpha = 0.40f),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.offset(x = 34.dp, y = 18.dp)
            )



            // Halo arrière
            Box(
                modifier = Modifier
                    .size(haloSize)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = haloAlpha))
                    .blur(30.dp)
            )

            // Anneau + shimmer
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                accent.copy(alpha = ringAlpha),
                                accent.copy(alpha = 0.03f),
                                accent.copy(alpha = ringAlpha)
                            ),
                            start = Offset(
                                x = 0f + (sweep * 160f),
                                y = 0f
                            ),
                            end = Offset(
                                x = 160f + (sweep * 160f),
                                y = 160f
                            )
                        )
                    )
                    .blur(10.dp)
            )

            // Symbole central
            Text(
                text = "✦",
                fontSize = 33.sp,
                color = accent.copy(alpha = 0.96f),
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = titleTop,
            fontSize = 30.sp,
            color = accent.copy(alpha = 0.95f),
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = titleBottom,
            fontSize = 18.sp,
            color = WHITE_SOFT.copy(alpha = 0.80f),
            fontWeight = FontWeight.Medium
        )
    }
}