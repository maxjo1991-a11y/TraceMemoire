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
import com.maxjth.tracememoire.ui.tracejour.components.screen.phrases.TracePhrasesData
import kotlin.math.abs
import kotlin.math.roundToInt

private val VIOLET_POSITIVE = Color(0xFF8A5CFF)

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
    onCapturedChanged: (Boolean) -> Unit = {},
    onAccentChanged: (Color) -> Unit = {},

    // ✅ valeur persistée venant du store (0..100)
    externalPercent: Int? = null,
    onPercentChanged: ((Int) -> Unit)? = null,

    // ✅ NOUVEAU (store) : affichage + lock one-shot
    externalCaptured: Boolean? = null,
    externalCreatedAtMillis: Long? = null,
    externalLocked: Boolean? = null,
    onLock: (() -> Unit)? = null
) {
    // 🔒 lock Premium (comme avant)
    val premiumLocked = isPremiumSlider && !userIsPremium

    // 🔒 lock “one-shot” par carte (store)
    val cycleLocked = (externalLocked == true)

    // 🔒 lock final
    val fullyLocked = premiumLocked || cycleLocked

    val stateKey = remember(seedBase, cycleKey, sliderKey) {
        "$seedBase|$cycleKey|$sliderKey"
    }

    // Valeur UI (0..1)
    var value by rememberSaveable(stateKey) { mutableFloatStateOf(0.5f) }

    // captured local (fallback) — mais on préfère externalCaptured si fourni
    var capturedLocal by rememberSaveable(stateKey + "|captured") { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()

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

            // Si le store a déjà capturé, capturedLocal sera synchronisé via externalCaptured.
            // Sinon on garde la logique locale (si p != 50) pour un fallback.
            if (externalCaptured == null && !capturedLocal && p != 50) {
                capturedLocal = true
                onCapturedChanged(true)
            }
        }
    }

    val pct = (value * 100f).roundToInt().coerceIn(0, 100)
    val sliderEnabled = enabled && !fullyLocked

    val baseAccent = remember(pct) { accentForPercent(pct) }
    val uiAccent = remember(baseAccent, isDragging) {
        if (isDragging) lerp(baseAccent, VIOLET_POSITIVE, 0.30f) else baseAccent
    }

    // ✅ Remonte l’accent au parent
    LaunchedEffect(uiAccent) { onAccentChanged(uiAccent) }

    val borderColor =
        if (isPremiumSlider) uiAccent.copy(alpha = 0.0f)
        else uiAccent.copy(alpha = 0.18f)

    val phrase = remember(pct, sliderKey, phaseKey) {
        TracePhrasesData.phraseForSlider(
            sliderKey = sliderKey,
            phaseKey = phaseKey,
            percent = pct
        )
    }

    // ✅ DOT STATE
    val dotState = when {
        premiumLocked -> TraceDotState.DISABLED
        capturedLocal -> TraceDotState.ON
        else -> TraceDotState.OFF
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Black.copy(alpha = 0.14f))
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
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

                TraceStatusDot(state = dotState)
            }

            // ✅ STAMP (Mémoire créée + date/heure + Cycle verrouillé + chip Verrouiller)
            Spacer(Modifier.height(8.dp))
            TraceMemoryStamp(
                captured = capturedLocal,
                createdAtMillis = externalCreatedAtMillis,
                locked = cycleLocked,
                onLock = if (!premiumLocked && !cycleLocked) onLock else null,
                showLockChip = (!premiumLocked && !cycleLocked)
            )

            Spacer(Modifier.height(10.dp))
        }

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

        Text(
            text = phrase,
            color = WHITE_SOFT.copy(alpha = 0.88f),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .background(Color.Black.copy(alpha = 0.12f))
                .border(1.6.dp, uiAccent.copy(alpha = 0.20f), RoundedCornerShape(10.dp))
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

                    if (!capturedLocal && abs(v - 0.5f) >= 0.01f) {
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