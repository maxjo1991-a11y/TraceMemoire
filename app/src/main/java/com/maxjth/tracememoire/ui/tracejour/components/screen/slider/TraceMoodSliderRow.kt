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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.ring.PercentRing
import com.maxjth.tracememoire.ui.tracejour.components.ring.accentForPercent
import com.maxjth.tracememoire.ui.tracejour.components.screen.notes.TraceNoteBlock
import com.maxjth.tracememoire.ui.tracejour.components.screen.phrases.TracePhrasesData
import kotlin.math.roundToInt

private val VIOLET_POSITIVE = Color(0xFF8A5CFF)

private const val NOTE_MAX_FREE = 200
private const val NOTE_MAX_PREMIUM = 589

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
    showTitle: Boolean = true
) {
    val locked = isPremiumSlider && !userIsPremium

    val stateKey = remember(seedBase, cycleKey, sliderKey) {
        "$seedBase|$cycleKey|$sliderKey"
    }

    // ✅ Slider state (saveable)
    var value by rememberSaveable(stateKey) { mutableStateOf(0.5f) }

    val pct = (value * 100f).roundToInt().coerceIn(0, 100)
    val sliderEnabled = enabled && !locked

    // ✅ NOTE state (saveable) — 1 note par slider
    val noteKey = remember(stateKey) { "$stateKey|note" }
    var noteText by rememberSaveable(noteKey) { mutableStateOf("") }

    // ✅ DRAG FIABLE
    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()

    // ✅ Accent basé sur %
    val baseAccent = remember(pct) { accentForPercent(pct) }

    // ✅ Petit shift pendant drag (subtil)
    val uiAccent = remember(baseAccent, isDragging) {
        if (isDragging) lerp(baseAccent, VIOLET_POSITIVE, 0.16f) else baseAccent
    }

    val borderColor =
        if (isPremiumSlider) uiAccent.copy(alpha = 0.26f)
        else uiAccent.copy(alpha = 0.18f)

    // ✅ Phrase
    val phrase = remember(pct, sliderKey, phaseKey) {
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
            .background(BG_SOFT.copy(alpha = 0.10f))
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {

        if (showTitle) {
            Text(
                text = title,
                color = WHITE_SOFT.copy(alpha = 0.92f),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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

        Spacer(Modifier.height(10.dp))

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

        // ✅ Slider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(999.dp))
                .background(Color.Black.copy(alpha = 0.14f))
                .border(1.dp, uiAccent.copy(alpha = 0.20f), RoundedCornerShape(999.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Slider(
                value = value,
                onValueChange = { if (sliderEnabled) value = it.coerceIn(0f, 1f) },
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

        // ✅ NOTE — directement sous le slider
        Spacer(Modifier.height(12.dp))
        TraceNoteBlock(
            note = noteText,
            onNoteChange = { txt -> noteText = txt }, // limite gérée dans TraceNoteBlock
            enabled = sliderEnabled,
            accent = uiAccent,
            userIsPremium = userIsPremium,     // ✅ IMPORTANT
            maxCharsFree = NOTE_MAX_FREE,      // ✅ 200
            maxCharsPremium = NOTE_MAX_PREMIUM // ✅ 589
        )

        if (locked) {
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