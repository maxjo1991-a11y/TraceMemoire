package com.maxjth.tracememoire.ui.accueil.ecran

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.maxjth.tracememoire.ui.accueil.ajout.effect.rememberEmpreinteEffectController
import com.maxjth.tracememoire.ui.accueil.ecran.home.HomeEmpreinteStarCanvas
import com.maxjth.tracememoire.ui.accueil.ecran.home.HomeScreenContent
import com.maxjth.tracememoire.ui.accueil.ecran.home.rememberHomeScreenData
import com.maxjth.tracememoire.ui.accueil.ecran.home.rememberHomeScreenEmpreinteStar
import com.maxjth.tracememoire.ui.accueil.ecran.home.rememberHomeScreenTicker
import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import com.maxjth.tracememoire.ui.systeme.soleil.cycle.rememberHomeNow
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.helper.TraceValeurCycleHelper
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore

@Composable
fun HomeScreen(
    onAddTrace: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenComprendre: () -> Unit,
    onOpenAstra: () -> Unit,
    onOpenMemora: () -> Unit,
    onOpenOrion: () -> Unit,
    saveStore: TraceSaveStore
) {
    val context = LocalContext.current
    val seedBase = remember { "TRACE_MEMOIRE" }

    val dataState = rememberHomeScreenData(
        saveStore = saveStore,
        seedBase = seedBase
    )

    val tickerState = rememberHomeScreenTicker(
        saveStore = saveStore
    )

    val empreinteStarState = rememberHomeScreenEmpreinteStar(
        onAddTrace = onAddTrace
    )

    val empreinteController = rememberEmpreinteEffectController()
    val homeNow = rememberHomeNow()

    val year = remember {
        java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
    }

    val subtitle = remember(year) {
        when (year) {
            2026 -> "Que restera-t-il de cette journée ?"
            2027 -> "Ce qui revient laisse une trace."
            2028 -> "La mémoire révèle ce qui insistait."
            2029 -> "Ce qui a été noté ne disparaît plus."
            else -> "Le temps fait la mémoire."
        }
    }

    fun dotsForCycle(type: TypeCycleHome): List<Boolean> =
        saveStore.getFreeDotsForCycle(type.storageKey())

    fun cycleValue(type: TypeCycleHome): Int? {
        val cycleKey = type.storageKey().trim().uppercase()

        val createdAt = try {
            saveStore.createdAtMillis(cycleKey)
        } catch (_: Exception) {
            null
        }

        if (createdAt == null) return null

        return TraceValeurCycleHelper.readPersistedCycleValue(
            context = context,
            seedBase = seedBase,
            cycleKey = cycleKey
        ).coerceIn(0, 400)
    }

    // ✅ VALEURS PURES DE CYCLE
    val pMatin = cycleValue(TypeCycleHome.MATIN)
    val pJour = cycleValue(TypeCycleHome.JOUR)
    val pSoir = cycleValue(TypeCycleHome.SOIR)
    val pNuit = cycleValue(TypeCycleHome.NUIT)

    val yesterdayValue by saveStore.yesterdayValue
    val todayValue by saveStore.todayValue

    val terreValue: Int? = yesterdayValue.coerceAtLeast(0)
    val soleilValue: Int? = todayValue.coerceAtLeast(0)

    val deltaValue: Int? = if (terreValue != null && soleilValue != null) {
        soleilValue - terreValue
    } else {
        null
    }

    val soleilDeltaText: String? = deltaValue?.let {
        when {
            it > 0 -> "+$it"
            it < 0 -> "$it"
            else -> "0"
        }
    }

    val terreDeltaText: String? = null

    fun createdAtMillisForCycleKey(cycleKey: String): Long? {
        return try {
            saveStore.createdAtMillis(cycleKey)
        } catch (_: Exception) {
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BG_DEEP)
    ) {
        HomeScreenContent(
            subtitle = subtitle,
            pillLabel = tickerState.pillLabel,
            showPill = tickerState.showPill,
            luneCount = dataState.luneCount,
            luneDeltaToday = dataState.luneDeltaToday,
            scoreHier = terreValue,
            soleilValue = soleilValue,
            soleilDeltaText = soleilDeltaText,
            terreDeltaText = terreDeltaText,
            homeNow = homeNow,
            activeCycleKey = tickerState.activeCycleKey,
            pMatin = pMatin,
            pJour = pJour,
            pSoir = pSoir,
            pNuit = pNuit,
            empreinteController = empreinteController,
            galaxyFlashValue = empreinteStarState.galaxyFlash.value,
            onAddTraceClick = { empreinteStarState.launchEmpreinteStar() },
            onOpenHistory = onOpenHistory,
            onOpenComprendre = onOpenComprendre,
            onBoutonCenterChanged = empreinteStarState.onBoutonCenterChanged,
            onMemoireCenterChanged = empreinteStarState.onMemoireCenterChanged,
            createdAtMillisForCycleKey = ::createdAtMillisForCycleKey,
            dotsForCycleKey = { cycleKey ->
                val type = TypeCycleHome.fromKey(cycleKey)
                if (type != null) dotsForCycle(type)
                else saveStore.getFreeDotsForCycle(cycleKey)
            },
            onTestMinuit = {
                saveStore.debugForceMidnight(
                    context = context,
                    seedBase = seedBase
                )
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        HomeEmpreinteStarCanvas(
            start = empreinteStarState.boutonCenterInRoot,
            end = empreinteStarState.memoireCenterInRoot,
            progress = empreinteStarState.starTravel.value,
            alpha = empreinteStarState.starAlpha.value,
            modifier = Modifier.fillMaxSize()
        )
    }
}