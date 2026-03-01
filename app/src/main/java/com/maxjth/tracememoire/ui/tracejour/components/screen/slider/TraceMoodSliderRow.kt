// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/slider/TraceMoodSliderRow.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen.slider

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.ring.PercentRing
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
            .graphicsLayer { rotationZ = 45f }
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
            modifier = Modifier.graphicsLayer { rotationZ = -45f }
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
    showRing: Boolean = true,
    forceCenterIfNotCaptured: Boolean = false,
    onDragStateChanged: (Boolean) -> Unit = {},

    onCapturedChanged: (Boolean) -> Unit = {},
    onAccentChanged: (Color) -> Unit = {},

    // valeur persistée venant du store (0..100)
    externalPercent: Int? = null,
    onPercentChanged: ((Int) -> Unit)? = null,

    // store : affichage + lock
    externalCaptured: Boolean? = null,
    externalCreatedAtMillis: Long? = null,
    externalLocked: Boolean? = null,

    onLock: (TraceLockPayload) -> Unit
) {
    // 🔒 lock Premium
    val premiumLocked = isPremiumSlider && !userIsPremium

    // 🔒 lock one-shot (store)
    val cycleLocked = (externalLocked == true)

    // 🔒 lock final
    val fullyLocked = premiumLocked || cycleLocked

    val stateKey = remember(seedBase, cycleKey, sliderKey) {
        "$seedBase|$cycleKey|$sliderKey"
    }

    // ✅ IMPORTANT : default UI = 0 (plus 50)
    var value by rememberSaveable(stateKey) { mutableFloatStateOf(0f) }

    // captured local (fallback)
    var capturedLocal by rememberSaveable(stateKey + "|captured") { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()

    LaunchedEffect(isDragging) { onDragStateChanged(isDragging) }

    // ✅ SYNC captured (store -> UI)
    LaunchedEffect(externalCaptured) {
        if (externalCaptured != null) {
            capturedLocal = externalCaptured == true
        }
    }

    // ✅ SYNC percent (store -> UI)
    LaunchedEffect(externalPercent) {
        val p = externalPercent ?: return@LaunchedEffect
        if (!isDragging) {
            val v = (p.coerceIn(0, 100) / 100f).coerceIn(0f, 1f)
            value = v

            // ✅ IMPORTANT : ne PAS auto-capturer juste parce que p != 50 (on est rendu à 0)
            // c’est l’utilisateur qui “capte” en bougeant le slider.
        }
    }

    // Option : forcer center tant que pas capturé (si tu utilises encore ce mode)
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

    LaunchedEffect(uiAccent) { onAccentChanged(uiAccent) }

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
            .padding(14.dp)
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

            Spacer(Modifier.height(8.dp))

            // ✅ FIX : on affiche le chip “Cycle verrouillé”
            TraceMemoryStamp(
                captured = capturedLocal,
                createdAtMillis = externalCreatedAtMillis,
                locked = cycleLocked,
                onLock = null,
                showLockChip = true
            )

            Spacer(Modifier.height(10.dp))
        }

        if (showRing) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                PercentRing(
                    ringKey = sliderKey,
                    percent = pct,
                    accent = uiAccent,
                    isDragging = isDragging
                )
            }

            Spacer(Modifier.height(35.dp))
        }

        Text(
            text = phrase,
            color = WHITE_SOFT.copy(alpha = 0.88f),
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(12.dp))

        val pillShape = RoundedCornerShape(999.dp)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(pillShape)
                .background(Color.Black.copy(alpha = 0.12f))
                .border(1.6.dp, uiAccent.copy(alpha = 0.20f), pillShape)
                .padding(horizontal = 6.dp, vertical = 7.dp)
        ) {
            Slider(
                value = value,
                onValueChange = { newValue ->
                    if (!sliderEnabled) return@Slider

                    val v = newValue.coerceIn(0f, 1f)
                    value = v

                    val newPct = (v * 100f).roundToInt().coerceIn(0, 100)
                    onPercentChanged?.invoke(newPct)

                    // ✅ capture = l’utilisateur bouge vraiment
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

        // ✅ BOUTON VERROUILLER
        if (!premiumLocked && !cycleLocked) {
            Spacer(Modifier.height(10.dp))
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
            Spacer(Modifier.height(10.dp))
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