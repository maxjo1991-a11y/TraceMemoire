// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/slider/TraceMoodSliderRow.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.slider

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.squares.TraceSquaresRow
import com.maxjth.tracememoire.ui.theme.*
import com.maxjth.tracememoire.ui.tracejour.components.screen.keywords.*
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random
import rings.*

private data class SliderPersona(
    val breatheMs: Int,
    val wobbleAmpDp: Float,
    val wobbleMs: Int
)

private val GAP_AFTER_SECTION = 18.dp

private fun stableSeed(seedBase: String, cycleKey: String, sliderKey: String, title: String): Int {
    val s = "$seedBase|$cycleKey|$sliderKey|$title"
    return s.fold(0) { acc, c -> acc * 31 + c.code }
}

private fun buildPersona(seed: Int): SliderPersona {
    val r = Random(seed)
    return SliderPersona(
        breatheMs = r.nextInt(2600, 5200),
        wobbleAmpDp = r.nextFloat() * 2.0f + 0.4f,
        wobbleMs = r.nextInt(1900, 4200)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceMoodSliderRow(
    title: String,
    enabled: Boolean,
    isPremium: Boolean,
    lockedLabel: String? = null,
    cycleKey: String,
    seedBase: String,
    sliderKey: String
) {

    val value = rememberSaveable(sliderKey, cycleKey, seedBase) { mutableFloatStateOf(50f) }
    var selectedWord by rememberSaveable(sliderKey, cycleKey, seedBase, "word") { mutableStateOf<String?>(null) }
    var showKeywords by remember { mutableStateOf(false) }
    var note by rememberSaveable(sliderKey, cycleKey, seedBase, "note") { mutableStateOf("") }

    val pct = value.floatValue.roundToInt().coerceIn(0, 100)

    val baseSpec = remember(seedBase, cycleKey, sliderKey) {
        RingEvolution.specForKey(seedBase, cycleKey, sliderKey)
    }

    val stableSpec = remember(baseSpec) {
        baseSpec.copy(
            strokeRatioBase = (baseSpec.strokeRatioBase * 1.65f).coerceIn(0.012f, 0.040f),
            strokeRatioAmp = (baseSpec.strokeRatioAmp * 1.45f).coerceIn(0.004f, 0.028f)
        )
    }

    val persona = remember(seedBase, cycleKey, sliderKey, title) {
        buildPersona(stableSeed(seedBase, cycleKey, sliderKey, title))
    }

    val infinite = rememberInfiniteTransition(label = "sliderAlive_$sliderKey")

    val breathePhase by infinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(
            tween(persona.breatheMs, easing = LinearEasing),
            RepeatMode.Reverse
        )
    )

    val wobblePhase by infinite.animateFloat(
        0f, 1f,
        infiniteRepeatable(
            tween(persona.wobbleMs, easing = LinearEasing),
            RepeatMode.Restart
        )
    )

    var isInteracting by remember { mutableStateOf(false) }

    LaunchedEffect(isInteracting) {
        if (isInteracting) {
            delay(520)
            isInteracting = false
        }
    }

    val boost by animateFloatAsState(
        if (isInteracting) 1f else 0f,
        tween(360, easing = FastOutSlowInEasing)
    )

    val respectRaw = (value.floatValue / 100f).coerceIn(0f, 1f)
    val respect = (0.25f + 0.75f * respectRaw) * (if (enabled) 1f else 0.55f)

    val innerGlow = MAUVE.copy(alpha = (0.08f + 0.06f * breathePhase) * respect)

    val pillBg = Brush.horizontalGradient(
        listOf(
            BG_SOFT.copy(0.20f),
            BG_SOFT.copy(0.24f),
            BG_SOFT.copy(0.20f)
        )
    )

    val outerTurqMain =
        TURQUOISE.copy(alpha = ((0.14f + 0.34f * breathePhase + 0.16f * boost) * respect))

    val outerTurqSoft =
        TURQUOISE.copy(alpha = (0.10f + 0.10f * breathePhase) * respect)

    val wobble = sin(wobblePhase * 2f * PI).toFloat() * persona.wobbleAmpDp
    val thumbOffsetX = if (enabled) (wobble * respect).dp else 0.dp

    val thumbScale =
        (0.98f + 0.05f * breathePhase) *
                (1f + 0.03f * boost) *
                (0.94f + 0.06f * respect)

    val activeTrackColor = MAUVE.copy(alpha = ((0.88f + 0.10f * boost) * respect))
    val inactiveTrackColor = Color.White.copy(alpha = (0.12f * respect))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 10.dp)
    ) {

        Text(
            text = title,
            color = WHITE_SOFT.copy(alpha = if (enabled) 0.92f else 0.55f),
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            TraceKeywordChip(
                label = selectedWord ?: "Définir état ▾",
                selected = !selectedWord.isNullOrBlank(),
                enabled = enabled,
                onClick = { if (enabled) showKeywords = true },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(12.dp))

            TraceRing(
                ringKey = sliderKey,
                percentText = "$pct%",
                sizeDp = 90.dp,
                ringColor = MAUVE.copy(0.96f),
                percentColor = TURQUOISE,
                spec = stableSpec,
                showInnerPercent = true
            )
        }

        if (!lockedLabel.isNullOrBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = lockedLabel,
                color = WHITE_SOFT.copy(0.42f),
                fontSize = 12.sp
            )
        }
    }

    Spacer(Modifier.height(10.dp))

    TraceSquaresRow(
        sliderKey = sliderKey,
        seedBase = seedBase,
        pct = pct,
        sliderValue = value.floatValue,
        blink = false,
        count = 4,
        size = 14.dp,
        gap = 8.dp,
        followAmpY = 6.dp
    )

    Spacer(Modifier.height(12.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(pillBg)
            .border(4.6.dp, outerTurqSoft, RoundedCornerShape(999.dp))
            .border(3.dp, outerTurqMain, RoundedCornerShape(999.dp))
            .background(Brush.horizontalGradient(listOf(innerGlow, Color.Transparent, innerGlow)))
            .padding(horizontal = 15.dp),
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
                inactiveTrackColor = inactiveTrackColor
            )
        )
    }

    val phrase = TraceSliderPhrases.phraseFor(sliderKey, cycleKey)

    Spacer(Modifier.height(10.dp))

    Text(
        text = phrase,
        color = WHITE_SOFT.copy(alpha = if (enabled) 0.72f else 0.50f),
        fontSize = 14.5.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 6.dp, end = 10.dp)
    )

    Spacer(Modifier.height(GAP_AFTER_SECTION))

    TraceKeywordPickerSheet(
        visible = showKeywords,
        title = title,
        words = TraceKeywordsData.wordsForSlider(sliderKey),
        selectedWord = selectedWord,
        note = note,
        onNoteChange = { note = it },
        onPick = {
            selectedWord = it
            showKeywords = false
        },
        onDismiss = { showKeywords = false }
    )
}