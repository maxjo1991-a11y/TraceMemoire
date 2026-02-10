// BLOC 2 — FICHIER MIS À JOUR (COMPLET)
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
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.common.TracePercentBadge
import com.maxjth.tracememoire.ui.tracejour.components.screen.keywords.TraceKeywordChip
import com.maxjth.tracememoire.ui.tracejour.components.screen.keywords.TraceKeywordPickerSheet
import com.maxjth.tracememoire.ui.tracejour.components.screen.keywords.TraceKeywordsData
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

private data class SliderPersona(
    val breatheMs: Int,
    val haloBase: Float,
    val haloAmp: Float,
    val wobbleAmpDp: Float,
    val wobbleMs: Int
)

private fun stableSeed(seedBase: String, cycleKey: String, sliderKey: String, title: String): Int {
    val s = "$seedBase|$cycleKey|$sliderKey|$title"
    return s.fold(0) { acc, c -> acc * 31 + c.code }
}

private fun buildPersona(seed: Int): SliderPersona {
    val r = Random(seed)
    return SliderPersona(
        breatheMs = r.nextInt(2600, 5200),
        haloBase = r.nextFloat() * 0.10f + 0.14f,
        haloAmp = r.nextFloat() * 0.10f + 0.06f,
        wobbleAmpDp = r.nextFloat() * 2.0f + 0.4f,
        wobbleMs = r.nextInt(1900, 4200)
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
    // ─────────────────────────────────────────
    // ÉTATS
    // ─────────────────────────────────────────
    val value = rememberSaveable(title) { mutableFloatStateOf(50f) }

    var selectedWord by rememberSaveable(sliderKey, cycleKey, seedBase) { mutableStateOf<String?>(null) }
    var showKeywords by remember { mutableStateOf(false) }

    // NOTE (pour ton Sheet — corrige l’erreur "note/onNoteChange")
    var note by rememberSaveable(sliderKey, cycleKey, seedBase, "note") { mutableStateOf("") }

    // ─────────────────────────────────────────
    // “VIE” (respiration + wobble)
    // ─────────────────────────────────────────
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
        animationSpec = tween(durationMillis = 360, easing = LinearEasing),
        label = "boost_$sliderKey"
    )

    val pct = value.floatValue.roundToInt().coerceIn(0, 100)

    // ─────────────────────────────────────────
    // COULEURS / STYLE
    // ─────────────────────────────────────────
    val respectRaw = (value.floatValue / 100f).coerceIn(0f, 1f)
    val respect = (0.25f + 0.75f * respectRaw) * (if (enabled) 1f else 0.55f)

    // Pastille: pas turquoise au milieu.
    // -> intérieur sombre + glow mauve léger
    // -> contour turquoise avec relief
    val innerGlow = MAUVE.copy(alpha = (0.08f + 0.06f * breathePhase) * respect)

    val pillBg = Brush.horizontalGradient(
        colors = listOf(
            BG_SOFT.copy(alpha = 0.20f),
            BG_SOFT.copy(alpha = 0.24f),
            BG_SOFT.copy(alpha = 0.20f)
        )
    )

    val outerTurqMain = TURQUOISE.copy(alpha = ((0.14f + 0.34f * breathePhase + 0.16f * boost) * respect).coerceIn(0f, 1f))
    val outerTurqSoft = TURQUOISE.copy(alpha = (0.10f + 0.10f * breathePhase) * respect)

    val wobble = sin(wobblePhase * 2f * PI).toFloat() * persona.wobbleAmpDp
    val thumbOffsetX = if (enabled) (wobble * respect).dp else 0.dp

    val thumbScale =
        (0.98f + 0.05f * breathePhase) * (1f + 0.03f * boost) * (0.94f + 0.06f * respect)

    val activeTrackColor = MAUVE.copy(alpha = ((0.88f + 0.10f * boost) * respect).coerceIn(0f, 1f))
    val inactiveTrackColor = Color.White.copy(alpha = (0.12f * respect).coerceIn(0f, 0.16f))

    // ─────────────────────────────────────────
    // HEADER (2 lignes)
    // L1: Titre + badge %
    // L2: Chip mot-clé (contour turquoise)
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

            // Badge % (contour mauve) — ton composant TracePercentBadge
            TracePercentBadge(
                percent = pct,
                enabled = enabled
            )
        }

        Spacer(Modifier.height(10.dp))

        TraceKeywordChip(
            label = selectedWord ?: "Choisir un mot ▾",
            selected = !selectedWord.isNullOrBlank(),
            enabled = enabled,
            onClick = { if (enabled) showKeywords = true },
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .border(
                    width = 1.dp,
                    color = TURQUOISE.copy(alpha = if (enabled) 0.75f else 0.35f),
                    shape = RoundedCornerShape(999.dp)
                )
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

    Spacer(modifier = Modifier.height(12.dp))

    // ─────────────────────────────────────────
    // PASTILLE (slider)
    // contour turquoise + relief, pas turquoise au milieu
    // ─────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(pillBg)
            // relief 1: contour doux
            .border(
                width = 1.dp,
                color = outerTurqSoft,
                shape = RoundedCornerShape(999.dp)
            )
            // relief 2: contour principal
            .border(
                width = 1.dp,
                color = outerTurqMain,
                shape = RoundedCornerShape(999.dp)
            )
            // glow interne mauve (léger)
            .background(
                Brush.horizontalGradient(
                    listOf(innerGlow, Color.Transparent, innerGlow)
                )
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
                        // centre mauve
                        .background(MAUVE.copy(alpha = if (enabled) 0.98f else 0.60f))
                        // halo turquoise (relief)
                        .border(
                            width = 7.dp,
                            color = TURQUOISE.copy(
                                alpha = ((0.14f + 0.34f * breathePhase + 0.14f * boost) * respect).coerceIn(0f, 1f)
                            ),
                            shape = CircleShape
                        )
                        // fin liseré blanc
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = if (enabled) 0.34f else 0.18f),
                            shape = CircleShape
                        )
                )
            }
        )
    }

    // ─────────────────────────────────────────
    // ✅ PHRASE SOUS LE SLIDER (OFFICIELLE)
    // + mini séparateur glow mauve ultra léger (look “premium”)
    // ─────────────────────────────────────────
    val phrase = TraceSliderPhrases.phraseFor(sliderKey = sliderKey, cycleKey = cycleKey)

    Spacer(modifier = Modifier.height(10.dp))

    // mini séparateur glow mauve
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MAUVE.copy(alpha = (0.10f + 0.08f * breathePhase) * respect),
                        Color.Transparent
                    )
                )
            )
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = phrase,
        color = WHITE_SOFT.copy(alpha = if (enabled) 0.72f else 0.50f),
        fontSize = 12.5.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 6.dp, end = 10.dp)
    )

    // plus d’air entre sliders
    Spacer(modifier = Modifier.height(18.dp))

    // ─────────────────────────────────────────
    // SHEET MOTS-CLÉS (+ NOTE) — corrige tes erreurs build
    // ─────────────────────────────────────────
    TraceKeywordPickerSheet(
        visible = showKeywords,
        title = title,
        words = TraceKeywordsData.wordsForSlider(sliderKey),
        selectedWord = selectedWord,
        note = note,
        onNoteChange = { note = it },
        onPick = { picked ->
            selectedWord = picked
            showKeywords = false
        },
        onDismiss = { showKeywords = false }
    )
}