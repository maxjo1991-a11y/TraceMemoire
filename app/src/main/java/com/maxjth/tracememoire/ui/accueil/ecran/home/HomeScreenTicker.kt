package com.maxjth.tracememoire.ui.accueil.ecran.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.maxjth.tracememoire.ui.moteur.cycle.liaison.CycleFacadeHome
import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import com.maxjth.tracememoire.ui.noyau.lois.HomeCycleTickerLogic
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore
import java.time.LocalDateTime

data class HomeScreenTickerState(
    val tickerUi: HomeCycleTickerLogic.HomeTickerUi?,
    val pillLabel: String,
    val showPill: Boolean,
    val activeCycleKey: String?
)

@Composable
fun rememberHomeScreenTicker(
    saveStore: TraceSaveStore
): HomeScreenTickerState {
    val cycleFacadeHome = remember { CycleFacadeHome() }

    var tickerUi by remember {
        mutableStateOf<HomeCycleTickerLogic.HomeTickerUi?>(null)
    }

    LaunchedEffect(Unit) {
        HomeCycleTickerLogic
            .tickerFlow(
                intervalMs = 2600L,
                provider = {
                    val now = LocalDateTime.now()

                    val completed =
                        cycleFacadeHome.completedFromPercentMap(
                            saveStore.cyclePercentMap
                        )

                    val resolution = cycleFacadeHome.resolve(
                        now = now,
                        completedToday = completed
                    )

                    HomeCycleTickerLogic.HomeSnapshot(
                        now = now,
                        completedToday = resolution.completedToday,
                        lastPercent = saveStore.lastPercent.value,
                        lastCycleSaved = null,
                        yearlyPercent = saveStore.yearlyPercent.value
                    )
                }
            )
            .collect { ui ->
                tickerUi = ui
            }
    }

    val pillLabel = cycleTitleFr(tickerUi?.currentCycle)
    val showPill = tickerUi?.currentCycle != null
    val activeCycleKey = tickerUi?.currentCycle?.storageKey()

    return HomeScreenTickerState(
        tickerUi = tickerUi,
        pillLabel = pillLabel,
        showPill = showPill,
        activeCycleKey = activeCycleKey
    )
}

fun cycleTitleFr(type: TypeCycleHome?): String = when (type) {
    TypeCycleHome.NUIT -> "Nuit"
    TypeCycleHome.MATIN -> "Matin"
    TypeCycleHome.JOUR -> "Jour"
    TypeCycleHome.SOIR -> "Soir"
    null -> "Cycle"
}