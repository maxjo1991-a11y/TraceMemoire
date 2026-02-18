package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.tracejour.components.screen.navigation.TraceBottomNavBar
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.BottomSaveBar
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.TraceSaveStore
import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycle
import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycleClock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceJourScreen(
    onBack: () -> Unit,
    onHistory: () -> Unit = {},
    onDeepen: () -> Unit = {},
    saveStore: TraceSaveStore // ✅ IMPORTANT: store partagé (Home <-> TraceJour)
) {
    val context = LocalContext.current
    val bg = BG_DEEP

    val currentCycle: TraceCycle = remember { TraceCycleClock.currentCycle() }
    val lockedCycles = remember(currentCycle) { TraceCycleClock.lockedCycles(currentCycle) }
    val isCurrentCycleEditable: Boolean = remember(currentCycle, lockedCycles) {
        currentCycle !in lockedCycles
    }

    val cycleKey: String = remember(currentCycle) { currentCycle.name }
    val seedBase: String = remember { "DEBUG" } // TODO: vraie date (yyyyMMdd)
    val isPremium: Boolean = remember { false }

    val allSliderKeys = remember {
        listOf(
            "humeur", "energie", "corps", "presence",
            "emotion", "sommeil", "typejour", "motifs", "environ", "clarte", "charge"
        )
    }

    LaunchedEffect(isCurrentCycleEditable) {
        saveStore.setCanSave(isCurrentCycleEditable)
    }

    LaunchedEffect(seedBase, cycleKey) {
        saveStore.loadFromPrefs(context, seedBase, cycleKey, allSliderKeys)
    }

    DisposableEffect(seedBase, cycleKey, isCurrentCycleEditable) {
        onDispose {
            if (isCurrentCycleEditable) {
                saveStore.persistAllToPrefs(context, seedBase, cycleKey)
            }
        }
    }

    fun safePersistIfEditable() {
        if (isCurrentCycleEditable) {
            saveStore.persistAllToPrefs(context, seedBase, cycleKey)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {},
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .navigationBarsPadding()
            ) {
                TraceBottomNavBar(
                    onBack = {
                        safePersistIfEditable()
                        onBack()
                    },
                    onHistory = {
                        safePersistIfEditable()
                        onHistory()
                    },
                    onDeepen = { onDeepen() },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                BottomSaveBar(
                    state = saveStore.state.value,
                    enabled = saveStore.canSaveEnabled.value,
                    onSave = {
                        if (saveStore.canSaveEnabled.value && isCurrentCycleEditable) {
                            saveStore.setSaving()
                            saveStore.persistAllToPrefs(context, seedBase, cycleKey)

                            // ✅ enregistre + marque le cycle complété (NUIT/MATIN/JOUR/SOIR)
                            saveStore.setSaved(cycleKey)
                        }
                    }
                )
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(bg, bg, bg)
                    )
                )
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 22.dp)
            ) {
                TraceJourTitleBlock()
                Spacer(modifier = Modifier.height(18.dp))

                TraceJourSlidersBlock(
                    enabled = isCurrentCycleEditable,
                    isPremium = isPremium,
                    cycleKey = cycleKey,
                    seedBase = seedBase,
                    showPremiumLockedRows = true,

                    onDirty = { saveStore.markDirty() },

                    externalSliderMap = saveStore.sliderMap,
                    externalNoteMap = saveStore.noteMap,
                    externalCapturedMap = saveStore.capturedMap,

                    externalCreatedAtMap = saveStore.createdAtMap,
                    externalLockedMap = saveStore.lockedMap,

                    onSliderValue = { key, value -> saveStore.sliderMap[key] = value },
                    onNoteValue = { key, text -> saveStore.noteMap[key] = text },

                    onCaptured = { key -> saveStore.markCaptured(key) },

                    onLock = { key ->
                        // ✅ on verrouille une CARTE (pas un cycle)
                        saveStore.lockCard(key)
                        saveStore.persistAllToPrefs(context, seedBase, cycleKey)
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}