package com.maxjth.tracememoire.ui.tracejour.components.screen.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.tracejour.components.screen.cards.CollapsibleSliderCard
import com.maxjth.tracememoire.ui.tracejour.components.screen.cards.model.CardOpenState
import com.maxjth.tracememoire.ui.tracejour.components.screen.helpers.INNER_OPEN_GAP_DP
import com.maxjth.tracememoire.ui.tracejour.components.screen.helpers.OUTER_HORIZONTAL_PADDING_DP
import com.maxjth.tracememoire.ui.tracejour.components.screen.helpers.ROW_SPACING_DP
import com.maxjth.tracememoire.ui.tracejour.components.screen.helpers.SLIDERS_PREMIUM
import com.maxjth.tracememoire.ui.tracejour.components.screen.helpers.buildMemoryKeyword
import com.maxjth.tracememoire.ui.tracejour.components.screen.helpers.buildMemoryPhrase
import com.maxjth.tracememoire.ui.tracejour.components.screen.helpers.cycleAccentColor
import com.maxjth.tracememoire.ui.tracejour.components.screen.helpers.formatMemoryMeta
import com.maxjth.tracememoire.ui.tracejour.components.screen.notes.TraceNoteBlock
import com.maxjth.tracememoire.ui.tracejour.components.screen.slider.TraceLockPayload
import com.maxjth.tracememoire.ui.tracejour.components.screen.slider.TraceMoodSliderRow

@Composable
fun TraceJourPremiumSlidersSection(
    enabled: Boolean,
    isPremium: Boolean,
    cycleKey: String,
    seedBase: String,
    getCardState: (String) -> CardOpenState,
    onCycleCardState: (String) -> Unit,
    capturedMap: MutableMap<String, Boolean>,
    noteMap: MutableMap<String, String>,
    sliderMap: MutableMap<String, Int>,
    createdAtMap: MutableMap<String, Long>,
    lockedMap: MutableMap<String, Boolean>,
    accentMap: MutableMap<String, Color>,
    optimisticLocking: MutableMap<String, Boolean>,
    phraseMap: MutableMap<String, String>,
    keywordMap: MutableMap<String, String>,
    onDirty: (() -> Unit)? = null,
    onSliderValue: ((String, Int) -> Unit)? = null,
    onNoteValue: ((String, String) -> Unit)? = null,
    onCaptured: ((String) -> Unit)? = null,
    onLock: ((TraceLockPayload) -> Unit)? = null,
    optimisticLock: (String) -> Unit
) {
    // MODE TEST : force Premium ouvert
    val debugForcePremium = true

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = OUTER_HORIZONTAL_PADDING_DP.dp)
    ) {
        SLIDERS_PREMIUM.forEachIndexed { index, def ->

            val captured = capturedMap[def.key] == true
            val note = noteMap[def.key] ?: ""
            val accent = accentMap[def.key] ?: cycleAccentColor(cycleKey)
            val persistedPct = sliderMap[def.key] ?: 0

            val createdAtMillis = createdAtMap[def.key]?.takeIf { it > 0L }
            val isLockedCard = lockedMap[def.key] == true

            val contentOk = isPremium || debugForcePremium
            val enabledCard = enabled && contentOk

            val cardState = getCardState(def.key)
            val isCardOpen = cardState != CardOpenState.CLOSED

            val memoryPhrase = phraseMap[def.key]
                ?: buildMemoryPhrase(
                    sliderKey = def.key,
                    cycleKey = cycleKey,
                    persistedPct = persistedPct,
                    captured = captured,
                    createdAtMillis = createdAtMillis
                )

            val memoryKeyword = keywordMap[def.key]
                ?: buildMemoryKeyword(memoryPhrase)

            val memoryKeywordForCard = if (isCardOpen) "" else memoryKeyword

            val memoryMeta = formatMemoryMeta(
                cycleKey = cycleKey,
                createdAtMillis = createdAtMillis
            )

            key(def.key) {
                CollapsibleSliderCard(
                    sliderKey = def.key,
                    title = def.title,
                    cardState = cardState,
                    captured = captured,
                    createdAtMillis = createdAtMillis,
                    locked = false,
                    onLockClick = null,
                    enabledForDot = enabled && contentOk,
                    onToggle = { onCycleCardState(def.key) },
                    percent = persistedPct,
                    memoryPhrase = memoryPhrase,
                    memoryKeyword = memoryKeywordForCard,
                    memoryMeta = memoryMeta,
                    hasNote = note.isNotBlank(),
                    isHero = false,
                    heroSubtitle = null,
                    heroMinHeight = 210.dp,
                    heroTitleSizeSp = 34,
                    activeCycleKey = cycleKey,
                    modifier = Modifier,
                    content = {
                        TraceMoodSliderRow(
                            title = def.title,
                            enabled = enabledCard,
                            userIsPremium = true,
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
                            externalCaptured = captured,
                            externalCreatedAtMillis = createdAtMillis,
                            externalLocked = false,
                            onPhraseChanged = { phrase ->
                                phraseMap[def.key] = phrase
                            },
                            onKeywordChanged = { keyword ->
                                keywordMap[def.key] = keyword
                            },
                            onLock = { payload ->
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
                            onAccentChanged = { c ->
                                accentMap[def.key] = c
                            }
                        )

                        Spacer(Modifier.size(INNER_OPEN_GAP_DP.dp))

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
                            title = "Trace écrite",
                            placeholder = "Écrire une note…"
                        )
                    }
                )
            }

            if (index != SLIDERS_PREMIUM.lastIndex) {
                Spacer(Modifier.size(ROW_SPACING_DP.dp))
            }
        }
    }
}