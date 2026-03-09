package com.maxjth.tracememoire.ui.accueil.ecran.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.maxjth.tracememoire.ui.systeme.lune.trace.LuneTraceStore
import com.maxjth.tracememoire.ui.terre.logic.TerreHistoryStore
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore

data class HomeScreenDataState(
    val luneCount: Int,
    val luneDeltaToday: Int,
    val scoreHier: Int?,
    val terreDeltaText: String?,
    val soleilPercent: Int,
    val soleilDeltaText: String?
)

@Composable
fun rememberHomeScreenData(
    saveStore: TraceSaveStore,
    seedBase: String = "TRACE_MEMOIRE"
): HomeScreenDataState {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // ✅ garde-fou : éviter un mini double refresh au tout premier retour
    var hasDoneInitialLoad by remember { mutableStateOf(false) }

    LaunchedEffect(context, seedBase) {
        saveStore.attach(context, seedBase)
        saveStore.loadHomeOnlyForHomeScreen(context, seedBase)
        saveStore.onHomeTick()
        hasDoneInitialLoad = true
    }

    LaunchedEffect(Unit) {
        println("DEBUG_HOME_TERRE → Premier lancement → force terreTick +1")
        saveStore.terreTick.intValue += 1
    }

    DisposableEffect(lifecycleOwner, hasDoneInitialLoad) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && hasDoneInitialLoad) {
                saveStore.onHomeTick()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var luneCount by remember { mutableIntStateOf(0) }
    var luneDeltaToday by remember { mutableIntStateOf(0) }

    var scoreHier by remember { mutableStateOf<Int?>(null) }
    var terreDeltaText by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(
        context,
        seedBase,
        saveStore.luneTick.intValue,
        saveStore.terreTick.intValue
    ) {
        println(
            "DEBUG_HOME_TERRE → LaunchedEffect déclenché ! " +
                    "Tick Lune=${saveStore.luneTick.intValue} | Terre=${saveStore.terreTick.intValue}"
        )

        luneCount = LuneTraceStore.read(context, seedBase)
        luneDeltaToday = LuneTraceStore.readDeltaToday(context, seedBase)

        val entries = TerreHistoryStore.readAll(context, seedBase)
            .toList()
            .sortedBy { it.first }

        println("DEBUG_HOME_TERRE → Historique Terre lu : ${entries.size} entrées")
        entries.forEachIndexed { index, entry ->
            println(
                "DEBUG_HOME_TERRE → entrée $index : date=${entry.first} | percent=${entry.second}"
            )
        }

        val latestPercent = entries.lastOrNull()?.second
        val previousPercent = if (entries.size >= 2) {
            entries[entries.size - 2].second
        } else {
            null
        }

        println(
            "DEBUG_HOME_TERRE → latestPercent = $latestPercent | previousPercent = $previousPercent"
        )

        scoreHier = latestPercent ?: 0

        terreDeltaText = if (latestPercent != null && previousPercent != null) {
            val delta = latestPercent - previousPercent
            println("DEBUG_HOME_TERRE → delta calculé = $delta")
            when {
                delta > 0 -> "+$delta"
                delta < 0 -> delta.toString()
                else -> null
            }
        } else {
            println(
                "DEBUG_HOME_TERRE → delta null → raison : " +
                        "latest=$latestPercent | previous=$previousPercent | entries.size=${entries.size}"
            )
            null
        }
    }

    val hasTodayHomeData =
        saveStore.cyclePercentMap.isNotEmpty() ||
                saveStore.lastDeltaToday.value != null

    val rawSoleilPercent = saveStore.lastPercent.value?.coerceIn(0, 100) ?: 0
    val soleilPercent = if (hasTodayHomeData) rawSoleilPercent else 0

    val rawSoleilDelta = saveStore.lastDeltaToday.value
    val soleilDeltaText = if (!hasTodayHomeData) {
        null
    } else {
        when {
            rawSoleilDelta == null -> null
            rawSoleilDelta == 0 -> null
            rawSoleilDelta > 0 -> "+$rawSoleilDelta"
            else -> rawSoleilDelta.toString()
        }
    }

    return HomeScreenDataState(
        luneCount = luneCount,
        luneDeltaToday = luneDeltaToday,
        scoreHier = scoreHier,
        terreDeltaText = terreDeltaText,
        soleilPercent = soleilPercent,
        soleilDeltaText = soleilDeltaText
    )
}