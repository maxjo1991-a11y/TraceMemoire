package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.maxjth.tracememoire.ui.tracejour.components.screen.diamondbutton.TraceApprofondirDiamondButton
import com.maxjth.tracememoire.ui.tracejour.components.screen.header.TraceJourDisplayMode
import com.maxjth.tracememoire.ui.tracejour.components.screen.header.TraceJourModeToggle
import com.maxjth.tracememoire.ui.tracejour.components.screen.retourpastille.TraceRetourPastille
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore
import com.maxjth.tracememoire.ui.tracejour.components.screen.slider.TraceLockPayload
import com.maxjth.tracememoire.ui.tracejour.components.screen.valeurcapsule.TraceCycleValeurCapsule
import com.maxjth.tracememoire.ui.tracejour.cycle.HomeTraceCycleBridge
import com.maxjth.tracememoire.ui.tracejour.cycle.TraceCycle
import com.maxjth.tracememoire.ui.time.TraceCycleClock
import kotlinx.coroutines.delay
import java.time.LocalDate
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

    var displayMode by rememberSaveable {
        mutableStateOf(TraceJourDisplayMode.ESSENTIEL)
    }

    var debugCycleOverride by rememberSaveable {
        mutableStateOf<TraceCycle?>(null)
    }

    var debugReloadTick by rememberSaveable {
        mutableIntStateOf(0)
    }

    val allSliderKeys = remember {
        listOf(
            "humeur", "energie", "corps", "presence",
            "repos", "archi_emo", "type_journee", "motifs_psy", "environnement", "clarte"
        )
    }

    val nowTick by produceState(initialValue = 0) {
        while (true) {
            delay(60_000)
            value += 1
        }
    }

    val nowDate = remember(nowTick) { LocalDate.now() }

    val homeCycle: TypeCycleHome = remember(nowTick) {
        GrilleCycleHome.resolve(LocalDateTime.now())
    }

    val currentCycleFromClock: TraceCycle = remember(homeCycle) {
        HomeTraceCycleBridge.toTraceCycle(homeCycle)
    }

    val currentCycle: TraceCycle = debugCycleOverride ?: currentCycleFromClock
    val cycleKey: String = currentCycle.name

    val lockedCycles = TraceCycleClock.lockedCycles(currentCycle)
    val isCurrentCycleEditable: Boolean = currentCycle !in lockedCycles

    val reloadKey = remember(nowDate, cycleKey, debugReloadTick) {
        "${nowDate}_${cycleKey}_$debugReloadTick"
    }

    val cycleLabel = when (currentCycle) {
        TraceCycle.MATIN -> "Matin"
        TraceCycle.JOUR -> "Jour"
        TraceCycle.SOIR -> "Soir"
        TraceCycle.NUIT -> "Nuit"
    }

    val valeurDuMoment =
        (saveStore.sliderMap["humeur"] ?: 0).coerceIn(0, 100) +
                (saveStore.sliderMap["energie"] ?: 0).coerceIn(0, 100) +
                (saveStore.sliderMap["corps"] ?: 0).coerceIn(0, 100) +
                (saveStore.sliderMap["presence"] ?: 0).coerceIn(0, 100)

    LaunchedEffect(seedBase) {
        saveStore.attach(context, seedBase)
    }

    LaunchedEffect(nowDate) {
        debugCycleOverride = null
    }

    LaunchedEffect(isCurrentCycleEditable) {
        saveStore.setCanSave(isCurrentCycleEditable)
    }

    LaunchedEffect(seedBase, reloadKey) {
        saveStore.onHomeTick()
        saveStore.loadFromPrefs(
            context = context,
            seedBase = seedBase,
            cycleKey = cycleKey,
            sliderKeys = allSliderKeys
        )
        saveStore.luneTick.intValue += 1
    }

    DisposableEffect(seedBase, reloadKey, isCurrentCycleEditable) {
        onDispose {
            if (isCurrentCycleEditable) {
                saveStore.persistAllToPrefs(
                    context = context,
                    seedBase = seedBase,
                    cycleKey = cycleKey
                )
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
        topBar = {}
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
                    .padding(horizontal = 24.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TraceJourTitleBlock(cycle = currentCycle)

                Spacer(modifier = Modifier.height(18.dp))

                TraceJourModeToggle(
                    selectedMode = displayMode,
                    onModeChange = { displayMode = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                TraceApprofondirDiamondButton(
                    onClick = onDeepen,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(18.dp))

                TraceCycleValeurCapsule(
                    cycleLabel = cycleLabel,
                    value = valeurDuMoment,
                    max = 400,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                key(reloadKey) {
                    TraceJourSlidersBlock(
                        enabled = isCurrentCycleEditable,
                        isPremium = isPremium,
                        cycleKey = cycleKey,
                        seedBase = seedBase,
                        showPremiumLockedRows = displayMode == TraceJourDisplayMode.PREMIUM,
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

                            saveStore.persistAllToPrefs(
                                context = context,
                                seedBase = seedBase,
                                cycleKey = cycleKey
                            )
                        },
                        onNoteValue = { key, text ->
                            saveStore.noteMap[key] = text
                            saveStore.markDirty()

                            saveStore.persistAllToPrefs(
                                context = context,
                                seedBase = seedBase,
                                cycleKey = cycleKey
                            )
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

                            saveStore.persistAllToPrefs(
                                context = context,
                                seedBase = seedBase,
                                cycleKey = cycleKey
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 18.dp, bottom = 18.dp)
            ) {
                TraceRetourPastille(
                    onClick = onBack
                )
            }
        }
    }
}