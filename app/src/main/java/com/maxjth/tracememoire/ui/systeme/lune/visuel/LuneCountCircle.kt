// FILE: app/src/main/java/com/maxjth/tracememoire/ui/systeme/lune/visuel/LuneCountCircle.kt
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
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
    // TUNING
    // =========================
    val ringStroke = 6.dp

    val baseValueSp = (sizeDp * 0.18f).coerceIn(34f, 42f)
    val deltaSp = (sizeDp * 0.15f).coerceIn(16f, 20f).sp

    val lineWidthFactor = 0.54f
    val lineHeightDp = 2.2.dp
    val contentGap = 4.dp

    val safeValue = value.coerceAtLeast(0)
    val safeDelta = deltaToday.coerceAtLeast(0)
    val showDelta = safeDelta > 0

    val valueText = safeValue.toString()

    // ✅ garde-fou visuel : zéro légèrement plus petit
    val adjustedValueSp = when {
        valueText == "0" -> baseValueSp * 0.82f
        valueText.length == 1 -> baseValueSp * 1.00f
        valueText.length == 2 -> baseValueSp * 0.92f
        valueText.length >= 3 -> baseValueSp * 0.82f
        else -> baseValueSp
    }.sp

    // =========================
    // MOTION
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
        initialValue = 0.992f,
        targetValue = 1.010f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mia_lune_pulse"
    )

    val innerShimmer by inf.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mia_lune_inner_shimmer"
    )

    // =========================
    // UI
    // =========================
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .background(BG_DEEP)
            .border(
                width = ringStroke,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.22f),
                        MAUVE.copy(alpha = 0.62f),
                        TURQUOISE.copy(alpha = 0.88f),
                        Color.Black.copy(alpha = 0.34f)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        // 1) Fond profond principal
        Box(
            modifier = Modifier
                .size((sizeDp * 0.92f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0A0A10).copy(alpha = 0.96f),
                            Color(0xFF07080E).copy(alpha = 0.86f),
                            Color.Transparent
                        ),
                        radius = sizeDp * 0.82f
                    )
                )
        )

        // 2) Ombre interne large
        Box(
            modifier = Modifier
                .size((sizeDp * 1.90f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.54f)
                        ),
                        radius = sizeDp * 2.18f
                    )
                )
        )

        // 3) Halo turquoise flottant
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
                        radius = sizeDp * 0.70f
                    )
                )
        )

        // 4) Halo mauve secondaire
        Box(
            modifier = Modifier
                .size((sizeDp * 0.98f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = glow * 0.82f),
                            Color.Transparent
                        ),
                        radius = sizeDp * 0.96f
                    )
                )
        )

        // 5) Reflet haut-gauche
        Box(
            modifier = Modifier
                .size((sizeDp * 0.66f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            WHITE_SOFT.copy(alpha = 0.16f),
                            Color.Transparent
                        ),
                        radius = sizeDp * 0.22f
                    )
                )
                .graphicsLayer(
                    translationX = (-sizeDp * 0.12f),
                    translationY = (-sizeDp * 0.15f),
                    alpha = 0.95f,
                    blendMode = BlendMode.SrcOver
                )
        )

        // 6) Lentille centrale douce
        Box(
            modifier = Modifier
                .size((sizeDp * 0.88f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            WHITE_SOFT.copy(alpha = innerShimmer),
                            Color.Transparent
                        ),
                        radius = sizeDp * 0.26f
                    )
                )
                .graphicsLayer(
                    translationX = (-sizeDp * 0.04f),
                    translationY = (-sizeDp * 0.05f),
                    alpha = 0.92f,
                    blendMode = BlendMode.SrcOver
                )
        )

        // 7) Micro lumière au centre
        Box(
            modifier = Modifier
                .size((sizeDp * 0.20f).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        radius = sizeDp * 0.11f
                    )
                )
        )

        // 8) Contenu
        Box(
            modifier = Modifier
                .size((sizeDp * 0.88f * pulse).dp),
            contentAlignment = Alignment.Center
        ) {
            if (!showDelta) {
                Text(
                    text = valueText,
                    style = TextStyle(
                        color = WHITE_SOFT.copy(alpha = 0.96f),
                        fontSize = adjustedValueSp,
                        fontWeight = FontWeight.ExtraBold,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.24f),
                            offset = Offset.Zero,
                            blurRadius = 10f
                        )
                    )
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = valueText,
                        style = TextStyle(
                            color = WHITE_SOFT.copy(alpha = 0.96f),
                            fontSize = adjustedValueSp,
                            fontWeight = FontWeight.ExtraBold,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.24f),
                                offset = Offset.Zero,
                                blurRadius = 10f
                            )
                        )
                    )

                    Spacer(Modifier.height(contentGap))

                    Box(
                        modifier = Modifier
                            .width((sizeDp * lineWidthFactor).dp)
                            .height(lineHeightDp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MAUVE.copy(alpha = 0.16f),
                                        MAUVE.copy(alpha = 0.66f),
                                        MAUVE.copy(alpha = 0.16f)
                                    )
                                )
                            )
                    )

                    Spacer(Modifier.height(contentGap))

                    Text(
                        text = "+$safeDelta",
                        fontSize = deltaSp,
                        fontWeight = FontWeight.SemiBold,
                        color = TURQUOISE.copy(alpha = 0.96f),
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.18f),
                                offset = Offset.Zero,
                                blurRadius = 8f
                            )
                        )
                    )
                }
            }
        }
    }
}