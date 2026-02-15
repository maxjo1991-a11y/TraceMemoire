// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/TraceJourScreen.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycle
import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycleClock
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.BottomSaveBar
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.TraceSaveStore

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
    val seedBase: String = remember { "DEBUG" }   // TODO: vraie date (yyyyMMdd)
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

    // ✅ AUTO-PERSIST quand on quitte l’écran (back, navigation, etc.)
    // (ça garde au moins les sliders/notes/captured même si tu n’appuies pas sur Enregistrer)
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
                title = { /* vide */ },
                navigationIcon = {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = BG_SOFT.copy(alpha = 0.18f),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.5.dp,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .border(
                                width = 1.dp,
                                color = TURQUOISE.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(999.dp)
                            )
                    ) {
                        TextButton(
                            onClick = {
                                // ✅ back = on persiste aussi (double sécurité)
                                if (isCurrentCycleEditable) {
                                    saveStore.persistAllToPrefs(context, seedBase, cycleKey)
                                }
                                onBack()
                            },
                            interactionSource = remember { MutableInteractionSource() },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "← En arrière",
                                color = TURQUOISE.copy(alpha = 0.8f),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bg)
            )
        },
        bottomBar = {
            BottomSaveBar(
                state = saveStore.state.value,
                enabled = saveStore.canSaveEnabled.value,
                onSave = {
                    saveStore.setSaving()

                    // ✅ ici tu “figes” vraiment les données pour le cycle
                    saveStore.persistAllToPrefs(context, seedBase, cycleKey)

                    // ✅ status SAVED + lastSavedAt + lastSavedCycleKey
                    saveStore.setSaved(cycleKey)

                    // ✅ on persiste encore pour lastSavedAt / status
                    saveStore.persistAllToPrefs(context, seedBase, cycleKey)
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

                    // ✅ nouvelle règle: chaque action = dirty + on stocke
                    onDirty = { saveStore.markDirty() },

                    // ✅ on passe les maps persistantes
                    externalSliderMap = saveStore.sliderMap,
                    externalNoteMap = saveStore.noteMap,
                    externalCapturedMap = saveStore.capturedMap,

                    // ✅ quand ça change on met à jour la map
                    onSliderValue = { key, value -> saveStore.sliderMap[key] = value },
                    onNoteValue = { key, text -> saveStore.noteMap[key] = text },
                    onCaptured = { key -> saveStore.capturedMap[key] = true }
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}