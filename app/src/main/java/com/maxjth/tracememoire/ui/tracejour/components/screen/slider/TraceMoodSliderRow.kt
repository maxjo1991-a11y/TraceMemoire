package com.maxjth.tracememoire.ui.tracejour.components.screen.slider

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.ring.accentForPercent
import com.maxjth.tracememoire.ui.tracejour.components.screen.dots.TraceDotState
import com.maxjth.tracememoire.ui.tracejour.components.screen.dots.TraceStatusDot
import com.maxjth.tracememoire.ui.tracejour.components.screen.header.TraceMemoryStamp
import com.maxjth.tracememoire.ui.tracejour.components.screen.lock.TraceLockConfirmButton
import com.maxjth.tracememoire.ui.tracejour.components.screen.phrases.TracePhrasesData
import kotlin.math.abs
import kotlin.math.roundToInt

private val VIOLET_POSITIVE = Color(0xFF8A5CFF)

data class TraceLockPayload(
    val seedBase: String,
    val cycleKey: String,
    val sliderKey: String,
    val percent: Int,
    val phaseKey: String
)

private fun extractKeywordFromPhrase(phrase: String): String {
    val stopWords = setOf(
        "de", "du", "des", "la", "le", "les", "un", "une", "et",
        "très", "plus", "peu", "assez", "dans", "sur", "avec", "sans",
        "au", "aux", "en", "du", "moment", "soirée", "journée", "nuit", "matinée"
    )

    val cleanedWords = phrase
        .replace("’", "'")
        .replace(",", " ")
        .replace(".", " ")
        .replace(";", " ")
        .replace(":", " ")
        .split(" ", "'", "-", "•")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.lowercase() }

    val best = cleanedWords.lastOrNull { it !in stopWords && it.length >= 3 }
        ?: cleanedWords.lastOrNull()
        ?: ""

    return best.replaceFirstChar { c ->
        if (c.isLowerCase()) c.titlecase() else c.toString()
    }
}

@Composable
private fun PercentBadgeDiamond(
    percent: Int,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(9.dp)

    Box(
        modifier = modifier
            .size(34.dp)
            .graphicsLayerCompat(45f)
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.20f))
            .border(1.2.dp, accent.copy(alpha = 0.30f), shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$percent",
            color = WHITE_SOFT.copy(alpha = 0.92f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.graphicsLayerCompat(-45f)
        )
    }
}

@Composable
fun TraceMoodSliderRow(
    title: String,
    enabled: Boolean,
    userIsPremium: Boolean,
    isPremiumSlider: Boolean,
    lockedLabel: String?,
    phaseKey: String,
    cycleKey: String,
    seedBase: String,
    sliderKey: String,

    showTitle: Boolean = true,

    // toggles
    showRing: Boolean = true, // gardé pour compatibilité, mais plus utilisé
    forceCenterIfNotCaptured: Boolean = false,
    onDragStateChanged: (Boolean) -> Unit = {},

    onCapturedChanged: (Boolean) -> Unit = {},
    onAccentChanged: (Color) -> Unit = {},

    // mémoire future
    onPhraseChanged: ((String) -> Unit)? = null,
    onKeywordChanged: ((String) -> Unit)? = null,

    // valeur persistée venant du store (0..100)
    externalPercent: Int? = null,
    onPercentChanged: ((Int) -> Unit)? = null,

    // store : affichage + lock
    externalCaptured: Boolean? = null,
    externalCreatedAtMillis: Long? = null,
    externalLocked: Boolean? = null,

    onLock: (TraceLockPayload) -> Unit
) {
    val premiumLocked = isPremiumSlider && !userIsPremium
    val cycleLocked = (externalLocked == true)
    val fullyLocked = premiumLocked || cycleLocked

    val stateKey = remember(seedBase, cycleKey, sliderKey) {
        "$seedBase|$cycleKey|$sliderKey"
    }

    var value by rememberSaveable(stateKey) { mutableFloatStateOf(0f) }
    var capturedLocal by rememberSaveable(stateKey + "|captured") { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()

    LaunchedEffect(isDragging) {
        onDragStateChanged(isDragging)
    }

    LaunchedEffect(externalCaptured) {
        if (externalCaptured != null) {
            capturedLocal = externalCaptured == true
        }
    }

    LaunchedEffect(externalPercent) {
        val p = externalPercent ?: return@LaunchedEffect
        if (!isDragging) {
            value = (p.coerceIn(0, 100) / 100f).coerceIn(0f, 1f)
        }
    }

    LaunchedEffect(forceCenterIfNotCaptured, capturedLocal) {
        if (forceCenterIfNotCaptured && !capturedLocal && !isDragging) {
            value = 0.5f
        }
    }

    val pct = (value * 100f).roundToInt().coerceIn(0, 100)
    val sliderEnabled = enabled && !fullyLocked

    val baseAccent = remember(pct) { accentForPercent(pct) }
    val uiAccent = remember(baseAccent, isDragging) {
        if (isDragging) lerp(baseAccent, VIOLET_POSITIVE, 0.30f) else baseAccent
    }

    LaunchedEffect(uiAccent) {
        onAccentChanged(uiAccent)
    }

    val borderColor = when {
        premiumLocked -> Color.White.copy(alpha = 0.06f)
        isPremiumSlider -> uiAccent.copy(alpha = 0.10f)
        else -> uiAccent.copy(alpha = 0.18f)
    }

    val phrase = remember(pct, sliderKey, phaseKey) {
        TracePhrasesData.phraseForSlider(
            sliderKey = sliderKey,
            phaseKey = phaseKey,
            percent = pct
        )
    }

    val autoKeyword = remember(phrase) {
        extractKeywordFromPhrase(phrase)
    }

    LaunchedEffect(phrase) {
        onPhraseChanged?.invoke(phrase)
    }

    LaunchedEffect(autoKeyword) {
        onKeywordChanged?.invoke(autoKeyword)
    }

    val dotState = when {
        premiumLocked -> TraceDotState.DISABLED
        capturedLocal -> TraceDotState.ON
        else -> TraceDotState.OFF
    }

    val cardShape = RoundedCornerShape(18.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(Color.Black.copy(alpha = 0.14f))
            .border(1.dp, borderColor, cardShape)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        if (showTitle) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = WHITE_SOFT.copy(alpha = 0.92f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                PercentBadgeDiamond(
                    percent = pct,
                    accent = uiAccent,
                    modifier = Modifier.padding(end = 10.dp)
                )

                TraceStatusDot(state = dotState)
            }

            Spacer(Modifier.size(8.dp))

            TraceMemoryStamp(
                captured = capturedLocal,
                createdAtMillis = externalCreatedAtMillis,
                locked = cycleLocked,
                onLock = null,
                showLockChip = true
            )

            Spacer(Modifier.size(10.dp))
        }

        Text(
            text = "$pct%",
            color = WHITE_SOFT.copy(alpha = 0.96f),
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                shadow = Shadow(
                    color = uiAccent.copy(alpha = 0.18f),
                    offset = Offset.Zero,
                    blurRadius = 12f
                )
            )
        )

        Spacer(Modifier.size(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            uiAccent.copy(alpha = 0.10f),
                            Color.White.copy(alpha = 0.03f)
                        )
                    )
                )
                .border(
                    1.dp,
                    uiAccent.copy(alpha = 0.14f),
                    RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = phrase,
                color = WHITE_SOFT.copy(alpha = 0.92f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 24.sp
            )
        }

        Spacer(Modifier.size(8.dp))

        if (autoKeyword.isNotBlank()) {
            Text(
                text = autoKeyword,
                color = uiAccent.copy(alpha = 0.86f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.size(10.dp))
        } else {
            Spacer(Modifier.size(6.dp))
        }

        val pillShape = RoundedCornerShape(999.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(pillShape)
                .background(Color.Black.copy(alpha = 0.12f))
                .border(1.4.dp, uiAccent.copy(alpha = 0.18f), pillShape)
                .padding(horizontal = 6.dp, vertical = 6.dp)
        ) {
            Slider(
                value = value,
                onValueChange = { newValue ->
                    if (!sliderEnabled) return@Slider

                    val v = newValue.coerceIn(0f, 1f)
                    value = v

                    val newPct = (v * 100f).roundToInt().coerceIn(0, 100)
                    onPercentChanged?.invoke(newPct)

                    if (!capturedLocal && abs(v - 0f) >= 0.01f) {
                        capturedLocal = true
                        onCapturedChanged(true)
                    }
                },
                enabled = sliderEnabled,
                valueRange = 0f..1f,
                interactionSource = interactionSource,
                colors = SliderDefaults.colors(
                    activeTrackColor = uiAccent.copy(alpha = if (isDragging) 0.85f else 0.62f),
                    inactiveTrackColor = Color.White.copy(alpha = 0.10f),
                    thumbColor = uiAccent.copy(alpha = if (isDragging) 1.00f else 0.92f)
                )
            )
        }

        if (!premiumLocked && !cycleLocked) {
            Spacer(Modifier.size(10.dp))

            TraceLockConfirmButton(
                text = "VERROUILLER",
                enabled = sliderEnabled && capturedLocal,
                onClick = {
                    onLock(
                        TraceLockPayload(
                            seedBase = seedBase,
                            cycleKey = cycleKey,
                            sliderKey = sliderKey,
                            percent = pct,
                            phaseKey = phaseKey
                        )
                    )
                }
            )
        }

        if (premiumLocked) {
            Spacer(Modifier.size(10.dp))

            Text(
                text = lockedLabel ?: "Débloqué avec Premium.",
                color = WHITE_SOFT.copy(alpha = 0.55f),
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun Modifier.graphicsLayerCompat(rotation: Float): Modifier {
    return this.graphicsLayer(rotationZ = rotation)
}
