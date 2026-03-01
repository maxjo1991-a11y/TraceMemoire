// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/TraceJourScreen.kt
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
import com.maxjth.tracememoire.ui.systeme.lune.trace.LuneTraceStore
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.tracejour.components.screen.navigation.TraceBottomNavBar
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore
import com.maxjth.tracememoire.ui.tracejour.components.screen.slider.TraceLockPayload
import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycle
import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycleClock
import kotlinx.coroutines.delay

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

    // ✅ IMPORTANT : doit matcher HomeScreen + attach()
    val seedBase: String = remember { "TRACE_MEMOIRE" }

    // (plus tard: branche ton vrai flag premium)
    val isPremium: Boolean = remember { false }

    // ✅ Keys sliders (FREE + Premium)
    val allSliderKeys = remember {
        listOf(
            "humeur", "energie", "corps", "presence",
            "repos", "archi_emo", "type_journee", "motifs_psy", "environnement", "clarte"
        )
    }

    // ✅ TICK TEMPOREL : force recomposition → le cycle se met à jour sans quitter l’écran
    val nowTick by produceState(initialValue = 0) {
        while (true) {
            delay(60_000)
            value = value + 1
        }
    }
    @Suppress("UNUSED_VARIABLE")
    val _consumeTick = nowTick

    // ✅ cycle recalculé à chaque recomposition (donc quand nowTick change)
    val currentCycle: TraceCycle = TraceCycleClock.currentCycle()
    val lockedCycles = TraceCycleClock.lockedCycles(currentCycle)
    val isCurrentCycleEditable: Boolean = currentCycle !in lockedCycles
    val cycleKey: String = currentCycle.name

    // ✅ attach pour HOME persist
    LaunchedEffect(seedBase) {
        saveStore.attach(context, seedBase)
    }

    // ✅ blocage save si cycle verrouillé (editable/figé)
    LaunchedEffect(isCurrentCycleEditable) {
        saveStore.setCanSave(isCurrentCycleEditable)
    }

    // ✅ load cycle + home (relancé automatiquement quand cycleKey change)
    LaunchedEffect(seedBase, cycleKey) {
        saveStore.loadFromPrefs(context, seedBase, cycleKey, allSliderKeys)

        // petit bump pour forcer refresh Home si besoin
        saveStore.luneTick.intValue += 1
    }

    // ✅ on sort : on persiste (si editable)
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

    // ✅ FIX KEY : on garde seulement la vraie clé (humeur/energie/...)
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

                        // anti-double : si déjà locked → rien
                        val alreadyLocked = saveStore.lockedMap[sliderKey] == true
                        if (alreadyLocked) return@TraceJourSlidersBlock

                        // ✅ 1) STORE : lock + cycleDone + recalcul Soleil + % rectangle + persist HOME-only
                        saveStore.handleLockForHomeAndRect(
                            cycleKey = cycleKey,
                            sliderKey = sliderKey
                        )

                        // ✅ 2) LUNE : 1 verrouillage = 1 trace (une seule fois / jour / slider / cycle)
                        val luneUniqueKey = "$cycleKey|$sliderKey"
                        LuneTraceStore.incrementOncePerCycleToday(
                            context = context,
                            seedBase = seedBase,
                            cycleKey = luneUniqueKey
                        )

                        // ✅ 3) SIGNAL UI : Home refresh direct
                        saveStore.luneTick.intValue += 1

                        // ✅ 4) dirty
                        saveStore.markDirty()
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}