// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/TraceJourSlidersBlock.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.tracejour.components.screen.cards.CollapsibleSliderCard
import com.maxjth.tracememoire.ui.tracejour.components.screen.depth.TraceDepthSection
import com.maxjth.tracememoire.ui.tracejour.components.screen.notes.TraceNoteBlock
import com.maxjth.tracememoire.ui.tracejour.components.screen.slider.TraceLockPayload
import com.maxjth.tracememoire.ui.tracejour.components.screen.slider.TraceMoodSliderRow

private data class SliderDef(val key: String, val title: String)

private val SLIDERS_FREE = listOf(
    SliderDef("humeur", "Humeur globale"),
    SliderDef("energie", "Énergie / rythme"),
    SliderDef("corps", "Corps / sensations"),
    SliderDef("presence", "Présence / attention")
)

private val SLIDERS_PREMIUM = listOf(
    SliderDef("repos", "Qualité du repos vécu"),
    SliderDef("archi_emo", "Architecture émotionnelle"),
    SliderDef("type_journee", "Type de journée"),
    SliderDef("motifs_psy", "Motifs psychiques"),
    SliderDef("environnement", "Environnement"),
    SliderDef("clarte", "Clarté mentale")
)

private val ROW_SPACING = 18.dp
private val SECTION_GAP = 26.dp
private val OUTER_HORIZONTAL_PADDING = 14.dp

private const val DEBUG_FORCE_PREMIUM_VISUALS = true

@Composable
fun TraceJourSlidersBlock(
    enabled: Boolean,
    isPremium: Boolean,
    cycleKey: String,
    seedBase: String,
    modifier: Modifier = Modifier,
    showPremiumLockedRows: Boolean = true,

    onDirty: (() -> Unit)? = null,
    externalSliderMap: MutableMap<String, Int>? = null,
    externalNoteMap: MutableMap<String, String>? = null,
    externalCapturedMap: MutableMap<String, Boolean>? = null,
    externalCreatedAtMap: MutableMap<String, Long>? = null,
    externalLockedMap: MutableMap<String, Boolean>? = null,

    onSliderValue: ((String, Int) -> Unit)? = null,
    onNoteValue: ((String, String) -> Unit)? = null,
    onCaptured: ((String) -> Unit)? = null,
    onLock: ((TraceLockPayload) -> Unit)? = null
) {
    val premiumVisualUnlocked = isPremium || DEBUG_FORCE_PREMIUM_VISUALS
    val premiumEffectiveForPremiumNotes = isPremium || DEBUG_FORCE_PREMIUM_VISUALS

    var openKey by remember { mutableStateOf("") }

    val capturedMap = externalCapturedMap ?: remember { mutableStateMapOf<String, Boolean>() }
    val noteMap = externalNoteMap ?: remember { mutableStateMapOf<String, String>() }
    val sliderMap = externalSliderMap ?: remember { mutableStateMapOf<String, Int>() }

    val createdAtMap = externalCreatedAtMap ?: remember { mutableStateMapOf<String, Long>() }
    val lockedMap = externalLockedMap ?: remember { mutableStateMapOf<String, Boolean>() }

    val accentMap = remember { mutableStateMapOf<String, Color>() }

    val optimisticLocking = remember { mutableStateMapOf<String, Boolean>() }

    fun optimisticLock(sliderKey: String) {
        optimisticLocking[sliderKey] = true
        if ((createdAtMap[sliderKey] ?: 0L) <= 0L) {
            createdAtMap[sliderKey] = System.currentTimeMillis()
        }
        if (openKey == sliderKey) openKey = ""
    }

    Column(modifier = modifier.fillMaxWidth()) {

        // ───────── FREE ─────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = OUTER_HORIZONTAL_PADDING)
        ) {
            SLIDERS_FREE.forEachIndexed { index, def ->

                val captured = capturedMap[def.key] == true
                val note = noteMap[def.key] ?: ""
                val accent = accentMap[def.key] ?: TURQUOISE

                // ✅ défaut = 0 (plus 50)
                val persistedPct = sliderMap[def.key] ?: 0

                val createdAtMillis: Long? = createdAtMap[def.key]?.takeIf { it > 0L }
                val isLockedCard: Boolean = lockedMap[def.key] == true
                val enabledCard = enabled && !isLockedCard

                key(def.key) {
                    CollapsibleSliderCard(
                        sliderKey = def.key,
                        title = def.title,
                        isOpen = openKey == def.key,
                        captured = captured,
                        createdAtMillis = createdAtMillis,
                        locked = isLockedCard,
                        enabledForDot = enabled,
                        onLockClick = null,
                        onToggle = { openKey = if (openKey == def.key) "" else def.key },
                        percent = persistedPct,
                        content = {
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
                                externalCaptured = captured,
                                externalCreatedAtMillis = createdAtMillis,
                                externalLocked = isLockedCard,
                                onLock = { payload ->
                                    if (lockedMap[def.key] == true) return@TraceMoodSliderRow
                                    if (optimisticLocking[def.key] == true) return@TraceMoodSliderRow
                                    optimisticLock(def.key)
                                    onLock?.invoke(payload)
                                    onDirty?.invoke()
                                },
                                onCapturedChanged = { hasCaptured ->
                                    if (hasCaptured) {
                                        capturedMap[def.key] = true
                                        onCaptured?.invoke(def.key)
                                        onDirty?.invoke()
                                    }
                                },
                                onAccentChanged = { c -> accentMap[def.key] = c }
                            )

                            Spacer(Modifier.size(12.dp))

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
                                footerMessageFree = "Tout ne demande pas à être écrit. Mais tout peut l'être.",
                                footerMessagePremium = "Ici, tout peut exister. Sans filtre."
                            )
                        }
                    )
                }

                if (index != SLIDERS_FREE.lastIndex) Spacer(Modifier.size(ROW_SPACING))
            }
        }

        // ───────── PREMIUM ─────────
        if (isPremium || showPremiumLockedRows) {
            Spacer(Modifier.size(SECTION_GAP))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = OUTER_HORIZONTAL_PADDING)
            ) {
                TraceDepthSection(
                    isPremium = premiumVisualUnlocked,
                    modifier = Modifier.fillMaxWidth()
                ) { contentEnabled ->

                    val contentOk = if (premiumVisualUnlocked) true else contentEnabled

                    SLIDERS_PREMIUM.forEachIndexed { index, def ->

                        val captured = capturedMap[def.key] == true
                        val note = noteMap[def.key] ?: ""
                        val accent = accentMap[def.key] ?: TURQUOISE

                        // ✅ défaut = 0 (plus 50)
                        val persistedPct = sliderMap[def.key] ?: 0

                        val createdAtMillis: Long? = createdAtMap[def.key]?.takeIf { it > 0L }
                        val isLockedCard: Boolean = lockedMap[def.key] == true
                        val enabledCard = (enabled && contentOk) && !isLockedCard

                        key(def.key) {
                            CollapsibleSliderCard(
                                sliderKey = def.key,
                                title = def.title,
                                isOpen = openKey == def.key,
                                captured = captured,
                                createdAtMillis = createdAtMillis,
                                locked = isLockedCard,
                                enabledForDot = enabled && contentOk,
                                onLockClick = null,
                                onToggle = { openKey = if (openKey == def.key) "" else def.key },
                                percent = persistedPct,
                                content = {
                                    TraceMoodSliderRow(
                                        title = def.title,
                                        enabled = enabledCard,
                                        userIsPremium = premiumEffectiveForPremiumNotes,
                                        isPremiumSlider = true,
                                        lockedLabel = "Débloqué avec Premium.",
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
                                        externalCaptured = captured,
                                        externalCreatedAtMillis = createdAtMillis,
                                        externalLocked = isLockedCard,
                                        onLock = { payload ->
                                            if (lockedMap[def.key] == true) return@TraceMoodSliderRow
                                            if (optimisticLocking[def.key] == true) return@TraceMoodSliderRow
                                            optimisticLock(def.key)
                                            onLock?.invoke(payload)
                                            onDirty?.invoke()
                                        },
                                        onCapturedChanged = { hasCaptured ->
                                            if (hasCaptured) {
                                                capturedMap[def.key] = true
                                                onCaptured?.invoke(def.key)
                                                onDirty?.invoke()
                                            }
                                        },
                                        onAccentChanged = { c -> accentMap[def.key] = c }
                                    )

                                    Spacer(Modifier.size(12.dp))

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
                                        footerMessageFree = "Tout ne demande pas à être écrit. Mais tout peut l'être.",
                                        footerMessagePremium = "Ici, tout peut exister. Sans filtre."
                                    )
                                }
                            )
                        }

                        if (index != SLIDERS_PREMIUM.lastIndex) Spacer(Modifier.size(ROW_SPACING))
                    }
                }
            }
        }
    }
}