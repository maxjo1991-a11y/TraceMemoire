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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.screen.keywords.TraceKeywordChip
import com.maxjth.tracememoire.ui.tracejour.components.screen.keywords.TraceKeywordPickerSheet
import com.maxjth.tracememoire.ui.tracejour.components.screen.keywords.TraceKeywordsData
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private data class SliderPersona(
    val breatheMs: Int,
    val haloBase: Float,
    val haloAmp: Float,
    val wobbleAmpDp: Float,
    val wobbleMs: Int,
    val edgeTintMix: Float
)

private fun stableSeed(seedBase: String, cycleKey: String, sliderKey: String, title: String): Int {
    val s = "$seedBase|$cycleKey|$sliderKey|$title"
    return s.fold(0) { acc, c -> acc * 31 + c.code }
}

private fun buildPersona(seed: Int): SliderPersona {
    val r = Random(seed)
    return SliderPersona(
        breatheMs = r.nextInt(2300, 5200),
        haloBase = r.nextFloat() * 0.10f + 0.14f,
        haloAmp = r.nextFloat() * 0.10f + 0.06f,
        wobbleAmpDp = r.nextFloat() * 2.2f + 0.4f,
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
    cycleKey: String,
    seedBase: String,
    sliderKey: String
) {
    val value = rememberSaveable(title) { mutableFloatStateOf(50f) }

    var selectedWord by rememberSaveable(sliderKey, cycleKey, seedBase) { mutableStateOf<String?>(null) }
    var showKeywords by remember { mutableStateOf(false) }

    val persona = remember(seedBase, cycleKey, sliderKey, title) {
        buildPersona(stableSeed(seedBase, cycleKey, sliderKey, title))
    }

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

    var isInteracting by remember { mutableStateOf(false) }
    LaunchedEffect(isInteracting) {
        if (isInteracting) {
            delay(520)
            isInteracting = false
        }
    }
    val boost by animateFloatAsState(
        targetValue = if (isInteracting) 1f else 0f,
        animationSpec = tween(durationMillis = 380, easing = LinearEasing),
        label = "boost_$sliderKey"
    )

    val respectRaw = (value.floatValue / 100f).coerceIn(0f, 1f)
    val respect = (0.10f + 0.90f * respectRaw) * (if (enabled) 1f else 0.55f)

    val aliveEdge = lerpColor(
        a = MAUVE.copy(alpha = 1f),
        b = Color(0xFF2EC4B6).copy(alpha = 1f),
        t = persona.edgeTintMix
    )

    val edgeAlpha = (persona.haloBase + persona.haloAmp * breathePhase) * respect
    val borderAlpha = ((0.18f + 0.14f * breathePhase + 0.18f * boost) * respect).coerceIn(0f, 1f)

    val pillBg = Brush.horizontalGradient(
        colors = listOf(
            aliveEdge.copy(alpha = (0.10f + 0.10f * breathePhase) * respect),
            BG_SOFT.copy(alpha = 0.18f),
            aliveEdge.copy(alpha = (0.10f + 0.10f * breathePhase) * respect)
        )
    )

    val wobble = sin(wobblePhase * 2f * PI).toFloat() * persona.wobbleAmpDp
    val thumbOffsetX = if (enabled) (wobble * respect).dp else 0.dp
    val thumbScale =
        (0.985f + 0.04f * breathePhase) * (1f + 0.03f * boost) * (0.95f + 0.05f * respect)

    val activeTrackColor = MAUVE.copy(alpha = ((0.88f + 0.10f * boost) * respect).coerceIn(0f, 1f))
    val inactiveTrackColor = Color.White.copy(alpha = (0.14f * respect).coerceIn(0f, 0.16f))

    // ─────────────────────────────────────────
    // ✅ NOUVEAU LAYOUT (2 lignes)
    //   Ligne 1: Titre | %
    //   Ligne 2: Chip mot-clé (en bas)
    // ─────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = WHITE_SOFT.copy(alpha = if (enabled) 0.92f else 0.55f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = true)
            )

            Spacer(Modifier.size(12.dp))

            Text(
                text = "${value.floatValue.roundToInt()}%",
                color = WHITE_SOFT.copy(alpha = if (enabled) 0.95f else 0.55f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }

        Spacer(Modifier.height(8.dp))

        // Chip en bas (plus jamais coupé)
        TraceKeywordChip(
            label = selectedWord ?: "Choisir un mot ▾",
            selected = !selectedWord.isNullOrBlank(),
            enabled = enabled,
            onClick = { if (enabled) showKeywords = true },
            modifier = Modifier

        )

        if (!lockedLabel.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = lockedLabel,
                color = WHITE_SOFT.copy(alpha = 0.42f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    // ✅ plus d’air entre header et slider
    Spacer(modifier = Modifier.height(12.dp))

    // ─────────────────────────────────────────
    // Pastille vivante (slider)
    // ─────────────────────────────────────────
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
                        .graphicsLayer(scaleX = thumbScale, scaleY = thumbScale)
                        .clip(CircleShape)
                        .background(aliveEdge.copy(alpha = if (enabled) 0.98f else 0.60f))
                        .border(
                            width = 7.dp,
                            color = aliveEdge.copy(alpha = (abs(edgeAlpha) + 0.06f * boost).coerceIn(0f, 1f)),
                            shape = CircleShape
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = if (enabled) 0.34f else 0.18f),
                            shape = CircleShape
                        )
                )
            }
        )
    }

    // ✅ plus d’air entre sliders (ça aide tes 4 sliders)
    Spacer(modifier = Modifier.height(18.dp))

    TraceKeywordPickerSheet(
        visible = showKeywords,
        title = title,
        words = TraceKeywordsData.wordsForSlider(sliderKey),
        selectedWord = selectedWord,
        onPick = { picked ->
            selectedWord = picked
            showKeywords = false
        },
        onDismiss = { showKeywords = false }
    )
}