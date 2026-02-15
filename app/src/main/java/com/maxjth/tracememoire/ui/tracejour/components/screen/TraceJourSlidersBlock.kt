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
import com.maxjth.tracememoire.ui.tracejour.components.screen.dots.TraceDotState
import com.maxjth.tracememoire.ui.tracejour.components.screen.dots.TraceStatusDot
import com.maxjth.tracememoire.ui.tracejour.components.screen.header.TraceMemoryStamp
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

    // ✅ store + persistance
    onDirty: (() -> Unit)? = null,
    externalSliderMap: MutableMap<String, Int>? = null,
    externalNoteMap: MutableMap<String, String>? = null,
    externalCapturedMap: MutableMap<String, Boolean>? = null,

    // ✅ NOUVEAU : maps pour date/heure + verrouillage
    externalCreatedAtMap: MutableMap<String, Long>? = null,
    externalLockedMap: MutableMap<String, Boolean>? = null,

    onSliderValue: ((String, Int) -> Unit)? = null,
    onNoteValue: ((String, String) -> Unit)? = null,

    // ✅ IMPORTANT : onCaptured doit être branché à saveStore.markCaptured(key)
    onCaptured: ((String) -> Unit)? = null,

    // ✅ NOUVEAU : verrouiller une carte
    onLock: ((String) -> Unit)? = null
) {
    val premiumVisualUnlocked = isPremium || DEBUG_FORCE_PREMIUM_VISUALS
    val premiumEffectiveForPremiumNotes = isPremium || DEBUG_FORCE_PREMIUM_VISUALS

    var openKey by remember { mutableStateOf("humeur") }

    // ✅ maps externes si dispo
    val capturedMap = externalCapturedMap ?: remember { mutableStateMapOf<String, Boolean>() }
    val noteMap = externalNoteMap ?: remember { mutableStateMapOf<String, String>() }
    val sliderMap = externalSliderMap ?: remember { mutableStateMapOf<String, Int>() }

    // ✅ NOUVEAU
    val createdAtMap = externalCreatedAtMap ?: remember { mutableStateMapOf<String, Long>() }
    val lockedMap = externalLockedMap ?: remember { mutableStateMapOf<String, Boolean>() }

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
            val persistedPct = sliderMap[def.key] ?: 50

            val createdAtMillis: Long? =
                createdAtMap[def.key]?.takeIf { it > 0L }

            val isLockedCard: Boolean =
                lockedMap[def.key] == true

            // ✅ si carte verrouillée => plus modifiable
            val enabledCard = enabled && !isLockedCard

            key(def.key) {
                CollapsibleSliderCard(
                    title = def.title,
                    isOpen = openKey == def.key,
                    captured = captured,
                    createdAtMillis = createdAtMillis,
                    locked = isLockedCard,
                    enabledForDot = enabled,
                    onLockClick = {
                        // ✅ verrouillage one-shot
                        onLock?.invoke(def.key)
                        onDirty?.invoke()
                    },
                    onToggle = { openKey = if (openKey == def.key) "" else def.key }
                ) {
                    TraceMoodSliderRow(
                        title = def.title,
                        enabled = enabledCard,
                        userIsPremium = isPremium,
                        isPremiumSlider = false,
                        lockedLabel = null,
                        phaseKey = cycleKey,
                        cycleKey = cycleKey,
                        seedBase = seedBase,
                        sliderKey = def.key,
                        showTitle = false,

                        externalPercent = persistedPct,
                        onPercentChanged = { newPct ->
                            if (!enabledCard) return@TraceMoodSliderRow
                            sliderMap[def.key] = newPct
                            onSliderValue?.invoke(def.key, newPct)
                            onDirty?.invoke()
                        },

                        onCapturedChanged = { hasCaptured ->
                            if (hasCaptured) {
                                capturedMap[def.key] = true

                                // ✅ CRITIQUE : doit appeler markCaptured pour date/heure
                                onCaptured?.invoke(def.key)

                                onDirty?.invoke()
                            }
                        },
                        onAccentChanged = { c -> accentMap[def.key] = c }
                    )

                    Spacer(Modifier.height(12.dp))

                    TraceNoteBlock(
                        note = note,
                        onNoteChange = { newText ->
                            if (!enabledCard) return@TraceNoteBlock
                            noteMap[def.key] = newText
                            onNoteValue?.invoke(def.key, newText)
                            onDirty?.invoke()
                        },
                        enabled = enabledCard,
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

                    val createdAtMillis: Long? =
                        createdAtMap[def.key]?.takeIf { it > 0L }

                    val isLockedCard: Boolean =
                        lockedMap[def.key] == true

                    // ✅ verrouillage carte (one-shot) + règles section depth
                    val enabledCard = (enabled && contentOk) && !isLockedCard

                    key(def.key) {
                        CollapsibleSliderCard(
                            title = def.title,
                            isOpen = openKey == def.key,
                            captured = captured,
                            createdAtMillis = createdAtMillis,
                            locked = isLockedCard,
                            enabledForDot = enabled && contentOk,
                            onLockClick = {
                                onLock?.invoke(def.key)
                                onDirty?.invoke()
                            },
                            onToggle = { openKey = if (openKey == def.key) "" else def.key }
                        ) {
                            TraceMoodSliderRow(
                                title = def.title,
                                enabled = enabledCard,
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
                                    if (!enabledCard) return@TraceMoodSliderRow
                                    sliderMap[def.key] = newPct
                                    onSliderValue?.invoke(def.key, newPct)
                                    onDirty?.invoke()
                                },

                                onCapturedChanged = { hasCaptured ->
                                    if (hasCaptured) {
                                        capturedMap[def.key] = true
                                        onCaptured?.invoke(def.key) // ✅ markCaptured
                                        onDirty?.invoke()
                                    }
                                },
                                onAccentChanged = { c -> accentMap[def.key] = c }
                            )

                            Spacer(Modifier.height(12.dp))

                            TraceNoteBlock(
                                note = note,
                                onNoteChange = { newText ->
                                    if (!enabledCard) return@TraceNoteBlock
                                    noteMap[def.key] = newText
                                    onNoteValue?.invoke(def.key, newText)
                                    onDirty?.invoke()
                                },
                                enabled = enabledCard,
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

    // ✅ NOUVEAU
    createdAtMillis: Long?,
    locked: Boolean,
    onLockClick: (() -> Unit)?,

    enabledForDot: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(CARD_RADIUS)
    val interaction = remember { MutableInteractionSource() }

    // ✅ DOT STATE
    val dotState = when {
        !enabledForDot -> TraceDotState.DISABLED
        captured -> TraceDotState.ON
        else -> TraceDotState.OFF
    }

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
                    .padding(start = 2.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f, fill = true)) {
                    Text(
                        text = title,
                        color = WHITE_SOFT.copy(alpha = 0.99f),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        style = androidx.compose.ui.text.TextStyle.Default
                    )

                    // ✅ REMPLACE l’ancien “Mémoire créée” par le vrai stamp (date + lock)
                    TraceMemoryStamp(
                        captured = captured,
                        createdAtMillis = createdAtMillis,
                        locked = locked,
                        onLock = onLockClick,
                        showLockChip = true,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(Modifier.size(10.dp))

                TraceStatusDot(
                    state = dotState,
                    glowSize = 18.dp
                )
            }

            if (isOpen) {
                Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}