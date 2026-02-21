// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/TraceJourScreen.kt
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
    saveStore: TraceSaveStore // ✅ store partagé (Home <-> TraceJour)
) {
    val context = LocalContext.current
    val bg = BG_DEEP

    val currentCycle: TraceCycle = remember { TraceCycleClock.currentCycle() }
    val lockedCycles = remember(currentCycle) { TraceCycleClock.lockedCycles(currentCycle) }
    val isCurrentCycleEditable: Boolean = remember(currentCycle, lockedCycles) {
        currentCycle !in lockedCycles
    }

    val cycleKey: String = remember(currentCycle) { currentCycle.name }

    // ✅ IMPORTANT : seedBase doit être IDENTIQUE à Home/attach
    // Mets ta vraie valeur officielle ici (la même partout).
    val seedBase: String = remember { "TRACE_MEMOIRE" }

    // ✅ Ici, tu peux laisser false, le store gère déjà premiumTouchedToday
    val isPremium: Boolean = remember { false }

    // ✅ IMPORTANT : ces keys doivent matcher celles attendues par TraceSaveStore
    // Base:
    //  - humeur, energie, corps, presence
    // Premium (si utilisé):
    //  - repos, archi_emo, type_journee, motifs_psy, environnement, clarte
    val allSliderKeys = remember {
        listOf(
            "humeur", "energie", "corps", "presence",
            "repos", "archi_emo", "type_journee", "motifs_psy", "environnement", "clarte"
        )
    }

    LaunchedEffect(isCurrentCycleEditable) {
        saveStore.setCanSave(isCurrentCycleEditable)
    }

    LaunchedEffect(seedBase, cycleKey) {
        // ✅ Load complet (sliders + HOME + hier) sans écraser la valeur persistée
        saveStore.loadFromPrefs(context, seedBase, cycleKey, allSliderKeys)

        // ❌ IMPORTANT : pas besoin de recalculer ici “à vide”.
        // Le store recalcule déjà quand il faut, et sinon il garde la valeur prefs.
        // (Le recalcul LIVE se fait dans onSliderValue)
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

                            // ✅ enregistre + marque cycle complété + persiste HOME
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
                .background(Brush.verticalGradient(colors = listOf(bg, bg, bg)))
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

                    // ✅ LIVE : chaque slider recalcule Jupiter
                    onSliderValue = { key, value ->
                        saveStore.sliderMap[key] = value
                        saveStore.recomputeHomeScoreFromSliders(includePremiumAxes = true)
                    },
                    onNoteValue = { key, text -> saveStore.noteMap[key] = text },

                    onCaptured = { key -> saveStore.markCaptured(key) },

                    onLock = { key ->
                        saveStore.lockCard(key)
                        saveStore.persistAllToPrefs(context, seedBase, cycleKey)
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}