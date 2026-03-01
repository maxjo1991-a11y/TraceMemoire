// FILE: app/src/main/java/com/maxjth/tracememoire/ui/accueil/ecran/HomeScreen.kt
package com.maxjth.tracememoire.ui.accueil.ecran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.maxjth.tracememoire.ui.accueil.ajout.button.EmpreinteButton
import com.maxjth.tracememoire.ui.accueil.ajout.effect.EmpreinteOverlay
import com.maxjth.tracememoire.ui.accueil.ajout.effect.empreinteInput
import com.maxjth.tracememoire.ui.accueil.ajout.effect.rememberEmpreinteEffectController
import com.maxjth.tracememoire.ui.accueil.blocs.AccueilHeaderBloc
import com.maxjth.tracememoire.ui.accueil.constellation.AccueilConstellationBlock
import com.maxjth.tracememoire.ui.accueil.rectangle.AccueilRectangleBloc
import com.maxjth.tracememoire.ui.systeme.lune.trace.LuneTraceStore
import com.maxjth.tracememoire.ui.systeme.soleil.cycle.rememberHomeNow
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore
import java.util.Calendar

@Composable
fun HomeScreen(
    onAddTrace: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenAstra: () -> Unit,
    onOpenMemora: () -> Unit,
    onOpenOrion: () -> Unit,
    saveStore: TraceSaveStore
) {
    val context = LocalContext.current
    val seedBase = remember { "TRACE_MEMOIRE" }

    // ✅ attach
    LaunchedEffect(Unit) { saveStore.attach(context, seedBase) }

    // ✅ IMPORTANT: reload HOME-only au démarrage (sinon score/rectangle peut disparaître après restart)
    LaunchedEffect(Unit) { saveStore.loadHomeOnlyForHomeScreen(context, seedBase) }

    // ✅ tick (Astra etc.)
    LaunchedEffect(Unit) { saveStore.onHomeTick() }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) saveStore.onHomeTick()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val luneCount = remember { LuneTraceStore.read(context, seedBase) }
    val luneDeltaToday = remember { LuneTraceStore.readDeltaToday(context, seedBase) }

    val soleilPercent: Int? = saveStore.lastPercent.value?.coerceIn(0, 100)
    val yearlyPercent: Int? = saveStore.yearlyPercent.value?.coerceIn(0, 100)

    val pMatin: Int? = saveStore.cyclePercentMap["MATIN"]?.coerceIn(0, 100)
    val pJour: Int? = saveStore.cyclePercentMap["JOUR"]?.coerceIn(0, 100)
    val pSoir: Int? = saveStore.cyclePercentMap["SOIR"]?.coerceIn(0, 100)
    val pNuit: Int? = saveStore.cyclePercentMap["NUIT"]?.coerceIn(0, 100)

    fun dotsForCycle(cycleKey: String) = saveStore.getFreeDotsForCycle(cycleKey)

    val year = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val subtitle = remember(year) {
        when (year) {
            2026 -> "Le temps n'explique pas. Il mémorise"
            2027 -> "Ce qui revient laisse une trace."
            2028 -> "La mémoire révèle ce qui insistait."
            2029 -> "Ce qui a été noté ne disparaît plus."
            else -> "Le temps fait la mémoire."
        }
    }

    val homeNow = rememberHomeNow()
    val scrollState = rememberScrollState()
    val empreinteController = rememberEmpreinteEffectController()

    // ✅ Offsets centralisés (facile à ajuster)
    val empreinteOffsetY = (-100).dp
    val rectangleOffsetY = (-50).dp

    Scaffold(containerColor = BG_DEEP) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(BG_DEEP, BG_SOFT.copy(alpha = 0.9f), BG_DEEP)
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 32.dp, vertical = 50.dp)
                    .widthIn(max = 520.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AccueilHeaderBloc(subtitle = subtitle)
                Spacer(Modifier.height(18.dp))

                AccueilConstellationBlock(
                    luneCount = luneCount,
                    luneDeltaToday = luneDeltaToday,
                    yearlyPercent = yearlyPercent,
                    soleilPercent = soleilPercent,
                    nowOverride = homeNow
                )

                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .offset(y = empreinteOffsetY)
                        .empreinteInput(
                            controller = empreinteController,
                            onClick = { onAddTrace() },
                            onLongPress = { onAddTrace() }
                        )
                ) {
                    EmpreinteButton(modifier = Modifier)

                    EmpreinteOverlay(
                        controller = empreinteController,
                        modifier = Modifier.fillMaxSize(),
                        colorStart = MAUVE,
                        colorEnd = TURQUOISE
                    )
                }

                Spacer(Modifier.height(1.dp))

                Box(modifier = Modifier.offset(y = rectangleOffsetY)) {
                    AccueilRectangleBloc(
                        pMatin = pMatin,
                        pJour = pJour,
                        pSoir = pSoir,
                        pNuit = pNuit,
                        dotsForCycleKey = { cycleKey -> dotsForCycle(cycleKey) }
                    )
                }

                Spacer(Modifier.height(60.dp))

                TextButton(
                    onClick = onOpenHistory,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Outlined.CalendarToday, null)
                    Spacer(Modifier.size(10.dp))
                    Text("Historique")
                }

                Spacer(Modifier.height(8.dp))

                TextButton(onClick = onOpenAstra, modifier = Modifier.fillMaxWidth()) { Text("Astra") }
                TextButton(onClick = onOpenMemora, modifier = Modifier.fillMaxWidth()) { Text("Memora") }
                TextButton(onClick = onOpenOrion, modifier = Modifier.fillMaxWidth()) { Text("Orion") }

                Spacer(Modifier.height(14.dp))
            }
        }
    }
}