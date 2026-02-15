package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.BottomSaveBar
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.TraceSaveStore
import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycle
import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycleClock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceJourScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val bg = BG_SOFT
    val bgSoft = BG_SOFT.copy(alpha = 0.92f)

    val currentCycle: TraceCycle = remember { TraceCycleClock.currentCycle() }
    val lockedCycles = remember(currentCycle) { TraceCycleClock.lockedCycles(currentCycle) }
    val isCurrentCycleEditable: Boolean = remember(currentCycle, lockedCycles) {
        currentCycle !in lockedCycles
    }

    val cycleKey: String = remember(currentCycle) { currentCycle.name }
    val seedBase: String = remember { "DEBUG" } // TODO: vraie date (yyyyMMdd)
    val isPremium: Boolean = remember { false }

    // ✅ store persistant
    val saveStore = remember { TraceSaveStore() }

    // Liste des sliders (FREE + PREMIUM) pour load/save
    val allSliderKeys = remember {
        listOf(
            "humeur", "energie", "corps", "presence",
            "emotion", "sommeil", "typejour", "motifs", "environ", "clarte", "charge"
        )
    }

    // ✅ set canSave
    LaunchedEffect(isCurrentCycleEditable) {
        saveStore.setCanSave(isCurrentCycleEditable)
    }

    // ✅ LOAD quand on arrive (ou quand cycle change)
    LaunchedEffect(seedBase, cycleKey) {
        saveStore.loadFromPrefs(context, seedBase, cycleKey, allSliderKeys)
    }

    // ✅ AUTO-PERSIST quand on quitte l’écran
    DisposableEffect(seedBase, cycleKey, isCurrentCycleEditable) {
        onDispose {
            if (isCurrentCycleEditable) {
                saveStore.persistAllToPrefs(context, seedBase, cycleKey)
            }
        }
    }

    Scaffold(
        containerColor = bg,

        topBar = {
            TopAppBar(
                title = { /* rien */ },

                navigationIcon = {
                    TextButton(
                        onClick = {
                            if (isCurrentCycleEditable) {
                                saveStore.persistAllToPrefs(context, seedBase, cycleKey)
                            }
                            onBack()
                        },
                        interactionSource = remember { MutableInteractionSource() },
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "← En arrière",
                            color = TURQUOISE.copy(alpha = 0.85f),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                },

                actions = { },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                )
            )
        },

        bottomBar = {
            BottomSaveBar(
                state = saveStore.state.value,
                enabled = saveStore.canSaveEnabled.value,
                onSave = {
                    // ✅ on garde ton save global (bar du bas)
                    if (saveStore.canSaveEnabled.value && isCurrentCycleEditable) {
                        saveStore.setSaving()

                        // ✅ figer global du cycle (si tu gardes ce comportement)
                        saveStore.persistAllToPrefs(context, seedBase, cycleKey)

                        saveStore.setSaved(cycleKey)

                        // ✅ persist encore pour lastSavedAt / status
                        saveStore.persistAllToPrefs(context, seedBase, cycleKey)
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bg, bgSoft, bg)))
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

                    // ✅ dirty
                    onDirty = { saveStore.markDirty() },

                    // ✅ maps persistantes
                    externalSliderMap = saveStore.sliderMap,
                    externalNoteMap = saveStore.noteMap,
                    externalCapturedMap = saveStore.capturedMap,

                    // ✅ NOUVEAU : maps date/heure + verrouillage
                    externalCreatedAtMap = saveStore.createdAtMap,
                    externalLockedMap = saveStore.lockedMap,

                    // ✅ update maps
                    onSliderValue = { key, value -> saveStore.sliderMap[key] = value },
                    onNoteValue = { key, text -> saveStore.noteMap[key] = text },

                    // ✅ CRITIQUE : captured doit passer par markCaptured()
                    onCaptured = { key -> saveStore.markCaptured(key) },

                    // ✅ NOUVEAU : verrouiller une carte (one-shot)
                    onLock = { key ->
                        saveStore.lockCycle(key)

                        // ✅ optionnel mais recommandé: persister tout de suite
                        saveStore.persistAllToPrefs(context, seedBase, cycleKey)
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}