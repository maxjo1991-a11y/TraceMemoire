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
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore

data class HomeScreenDataState(
    val luneCount: Int,
    val luneDeltaToday: Int
)

@Composable
fun rememberHomeScreenData(
    saveStore: TraceSaveStore,
    seedBase: String = "TRACE_MEMOIRE"
): HomeScreenDataState {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasDoneInitialLoad by remember { mutableStateOf(false) }

    LaunchedEffect(context, seedBase) {
        saveStore.attach(context, seedBase)
        saveStore.loadHomeOnlyForHomeScreen(context, seedBase)
        saveStore.onHomeTick()
        hasDoneInitialLoad = true
    }

    LaunchedEffect(Unit) {
        saveStore.terreTick.intValue += 1
    }

    DisposableEffect(lifecycleOwner, hasDoneInitialLoad) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && hasDoneInitialLoad) {
                saveStore.onHomeTick()
                saveStore.loadHomeOnlyForHomeScreen(context, seedBase)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    var luneCount by remember { mutableIntStateOf(0) }
    var luneDeltaToday by remember { mutableIntStateOf(0) }

    LaunchedEffect(
        context,
        seedBase,
        saveStore.luneTick.intValue
    ) {
        luneCount = LuneTraceStore.read(context, seedBase)
        luneDeltaToday = LuneTraceStore.readDeltaToday(context, seedBase)
    }

    return HomeScreenDataState(
        luneCount = luneCount,
        luneDeltaToday = luneDeltaToday
    )
}