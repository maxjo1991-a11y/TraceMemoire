package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.moteur.cycle.grille.GrilleCycleHome
import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import com.maxjth.tracememoire.ui.systeme.lune.trace.LuneTraceStore
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.tracejour.components.screen.header.CycleStatusPill
import com.maxjth.tracememoire.ui.tracejour.components.screen.navigation.TraceBottomNavBar
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore
import com.maxjth.tracememoire.ui.tracejour.components.screen.slider.TraceLockPayload
import com.maxjth.tracememoire.ui.tracejour.cycle.HomeTraceCycleBridge
import com.maxjth.tracememoire.ui.tracejour.cycle.TraceCycle
import com.maxjth.tracememoire.ui.time.TraceCycleClock
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceJourScreen(
    onBack: () -> Unit,
    onHistory: () -> Unit = {},
    onDeepen: () -> Unit = {},
    saveStore: TraceSaveStore
) {
    val context = LocalContext.current
    val bg = BG_DEEP

    val seedBase: String = remember { "TRACE_MEMOIRE" }
    val isPremium: Boolean = remember { false }

    val allSliderKeys = remember {
        listOf(
            "humeur", "energie", "corps", "presence",
            "repos", "archi_emo", "type_journee", "motifs_psy", "environnement", "clarte"
        )
    }

    val nowTick by produceState(initialValue = 0) {
        while (true) {
            delay(60_000)
            value = value + 1
        }
    }
    @Suppress("UNUSED_VARIABLE")
    val _consumeTick = nowTick

    val homeCycle: TypeCycleHome = remember(nowTick) {
        GrilleCycleHome.resolve(LocalDateTime.now())
    }

    val currentCycle: TraceCycle = remember(homeCycle) {
        HomeTraceCycleBridge.toTraceCycle(homeCycle)
    }

    val lockedCycles = TraceCycleClock.lockedCycles(currentCycle)
    val isCurrentCycleEditable: Boolean = currentCycle !in lockedCycles
    val cycleKey: String = currentCycle.name

    val cycleLabel = when (currentCycle) {
        TraceCycle.MATIN -> "Matin"
        TraceCycle.JOUR -> "Jour"
        TraceCycle.SOIR -> "Soir"
        TraceCycle.NUIT -> "Nuit"
    }

    LaunchedEffect(seedBase) {
        saveStore.attach(context, seedBase)
    }

    LaunchedEffect(isCurrentCycleEditable) {
        saveStore.setCanSave(isCurrentCycleEditable)
    }

    LaunchedEffect(seedBase, cycleKey) {
        saveStore.loadFromPrefs(context, seedBase, cycleKey, allSliderKeys)
        saveStore.luneTick.intValue += 1
    }

    DisposableEffect(seedBase, cycleKey, isCurrentCycleEditable) {
        onDispose {
            if (isCurrentCycleEditable) {
                saveStore.persistAllToPrefs(context, seedBase, cycleKey)
            }
        }
    }

    fun normalizeSliderKey(raw: String): String {
        val last = raw
            .split("|", "/", ":", ">", " ")
            .lastOrNull()
            ?.trim()
            .orEmpty()
        return last.lowercase()
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
                    onBack = { onBack() },
                    onHistory = { onHistory() },
                    onDeepen = { onDeepen() },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))
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
                    .padding(horizontal = 24.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TraceJourTitleBlock(cycle = currentCycle)

                // ✅ pastille un peu plus proche du header
                Spacer(modifier = Modifier.height(0.dp))

                CycleStatusPill(
                    label = cycleLabel,
                    isActive = true
                )

                // ✅ espace plus équilibré avant les cartes
                Spacer(modifier = Modifier.height(16.dp))

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

                    onSliderValue = { key, value ->
                        saveStore.sliderMap[key] = value
                        saveStore.markSliderTouched(key)
                        saveStore.recomputeHomeScoreFromSliders(includePremiumAxes = true)
                        saveStore.markDirty()
                    },

                    onNoteValue = { key, text ->
                        saveStore.noteMap[key] = text
                        saveStore.markDirty()
                    },

                    onCaptured = { key ->
                        saveStore.markCaptured(key)
                    },

                    onLock = { payload: TraceLockPayload ->
                        if (!isCurrentCycleEditable) return@TraceJourSlidersBlock

                        val sliderKey = normalizeSliderKey(payload.sliderKey)
                        val alreadyLocked = saveStore.lockedMap[sliderKey] == true
                        if (alreadyLocked) return@TraceJourSlidersBlock

                        saveStore.handleLockForHomeAndRect(
                            cycleKey = cycleKey,
                            sliderKey = sliderKey
                        )

                        val luneUniqueKey = "$cycleKey|$sliderKey"
                        LuneTraceStore.incrementOncePerCycleToday(
                            context = context,
                            seedBase = seedBase,
                            cycleKey = luneUniqueKey
                        )

                        saveStore.luneTick.intValue += 1
                        saveStore.markDirty()
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}