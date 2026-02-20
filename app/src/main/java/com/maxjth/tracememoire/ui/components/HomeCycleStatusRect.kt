// FILE: app/src/main/java/com/maxjth/tracememoire/ui/components/HomeCycleStatusRect.kt
package com.maxjth.tracememoire.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import kotlin.math.sin

@Composable
fun HomeCycleStatusRect(
    leftTitle: String,
    rightCounter: String,
    ambientLine: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // ✅ Plus “objet” / moins agressif
    val shape = RoundedCornerShape(22.dp)

    // ✅ Pas de ripple (et évite les soucis d’indication selon versions)
    val isrc = remember { MutableInteractionSource() }
    val pressed by isrc.collectIsPressedAsState()

    // ✅ Halo externe qui “respire”
    val inf = rememberInfiniteTransition(label = "homeRectHalo")
    val haloPhase by inf.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "haloPhase"
    )

    // Halo doux + boost au touch
    val haloBase = 0.06f + 0.06f * haloPhase
    val haloTouchBoost = if (pressed) 0.14f else 0f
    val haloA = (haloBase + haloTouchBoost).coerceIn(0.04f, 0.24f)

    // ✅ Dégradé “velours” (diagonal très subtil)
    val veloursBrush = Brush.linearGradient(
        colors = listOf(
            MAUVE.copy(alpha = 0.18f),
            TURQUOISE.copy(alpha = 0.14f),
            MAUVE.copy(alpha = 0.12f)
        ),
        start = Offset(0f, 0f),
        end = Offset(900f, 420f)
    )

    // ✅ Fond : BG_SOFT mais plus “deep” (sans voler la lumière du sous-titre du haut)
    val baseBg = BG_SOFT.copy(alpha = 0.28f)

    // ✅ Bordure fine + premium
    val borderA = if (pressed) 0.26f else 0.16f
    val borderColor = TURQUOISE.copy(alpha = borderA)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 70.dp) // compact (tu peux monter à 72 si tu veux)
            .clip(shape)
            .background(baseBg)
            .background(veloursBrush)
            .drawBehind {
                val r = CornerRadius(22.dp.toPx(), 22.dp.toPx())

                // ── Halo externe (très doux)
                drawRoundRect(
                    color = TURQUOISE.copy(alpha = haloA * 0.55f),
                    style = Stroke(width = 10.dp.toPx()),
                    cornerRadius = r
                )
                drawRoundRect(
                    color = MAUVE.copy(alpha = haloA * 0.40f),
                    style = Stroke(width = 6.dp.toPx()),
                    cornerRadius = r
                )

                // ── Grain “velours numérique” (ultra discret, statique)
                // On dessine quelques lignes fines pseudo-aléatoires (pas d’animation => pas fatigant)
                val w = size.width
                val h = size.height
                val grainAlpha = 0.045f
                val step = 18f
                var y = 0f
                var i = 0
                while (y < h) {
                    val dx = (sin(i * 1.7f) * 14f)
                    drawLine(
                        color = Color.White.copy(alpha = grainAlpha),
                        start = Offset(x = 0f + dx, y = y),
                        end = Offset(x = w, y = y),
                        strokeWidth = 0.9f
                    )
                    y += step
                    i++
                }
            }
            .border(1.5.dp, borderColor, shape)
            .clickable(
                interactionSource = isrc,
                indication = null
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = leftTitle,
                color = WHITE_SOFT.copy(alpha = 0.92f),
                fontSize = 13.sp, // ✅ plus lisible (premium)
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = rightCounter,
                color = TURQUOISE.copy(alpha = 0.86f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.padding(top = 6.dp))

        // ✅ Animation douce quand la phrase change (fade + micro slide)
        AnimatedContent(
            targetState = ambientLine,
            transitionSpec = {
                fadeIn(tween(950, easing = FastOutSlowInEasing)) togetherWith
                        fadeOut(tween(950, easing = FastOutSlowInEasing))
            },
            label = "ambient_phrase_swap"
        ) { line ->
            Text(
                text = line,
                color = TURQUOISE.copy(alpha = 0.56f), // ✅ minimal turquoise (ne concurrence pas ton sous-titre MAUVE)
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Start,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}