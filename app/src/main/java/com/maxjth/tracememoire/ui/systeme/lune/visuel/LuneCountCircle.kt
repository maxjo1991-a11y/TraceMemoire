// FILE: app/src/main/java/com/maxjth/tracememoire/ui/lune/visual/LuneCountCircle.kt
package com.maxjth.tracememoire.ui.systeme.lune.visuel

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun LuneCountCircle(
    value: Int,
    sizeDp: Int,
    modifier: Modifier = Modifier,
    deltaToday: Int = 0
) {
    // =========================
    // TUNING (facile à ajuster)
    // =========================
    val ringStroke = 6.dp

    // ✅ Texte (tes réglages actuels)
    val valueSp = (sizeDp * 0.20f).coerceIn(45f, 50f).sp
    val deltaSp = (sizeDp * 0.20f).coerceIn(20f, 22f).sp

    // Ligne mauve (fine + classy)
    val lineWidthFactor = 0.54f
    val lineHeightDp = 2.2.dp

    val safeValue = value.coerceAtLeast(0)
    val safeDelta = deltaToday.coerceAtLeast(0)
    val showDelta = safeDelta > 0

    // =========================
    // Motion (premium, doux)
    // =========================
    val inf = rememberInfiniteTransition(label = "mia_lune")

    val glow by inf.animateFloat(
        initialValue = 0.10f,
        targetValue = 0.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mia_lune_glow"
    )

    val pulse by inf.animateFloat(
        initialValue = 0.994f,
        targetValue = 1.010f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mia_lune_pulse"
    )

    // =========================
    // UI
    // =========================
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(BG_DEEP)
            // ✅ Anneau “verre premium” (highlight + teinte + ombre)
            .border(
                width = ringStroke,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.20f), // highlight
                        MAUVE.copy(alpha = 0.55f),
                        TURQUOISE.copy(alpha = 0.85f),
                        Color.Black.copy(alpha = 0.35f)  // ombre perceptuelle
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        // 1) Fond “verre” / profondeur
        Box(
            modifier = Modifier
                .size((sizeDp * 0.92f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0A0A10).copy(alpha = 0.92f),
                            Color(0xFF06070D).copy(alpha = 0.78f),
                            Color.Transparent
                        ),
                        radius = (sizeDp * 1.15f)
                    )
                )
        )

        // =========================================================
        // ✅ (A) INNER SHADOW RADIAL — profondeur “verre” (GPU safe)
        // =========================================================
        Box(
            modifier = Modifier
                .size((sizeDp * 1.92f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.52f)
                        ),
                        radius = sizeDp * 2.20f
                    )
                )
        )

        // 2) Halo doux (turquoise) + (mauve) pour le côté “flottant”
        Box(
            modifier = Modifier
                .size((sizeDp * 0.80f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            TURQUOISE.copy(alpha = glow),
                            Color.Transparent
                        ),
                        radius = (sizeDp * 0.70f)
                    )
                )
        )

        Box(
            modifier = Modifier
                .size((sizeDp * 0.96f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = glow * 0.78f),
                            Color.Transparent
                        ),
                        radius = (sizeDp * 0.95f)
                    )
                )
        )

        // =========================================================
        // ✅ (B) HIGHLIGHT HAUT-GAUCHE — reflet “verre”
        // =========================================================
        Box(
            modifier = Modifier
                .size((sizeDp * 0.68f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            WHITE_SOFT.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = sizeDp * 0.10f
                    )
                )
                .graphicsLayer(
                    translationX = (-sizeDp * 0.12f),
                    translationY = (-sizeDp * 0.14f),
                    alpha = 0.95f,
                    blendMode = BlendMode.SrcOver
                )
        )

        // 3) “Lentille” interne (petit reflet propre)
        Box(
            modifier = Modifier
                .size((sizeDp * 0.92f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            WHITE_SOFT.copy(alpha = 0.10f),
                            Color.Transparent
                        ),
                        radius = (sizeDp * 0.30f)
                    )
                )
                .graphicsLayer(
                    translationX = (-sizeDp * 0.07f),
                    translationY = (-sizeDp * 0.08f),
                    alpha = 0.95f,
                    blendMode = BlendMode.SrcOver
                )
        )

        // 4) Contenu (pulse très léger)
        Box(
            modifier = Modifier
                .size((sizeDp * 0.88f * pulse).dp),
            contentAlignment = Alignment.Center
        ) {
            if (!showDelta) {

                Text(
                    text = safeValue.toString(),
                    fontSize = valueSp,
                    fontWeight = FontWeight.ExtraBold,
                    color = WHITE_SOFT.copy(alpha = 0.94f)
                )

            } else {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = safeValue.toString(),
                        fontSize = valueSp,
                        fontWeight = FontWeight.ExtraBold,
                        color = WHITE_SOFT.copy(alpha = 0.94f)
                    )

                    Spacer(Modifier.height(7.dp))

                    // ✅ Ligne mauve (fine, premium)
                    Box(
                        modifier = Modifier
                            .width((sizeDp * lineWidthFactor).dp)
                            .height(lineHeightDp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MAUVE.copy(alpha = 0.18f),
                                        MAUVE.copy(alpha = 0.62f),
                                        MAUVE.copy(alpha = 0.18f)
                                    )
                                )
                            )
                    )

                    Spacer(Modifier.height(7.dp))

                    Text(
                        text = "+$safeDelta",
                        fontSize = deltaSp,
                        fontWeight = FontWeight.SemiBold,
                        color = TURQUOISE.copy(alpha = 0.96f)
                    )
                }
            }
        }
    }
}