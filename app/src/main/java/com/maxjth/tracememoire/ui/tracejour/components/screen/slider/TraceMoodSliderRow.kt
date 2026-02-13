package com.maxjth.tracememoire.ui.tracejour.components.screen.slider

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.squares.TraceSquaresRow
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.screen.phrases.TracePhrasesData
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random
import rings.RingEvolution
import rings.TraceRing

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

    // premium réel de l'utilisateur
    userIsPremium: Boolean,

    // true seulement pour les sliders de la section premium
    isPremiumSlider: Boolean,

    lockedLabel: String? = null,

    // ✅ IMPORTANT : pour les phrases matin/jour/soir/nuit
    phaseKey: String,

    cycleKey: String,
    seedBase: String,
    sliderKey: String
) {
    val value = rememberSaveable(sliderKey, cycleKey, seedBase) { mutableFloatStateOf(50f) }
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
        animationSpec = tween(360, easing = FastOutSlowInEasing),
        label = "boost_$sliderKey"
    )

    val respectRaw = (value.floatValue / 100f).coerceIn(0f, 1f)
    val respect = (0.25f + 0.75f * respectRaw) * (if (enabled) 1f else 0.55f)

    // ───────────── carte glow
    val cardBg = BG_SOFT.copy(alpha = 0.10f)
    val cardBorder = MAUVE.copy(alpha = 0.14f)
    val cardGlow = MAUVE.copy(alpha = (0.06f + 0.04f * breathePhase) * respect)

    // slider pill
    val innerGlow = MAUVE.copy(alpha = (0.08f + 0.06f * breathePhase) * respect)
    val pillBg = Brush.horizontalGradient(
        listOf(
            BG_SOFT.copy(alpha = 0.20f),
            BG_SOFT.copy(alpha = 0.24f),
            BG_SOFT.copy(alpha = 0.20f)
        )
    )

    val outerTurqMain =
        TURQUOISE.copy(alpha = ((0.14f + 0.34f * breathePhase + 0.16f * boost) * respect).coerceIn(0f, 1f))
    val outerTurqSoft =
        TURQUOISE.copy(alpha = ((0.10f + 0.10f * breathePhase) * respect).coerceIn(0f, 1f))

    val wobble = sin(wobblePhase * 2f * PI).toFloat() * persona.wobbleAmpDp
    val thumbOffsetX = if (enabled) (wobble * respect).dp else 0.dp

    val thumbScale =
        (0.98f + 0.05f * breathePhase) *
                (1f + 0.03f * boost) *
                (0.94f + 0.06f * respect)

    val activeTrackColor = MAUVE.copy(alpha = ((0.88f + 0.10f * boost) * respect).coerceIn(0f, 1f))
    val inactiveTrackColor = Color.White.copy(alpha = (0.12f * respect).coerceIn(0f, 0.16f))

    // ✅ Phrase auto (matin/jour/soir/nuit + %)
    val phrase = if (isPremiumSlider && !userIsPremium) {
        "Débloqué avec Premium."
    } else {
        TracePhrasesData.phraseForSlider(
            sliderKey = sliderKey,
            phaseKey = phaseKey,
            percent = pct
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(cardGlow, cardBg, cardGlow)))
            .border(1.dp, cardBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 14.dp, vertical = 16.dp) // ✅ plus d’air
    ) {

        // ✅ TITRE (Humeur globale) — CORRIGÉ
        // ✅ TITRE (centré, droit avec le cercle)
        Text(
            text = title,
            color = WHITE_SOFT.copy(alpha = if (enabled) 0.92f else 0.55f),
            fontSize = 25.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        // ✅ espace titre → cercle (empêche de toucher)
        Spacer(Modifier.height(20.dp))

        // ✅ CERCLE % AU CENTRE (un peu plus haut = pas de gros gap)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TraceRing(
                ringKey = sliderKey,
                percentText = "$pct%",
                sizeDp = 140.dp,
                ringColor = MAUVE.copy(alpha = 0.96f),
                percentColor = TURQUOISE,
                spec = stableSpec,
                showInnerPercent = true
            )
        }

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

        // ✅ phrase : centrée + respiration (pas collée au cercle)
        if (phrase.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = phrase,
                color = WHITE_SOFT.copy(alpha = if (enabled) 0.80f else 0.50f),
                fontSize = 18.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        // ✅ respiration avant le slider
        Spacer(Modifier.height(16.dp))

        // ✅ SLIME / SLIDER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(pillBg)
                .border(2.2.dp, outerTurqSoft, RoundedCornerShape(999.dp))
                .border(1.6.dp, outerTurqMain, RoundedCornerShape(999.dp))
                .background(Brush.horizontalGradient(listOf(innerGlow, Color.Transparent, innerGlow)))
                .padding(horizontal = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Slider(
                value = value.floatValue,
                onValueChange = { newValue ->
                    if (!enabled) return@Slider
                    value.floatValue = newValue
                    isInteracting = true
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
                            .background(MAUVE.copy(alpha = if (enabled) 0.98f else 0.60f))
                            .border(
                                width = 7.dp,
                                color = TURQUOISE.copy(
                                    alpha = ((0.22f + 0.42f * breathePhase + 0.18f * boost) * respect)
                                        .coerceIn(0f, 1f)
                                ),
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

        // ✅ respiration avant les cubes
        Spacer(Modifier.height(12.dp))

        // ✅ PETITS CUBES EN BAS
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

        // ✅ séparateur
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            MAUVE.copy(alpha = (0.12f + 0.10f * breathePhase) * respect),
                            Color.Transparent
                        )
                    )
                )
        )
    }

    Spacer(Modifier.height(GAP_AFTER_SECTION))
}