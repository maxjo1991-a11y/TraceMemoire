// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/TraceJourSlidersBlock.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.components.screen.depth.TraceDepthSection
import com.maxjth.tracememoire.ui.tracejour.components.screen.notes.TraceNoteBlock
import com.maxjth.tracememoire.ui.tracejour.components.screen.slider.TraceMoodSliderRow

private data class SliderDef(val key: String, val title: String)

private val SLIDERS_FREE = listOf(
    SliderDef("humeur", "Humeur globale"),
    SliderDef("energie", "Énergie / rythme"),
    SliderDef("corps", "Corps / sensations"),
    SliderDef("presence", "Présence / attention")
)

private val SLIDERS_PREMIUM = listOf(
    SliderDef("emotion", "Architecture émotionnelle"),
    SliderDef("sommeil", "Qualité du repos vécu"),
    SliderDef("typejour", "Type de journée"),
    SliderDef("motifs", "Motifs psychiques"),
    SliderDef("environ", "Environnement"),
    SliderDef("clarte", "Clarté mentale"),
    SliderDef("charge", "Charge émotionnelle")
)

private val ROW_SPACING = 18.dp
private val SECTION_GAP = 26.dp
private val CARD_RADIUS = 24.dp
private val CARD_MIN_HEIGHT = 86.dp
private val CARD_PADDING = 20.dp
private val OUTER_HORIZONTAL_PADDING = 14.dp

// ✅ TEMP : force visuel Premium (remets false avant release)
private const val DEBUG_FORCE_PREMIUM_VISUALS = true

@Composable
fun TraceJourSlidersBlock(
    enabled: Boolean,
    isPremium: Boolean,
    cycleKey: String,
    seedBase: String,
    modifier: Modifier = Modifier,
    showPremiumLockedRows: Boolean = true,

    // ✅ NOUVEAUX PARAMS (pour ton store + persistance)
    onDirty: (() -> Unit)? = null,
    externalSliderMap: MutableMap<String, Int>? = null,
    externalNoteMap: MutableMap<String, String>? = null,
    externalCapturedMap: MutableMap<String, Boolean>? = null,
    onSliderValue: ((String, Int) -> Unit)? = null,
    onNoteValue: ((String, String) -> Unit)? = null,
    onCaptured: ((String) -> Unit)? = null
) {
    val premiumVisualUnlocked = isPremium || DEBUG_FORCE_PREMIUM_VISUALS
    val premiumEffectiveForPremiumNotes = isPremium || DEBUG_FORCE_PREMIUM_VISUALS

    var openKey by remember { mutableStateOf("humeur") }

    // ✅ si tu passes des maps externes, on les utilise.
    val capturedMap = externalCapturedMap ?: remember { mutableStateMapOf<String, Boolean>() }
    val noteMap = externalNoteMap ?: remember { mutableStateMapOf<String, String>() }

    // ✅ SLIDERS: si map externe absente, fallback local
    val sliderMap = externalSliderMap ?: remember { mutableStateMapOf<String, Int>() }

    // accent local (pas besoin de persister)
    val accentMap = remember { mutableStateMapOf<String, Color>() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = OUTER_HORIZONTAL_PADDING)
    ) {

        // ───────── FREE ─────────
        SLIDERS_FREE.forEachIndexed { index, def ->
            val captured = capturedMap[def.key] == true
            val note = noteMap[def.key] ?: ""
            val accent = accentMap[def.key] ?: TURQUOISE

            // ✅ valeur persistée du store (0..100)
            val persistedPct = sliderMap[def.key] ?: 50

            key(def.key) {
                CollapsibleSliderCard(
                    title = def.title,
                    isOpen = openKey == def.key,
                    captured = captured,
                    onToggle = { openKey = if (openKey == def.key) "" else def.key }
                ) {
                    TraceMoodSliderRow(
                        title = def.title,
                        enabled = enabled,
                        userIsPremium = isPremium,
                        isPremiumSlider = false,
                        lockedLabel = null,
                        phaseKey = cycleKey,
                        cycleKey = cycleKey,
                        seedBase = seedBase,
                        sliderKey = def.key,
                        showTitle = false,

                        // ✅ cercle/slider se recalent sur store
                        externalPercent = persistedPct,
                        onPercentChanged = { newPct ->
                            sliderMap[def.key] = newPct
                            onSliderValue?.invoke(def.key, newPct)
                            onDirty?.invoke()
                        },

                        onCapturedChanged = { hasCaptured ->
                            if (hasCaptured) {
                                capturedMap[def.key] = true
                                onCaptured?.invoke(def.key)
                                onDirty?.invoke()
                            }
                        },
                        onAccentChanged = { c ->
                            accentMap[def.key] = c
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    TraceNoteBlock(
                        note = note,
                        onNoteChange = { newText ->
                            noteMap[def.key] = newText
                            onNoteValue?.invoke(def.key, newText)
                            onDirty?.invoke()
                        },
                        enabled = enabled,
                        accent = accent,
                        userIsPremium = isPremium,
                        title = "Trace écrite",
                        placeholder = "Écrire une note…",
                        lockedLabel = "Espace personnel"
                    )
                }
            }

            if (index != SLIDERS_FREE.lastIndex) {
                Spacer(Modifier.height(ROW_SPACING))
            }
        }

        // ───────── PREMIUM ─────────
        if (isPremium || showPremiumLockedRows) {
            Spacer(Modifier.height(SECTION_GAP))

            TraceDepthSection(
                isPremium = premiumVisualUnlocked,
                modifier = Modifier.fillMaxWidth()
            ) { contentEnabled ->

                val contentOk = if (premiumVisualUnlocked) true else contentEnabled

                SLIDERS_PREMIUM.forEachIndexed { index, def ->
                    val captured = capturedMap[def.key] == true
                    val note = noteMap[def.key] ?: ""
                    val accent = accentMap[def.key] ?: TURQUOISE

                    val persistedPct = sliderMap[def.key] ?: 50

                    key(def.key) {
                        CollapsibleSliderCard(
                            title = def.title,
                            isOpen = openKey == def.key,
                            captured = captured,
                            onToggle = { openKey = if (openKey == def.key) "" else def.key }
                        ) {
                            TraceMoodSliderRow(
                                title = def.title,
                                enabled = enabled && contentOk,
                                userIsPremium = premiumEffectiveForPremiumNotes,
                                isPremiumSlider = true,
                                lockedLabel = null,
                                phaseKey = cycleKey,
                                cycleKey = cycleKey,
                                seedBase = seedBase,
                                sliderKey = def.key,
                                showTitle = false,

                                externalPercent = persistedPct,
                                onPercentChanged = { newPct ->
                                    sliderMap[def.key] = newPct
                                    onSliderValue?.invoke(def.key, newPct)
                                    onDirty?.invoke()
                                },

                                onCapturedChanged = { hasCaptured ->
                                    if (hasCaptured) {
                                        capturedMap[def.key] = true
                                        onCaptured?.invoke(def.key)
                                        onDirty?.invoke()
                                    }
                                },
                                onAccentChanged = { c ->
                                    accentMap[def.key] = c
                                }
                            )

                            Spacer(Modifier.height(12.dp))

                            TraceNoteBlock(
                                note = note,
                                onNoteChange = { newText ->
                                    noteMap[def.key] = newText
                                    onNoteValue?.invoke(def.key, newText)
                                    onDirty?.invoke()
                                },
                                enabled = enabled && contentOk,
                                accent = accent,
                                userIsPremium = premiumEffectiveForPremiumNotes,
                                title = "Trace écrite",
                                placeholder = "Écrire une note…",
                                lockedLabel = "Espace personnel"
                            )
                        }
                    }

                    if (index != SLIDERS_PREMIUM.lastIndex) {
                        Spacer(Modifier.height(ROW_SPACING))
                    }
                }
            }
        }
    }
}

@Composable
private fun CollapsibleSliderCard(
    title: String,
    isOpen: Boolean,
    captured: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(CARD_RADIUS)
    val interaction = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MAUVE.copy(alpha = 0.22f),
                            MAUVE.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    ),
                    shape = shape
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            TURQUOISE.copy(alpha = 0.10f),
                            Color.Transparent,
                            TURQUOISE.copy(alpha = 0.10f)
                        )
                    ),
                    shape = shape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = CARD_MIN_HEIGHT)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            BG_SOFT.copy(alpha = 0.26f),
                            BG_SOFT.copy(alpha = 0.18f)
                        )
                    ),
                    shape = shape
                )
                .border(1.5.dp, MAUVE.copy(alpha = 0.28f), shape)
                .padding(1.dp)
                .border(1.dp, TURQUOISE.copy(alpha = 0.12f), shape)
                .padding(CARD_PADDING)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable(
                        interactionSource = interaction,
                        indication = null
                    ) { onToggle() }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        color = WHITE_SOFT.copy(alpha = 0.94f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (captured) {
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(MAUVE.copy(alpha = 0.95f))
                            )
                            Spacer(Modifier.size(8.dp))
                            Text(
                                text = "Mémoire créée",
                                color = MAUVE.copy(alpha = 0.90f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isOpen -> TURQUOISE.copy(alpha = 0.90f)
                                captured -> MAUVE.copy(alpha = 0.28f)
                                else -> Color.Transparent
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = TURQUOISE.copy(alpha = 0.65f),
                            shape = CircleShape
                        )
                )
            }

            if (isOpen) {
                Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}