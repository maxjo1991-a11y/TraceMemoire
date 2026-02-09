// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/slider/TraceMoodSliderRow.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.slider

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private data class SliderPersona(
    val breatheMs: Int,         // vitesse respiration
    val haloBase: Float,        // halo base
    val haloAmp: Float,         // amplitude halo
    val wobbleAmpDp: Float,     // micro wobble (dp)
    val wobbleMs: Int,          // vitesse wobble
    val edgeTintMix: Float      // 0..1 (mauve -> turquoise)
)

private fun stableSeed(seedBase: String, cycleKey: String, sliderKey: String, title: String): Int {
    val s = "$seedBase|$cycleKey|$sliderKey|$title"
    return s.fold(0) { acc, c -> acc * 31 + c.code }
}

private fun buildPersona(seed: Int): SliderPersona {
    val r = Random(seed)
    return SliderPersona(
        breatheMs = r.nextInt(2300, 5200),
        haloBase = r.nextFloat() * 0.10f + 0.14f,     // 0.14..0.24
        haloAmp = r.nextFloat() * 0.10f + 0.06f,      // 0.06..0.16
        wobbleAmpDp = r.nextFloat() * 2.2f + 0.4f,    // 0.4..2.6 dp
        wobbleMs = r.nextInt(1800, 4200),
        edgeTintMix = r.nextFloat()
    )
}

private fun lerpColor(a: Color, b: Color, t: Float): Color {
    val tt = t.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * tt,
        green = a.green + (b.green - a.green) * tt,
        blue = a.blue + (b.blue - a.blue) * tt,
        alpha = a.alpha + (b.alpha - a.alpha) * tt
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceMoodSliderRow(
    title: String,
    enabled: Boolean,
    lockedLabel: String? = null,

    // “verrouillage vivant”
    cycleKey: String,     // "MATIN" / "SOIR" / "NUIT"
    seedBase: String,     // ex: "20260208" (clé stable par jour)
    sliderKey: String     // ex: "humeur"
) {
    // Valeur locale 0..100
    val value = rememberSaveable(title) { mutableFloatStateOf(50f) }

    // Persona stable (aléatoire mais fixe)
    val persona = remember(seedBase, cycleKey, sliderKey, title) {
        buildPersona(stableSeed(seedBase, cycleKey, sliderKey, title))
    }

    // Idle motion (respiration + wobble)
    val infinite = rememberInfiniteTransition(label = "sliderAlive_$sliderKey")
    val breathePhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(persona.breatheMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe_$sliderKey"
    )
    val wobblePhase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(persona.wobbleMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wobble_$sliderKey"
    )

    // Réaction post-geste
    var isInteracting by remember { mutableStateOf(false) }
    LaunchedEffect(isInteracting) {
        if (isInteracting) {
            delay(520) // petit “après-geste”
            isInteracting = false
        }
    }
    val boost by animateFloatAsState(
        targetValue = if (isInteracting) 1f else 0f,
        animationSpec = tween(durationMillis = 380, easing = LinearEasing),
        label = "boost_$sliderKey"
    )

    // Respect : si valeur proche 0 -> quasi immobile ; si haut -> vivant
    val respectRaw = (value.floatValue / 100f).coerceIn(0f, 1f)
    val respect = (0.10f + 0.90f * respectRaw) * (if (enabled) 1f else 0.55f)

    // Couleurs “Trace”
    val aliveEdge = lerpColor(
        a = MAUVE.copy(alpha = 1f),
        b = Color(0xFF2EC4B6).copy(alpha = 1f),
        t = persona.edgeTintMix
    )

    // Alphas (respiration + boost) * respect
    val edgeAlpha = (persona.haloBase + persona.haloAmp * breathePhase) * respect
    val borderAlpha = ((0.18f + 0.14f * breathePhase + 0.18f * boost) * respect).coerceIn(0f, 1f)

    // Fond pastille (bords + vivant)
    val pillBg = Brush.horizontalGradient(
        colors = listOf(
            aliveEdge.copy(alpha = (0.10f + 0.10f * breathePhase) * respect),
            BG_SOFT.copy(alpha = 0.18f),
            aliveEdge.copy(alpha = (0.10f + 0.10f * breathePhase) * respect)
        )
    )

    // Thumb vivant (wobble + micro scale)
    val wobble = sin(wobblePhase * 2f * PI).toFloat() * persona.wobbleAmpDp
    val thumbOffsetX = if (enabled) (wobble * respect).dp else 0.dp
    val thumbScale = (0.985f + 0.04f * breathePhase) * (1f + 0.03f * boost) * (0.95f + 0.05f * respect)

    // Track
    val activeTrackColor = MAUVE.copy(alpha = ((0.88f + 0.10f * boost) * respect).coerceIn(0f, 1f))
    val inactiveTrackColor = Color.White.copy(alpha = (0.14f * respect).coerceIn(0f, 0.16f))

    // Header
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                color = WHITE_SOFT.copy(alpha = if (enabled) 0.92f else 0.55f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (!lockedLabel.isNullOrBlank()) {
                Spacer(Modifier.size(8.dp))
                Text(
                    text = "• $lockedLabel",
                    color = WHITE_SOFT.copy(alpha = 0.45f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Text(
            text = "${value.floatValue.roundToInt()}%",
            color = WHITE_SOFT.copy(alpha = if (enabled) 0.95f else 0.55f),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Pastille vivante (BOX)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(brush = pillBg)
            .border(
                width = 1.dp,
                color = aliveEdge.copy(alpha = borderAlpha),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Slider(
            value = value.floatValue,
            onValueChange = {
                value.floatValue = it
                if (enabled) isInteracting = true
            },
            valueRange = 0f..100f,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                activeTrackColor = activeTrackColor,
                inactiveTrackColor = inactiveTrackColor,
                thumbColor = MAUVE.copy(alpha = if (enabled) 1.0f else 0.55f),
                disabledActiveTrackColor = MAUVE.copy(alpha = 0.35f),
                disabledInactiveTrackColor = Color.White.copy(alpha = 0.07f),
                disabledThumbColor = MAUVE.copy(alpha = 0.50f)
            ),
            thumb = {
                Box(
                    modifier = Modifier
                        .offset(x = thumbOffsetX)
                        .size(24.dp)
                        .graphicsLayer(
                            scaleX = thumbScale,
                            scaleY = thumbScale
                        )
                        .clip(CircleShape)
                        .background(aliveEdge.copy(alpha = if (enabled) 0.98f else 0.60f))
                        // halo externe (plus visible quand valeur haute)
                        .border(
                            width = 7.dp,
                            color = aliveEdge.copy(alpha = (abs(edgeAlpha) + 0.06f * boost).coerceIn(0f, 1f)),
                            shape = CircleShape
                        )
                        // peau fine
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = if (enabled) 0.34f else 0.18f),
                            shape = CircleShape
                        )
                )
            }
        )
    }
}