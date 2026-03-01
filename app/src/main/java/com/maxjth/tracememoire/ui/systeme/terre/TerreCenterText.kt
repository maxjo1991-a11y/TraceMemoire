// FILE: app/src/main/java/com/maxjth/tracememoire/ui/terre/TerreCenterText.kt
package com.maxjth.tracememoire.ui.systeme.terre

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TerreCenterText(
    pct: Int?,                           // null = pas de donnée
    modifier: Modifier = Modifier,
    numberScale: Float = 0.78f,
    offsetY: Dp = (-2).dp,
    showPercentSymbol: Boolean = false,  // 🔒 pas de % dans les cercles
    nullSymbol: String = "·",            // ✅ plus élégant que "—"

    // ✅ Ajouts: pour matcher l’univers “matière” du Soleil
    colorOverride: Color? = null,
    glowColor: Color? = null
) {
    val safePct = pct?.coerceIn(0, 100)

    // ✅ Quand null: on affiche un petit point discret
    val centerText = safePct?.toString() ?: nullSymbol

    val numberSize = when {
        safePct == null -> 24.sp
        centerText.length == 1 -> 46.sp
        centerText.length == 2 -> 44.sp
        else -> 38.sp
    }

    // ✅ Blanc "titre" (si tu veux encore plus blanc: Color.White)
    val titleWhite = Color.White

    // ✅ Si pct == 0 => on force blanc comme le titre
    val isZero = (safePct == 0)
    val isNull = (safePct == null)

    // Brush par défaut (si pas override) — gardé, mais plus utilisé pour le 0
    val defaultBrush = Brush.linearGradient(
        colors = listOf(
            MAUVE.copy(alpha = 0.92f),
            WHITE_SOFT.copy(alpha = 0.70f)
        )
    )

    val hasSoleilMaterial = (colorOverride != null && glowColor != null)

    val percentSp = (numberSize.value * 0.46f).sp.let { if (it.value < 14f) 14.sp else it }
    val percentColor = if (safePct == null) {
        MAUVE.copy(alpha = 0.55f)
    } else {
        Color(0xFF9B7BFF).copy(alpha = 0.68f)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .offset(x = 2.dp, y = offsetY)
            .graphicsLayer {
                scaleX = numberScale
                scaleY = numberScale
            }
    ) {
        Box(contentAlignment = Alignment.Center) {

            val accent = glowColor ?: TURQUOISE
            val coreTint = colorOverride ?: WHITE_SOFT

            // ✅ Alpha ajustés
            val glowAlpha = if (isNull) 0.030f else 0.060f
            val shadowAlpha = if (isNull) 0.10f else 0.18f
            val coreAlpha = if (isNull) 0.55f else 0.90f

            // 1) Halo ultra soft (optionnel)
            if (glowColor != null) {
                Text(
                    text = centerText,
                    style = TextStyle(
                        color = accent.copy(alpha = glowAlpha),
                        fontSize = numberSize,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1.10).sp,
                        shadow = Shadow(
                            color = accent.copy(alpha = shadowAlpha),
                            offset = Offset.Zero,
                            blurRadius = if (isNull) 14f else 18f
                        )
                    )
                )
            }

            // 2) Texte centre net
            Text(
                text = centerText,
                style = when {
                    // ✅ PRIORITÉ: 0 = BLANC TITRE
                    isZero -> TextStyle(
                        color = titleWhite,
                        fontSize = numberSize,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1.08).sp,
                        shadow = Shadow(
                            color = MAUVE.copy(alpha = 0.14f),
                            offset = Offset.Zero,
                            blurRadius = 14f
                        )
                    )

                    // ✅ Matière premium proche Soleil
                    hasSoleilMaterial -> TextStyle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                coreTint.copy(alpha = 0.96f),
                                lerp(coreTint, accent, 0.18f).copy(alpha = 0.88f),
                                accent.copy(alpha = 0.78f),
                                Color(0xFF0A0A0A).copy(alpha = 0.92f)
                            ),
                            center = Offset(28f, -28f),
                            radius = 140f
                        ),
                        fontSize = numberSize,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1.08).sp
                    )

                    // ✅ Couleur simple override
                    colorOverride != null -> TextStyle(
                        color = colorOverride.copy(alpha = coreAlpha),
                        fontSize = numberSize,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1.08).sp
                    )

                    // ✅ Fallback
                    else -> TextStyle(
                        brush = defaultBrush,
                        fontSize = numberSize,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1.08).sp
                    )
                }
            )
        }

        if (showPercentSymbol) {
            Text(
                text = "%",
                fontSize = percentSp,
                fontWeight = FontWeight.SemiBold,
                color = percentColor,
                modifier = Modifier.offset(x = 3.dp, y = 1.dp)
            )
        }
    }
}