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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.ring.accentForPercent
import com.maxjth.tracememoire.ui.tracejour.components.screen.header.TraceMemoryStamp
import com.maxjth.tracememoire.ui.tracejour.components.screen.lock.TraceLockConfirmButton
import com.maxjth.tracememoire.ui.tracejour.components.screen.phrases.TracePhrasesData
import kotlin.math.abs
import kotlin.math.roundToInt

private val VIOLET_POSITIVE = Color(0xFF8A5CFF)
private val TURQUOISE_LIVE = Color(0xFF35D6D1)

data class TraceLockPayload(
    val seedBase: String,
    val cycleKey: String,
    val sliderKey: String,
    val percent: Int,
    val phaseKey: String
)

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

@OptIn(ExperimentalMaterial3Api::class)
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
    forceCenterIfNotCaptured: Boolean = false,
    onDragStateChanged: (Boolean) -> Unit = {},
    onCapturedChanged: (Boolean) -> Unit = {},
    onAccentChanged: (Color) -> Unit = {},
    onPhraseChanged: ((String) -> Unit)? = null,
    onKeywordChanged: ((String) -> Unit)? = null,
    externalPercent: Int? = null,
    onPercentChanged: ((Int) -> Unit)? = null,
    externalCaptured: Boolean? = null,
    externalCreatedAtMillis: Long? = null,
    externalLocked: Boolean? = null,
    onLock: (TraceLockPayload) -> Unit
) {
    // MODE TEST : TOUT DÉVERROUILLÉ
    val premiumLocked = false
    val cycleLocked = false
    val fullyLocked = false

    val stateKey = remember(seedBase, cycleKey, sliderKey) {
        "$seedBase|$cycleKey|$sliderKey"
    }

    var value by rememberSaveable(stateKey) { mutableFloatStateOf(0f) }
    var capturedLocal by rememberSaveable("${stateKey}|captured") { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()

    LaunchedEffect(isDragging) {
        onDragStateChanged(isDragging)
    }

    LaunchedEffect(externalCaptured) {
        if (externalCaptured != null) {
            capturedLocal = externalCaptured
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

    val controlledKeyword = remember(pct, sliderKey, phaseKey) {
        TracePhrasesData.keywordForSlider(
            sliderKey = sliderKey,
            phaseKey = phaseKey,
            percent = pct
        )
    }

    LaunchedEffect(phrase) {
        onPhraseChanged?.invoke(phrase)
    }

    LaunchedEffect(controlledKeyword) {
        onKeywordChanged?.invoke(controlledKeyword)
    }

    val infinite = rememberInfiniteTransition(label = "slider_live")

    val gradientShift by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_shift"
    )

    val restingGlow by infinite.animateFloat(
        initialValue = 0.035f,
        targetValue = 0.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2600,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "resting_glow"
    )

    val dragGlow by animateFloatAsState(
        targetValue = if (isDragging) 0.16f else restingGlow,
        animationSpec = tween(220),
        label = "drag_glow"
    )

    val thumbScale by animateFloatAsState(
        targetValue = if (isDragging) 1.12f else 1f,
        animationSpec = tween(180),
        label = "thumb_scale"
    )

    val outerScale by animateFloatAsState(
        targetValue = if (isDragging) 1.012f else 1f,
        animationSpec = tween(220),
        label = "outer_scale"
    )

    val pillShape = RoundedCornerShape(999.dp)
    val liveStart = lerp(VIOLET_POSITIVE, uiAccent, 0.35f)
    val liveMiddle = lerp(uiAccent, TURQUOISE_LIVE, 0.45f)
    val liveEnd = lerp(TURQUOISE_LIVE, uiAccent, 0.35f)

    val sliderBackgroundBrush = Brush.horizontalGradient(
        colorStops = arrayOf(
            0.00f to Color.Black.copy(alpha = 0.14f),
            (0.22f + gradientShift * 0.10f).coerceIn(0f, 1f) to liveStart.copy(alpha = dragGlow),
            (0.50f + gradientShift * 0.08f).coerceIn(0f, 1f) to liveMiddle.copy(alpha = dragGlow * 0.95f),
            (0.78f + gradientShift * 0.06f).coerceIn(0f, 1f) to liveEnd.copy(alpha = dragGlow * 0.90f),
            1.00f to Color.Black.copy(alpha = 0.14f)
        )
    )

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
                    color = WHITE_SOFT.copy(alpha = 0.94f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                PercentBadgeDiamond(
                    percent = pct,
                    accent = uiAccent
                )
            }

            Spacer(Modifier.size(8.dp))

            TraceMemoryStamp(
                captured = capturedLocal,
                createdAtMillis = externalCreatedAtMillis,
                locked = cycleLocked,
                onLock = null,
                showLockChip = true
            )

            Spacer(Modifier.size(12.dp))
        }

        Text(
            text = phrase,
            color = WHITE_SOFT.copy(alpha = 0.94f),
            fontSize = 21.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 25.sp
        )

        Spacer(Modifier.size(8.dp))

        if (controlledKeyword.isNotBlank()) {
            Text(
                text = controlledKeyword,
                color = uiAccent.copy(alpha = 0.88f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.size(12.dp))
        } else {
            Spacer(Modifier.size(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = outerScale
                    scaleY = outerScale
                }
                .clip(pillShape)
                .background(sliderBackgroundBrush)
                .border(
                    width = 1.2.dp,
                    color = uiAccent.copy(alpha = if (isDragging) 0.26f else 0.16f),
                    shape = pillShape
                )
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
                thumb = {
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = thumbScale
                                scaleY = thumbScale
                            }
                            .size(if (isDragging) 28.dp else 24.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        uiAccent.copy(alpha = if (isDragging) 1f else 0.96f),
                                        uiAccent.copy(alpha = if (isDragging) 0.84f else 0.72f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = if (isDragging) 0.20f else 0.10f),
                                shape = RoundedCornerShape(999.dp)
                            )
                    )
                },
                colors = SliderDefaults.colors(
                    activeTrackColor = uiAccent.copy(alpha = if (isDragging) 0.94f else 0.70f),
                    inactiveTrackColor = Color.White.copy(alpha = 0.10f),
                    thumbColor = Color.Transparent,
                    disabledActiveTrackColor = uiAccent.copy(alpha = 0.38f),
                    disabledInactiveTrackColor = Color.White.copy(alpha = 0.08f),
                    disabledThumbColor = Color.Transparent
                )
            )
        }

        if (!premiumLocked) {
            Spacer(Modifier.size(12.dp))

            TraceLockConfirmButton(
                text = if (cycleLocked) "VALIDÉ" else "VERROUILLER",
                enabled = if (cycleLocked) false else sliderEnabled && capturedLocal,
                onClick = {
                    if (!cycleLocked) {
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