package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.tracejour.components.screen.helpers.SECTION_GAP_DP
import com.maxjth.tracememoire.ui.tracejour.components.screen.section.TraceJourFreeSlidersSection
import com.maxjth.tracememoire.ui.tracejour.components.screen.section.TraceJourPremiumSlidersSection
import com.maxjth.tracememoire.ui.tracejour.components.screen.slider.TraceLockPayload
import androidx.compose.runtime.getValue

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
    var openKey by remember { mutableStateOf("") }

    val capturedMap = externalCapturedMap ?: remember { mutableStateMapOf<String, Boolean>() }
    val noteMap = externalNoteMap ?: remember { mutableStateMapOf<String, String>() }
    val sliderMap = externalSliderMap ?: remember { mutableStateMapOf<String, Int>() }

    val createdAtMap = externalCreatedAtMap ?: remember { mutableStateMapOf<String, Long>() }
    val lockedMap = externalLockedMap ?: remember { mutableStateMapOf<String, Boolean>() }

    val accentMap = remember { mutableStateMapOf<String, Color>() }
    val optimisticLocking = remember { mutableStateMapOf<String, Boolean>() }

    // feedback immédiat pendant la session
    val phraseMap = remember { mutableStateMapOf<String, String>() }
    val keywordMap = remember { mutableStateMapOf<String, String>() }

    fun optimisticLock(sliderKey: String) {
        optimisticLocking[sliderKey] = true

        if ((createdAtMap[sliderKey] ?: 0L) <= 0L) {
            createdAtMap[sliderKey] = System.currentTimeMillis()
        }

        if (openKey == sliderKey) {
            openKey = ""
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        TraceJourFreeSlidersSection(
            enabled = enabled,
            isPremium = isPremium,
            cycleKey = cycleKey,
            seedBase = seedBase,
            openKey = openKey,
            onOpenKeyChange = { openKey = it },

            capturedMap = capturedMap,
            noteMap = noteMap,
            sliderMap = sliderMap,
            createdAtMap = createdAtMap,
            lockedMap = lockedMap,
            accentMap = accentMap,
            optimisticLocking = optimisticLocking,
            phraseMap = phraseMap,
            keywordMap = keywordMap,

            onDirty = onDirty,
            onSliderValue = onSliderValue,
            onNoteValue = onNoteValue,
            onCaptured = onCaptured,
            onLock = onLock,
            optimisticLock = ::optimisticLock
        )

        if (isPremium || showPremiumLockedRows) {
            Spacer(Modifier.size(SECTION_GAP_DP.dp))

            TraceJourPremiumSlidersSection(
                enabled = enabled,
                isPremium = isPremium,
                cycleKey = cycleKey,
                seedBase = seedBase,
                openKey = openKey,
                onOpenKeyChange = { openKey = it },

                capturedMap = capturedMap,
                noteMap = noteMap,
                sliderMap = sliderMap,
                createdAtMap = createdAtMap,
                lockedMap = lockedMap,
                accentMap = accentMap,
                optimisticLocking = optimisticLocking,
                phraseMap = phraseMap,
                keywordMap = keywordMap,

                onDirty = onDirty,
                onSliderValue = onSliderValue,
                onNoteValue = onNoteValue,
                onCaptured = onCaptured,
                onLock = onLock,
                optimisticLock = ::optimisticLock
            )
        }
    }
}