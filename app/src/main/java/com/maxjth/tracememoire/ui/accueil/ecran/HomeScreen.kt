package com.maxjth.tracememoire.ui.accueil.ecran

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.maxjth.tracememoire.ui.accueil.ajout.effect.rememberEmpreinteEffectController
import com.maxjth.tracememoire.ui.accueil.ecran.home.HomeScreenContent
import com.maxjth.tracememoire.ui.accueil.ecran.home.rememberHomeScreenData
import com.maxjth.tracememoire.ui.accueil.ecran.home.rememberHomeScreenEmpreinteStar
import com.maxjth.tracememoire.ui.accueil.ecran.home.rememberHomeScreenTicker
import com.maxjth.tracememoire.ui.moteur.cycle.modele.TypeCycleHome
import com.maxjth.tracememoire.ui.systeme.soleil.cycle.rememberHomeNow
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.store.TraceSaveStore
import java.time.LocalDateTime
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

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

    fun cyclePercent(type: TypeCycleHome): Int? =
        saveStore.cyclePercentMap[type.storageKey()]?.coerceIn(0, 100)

    fun dotsForCycle(type: TypeCycleHome): List<Boolean> =
        saveStore.getFreeDotsForCycle(type.storageKey())

    val pMatin = cyclePercent(TypeCycleHome.MATIN)
    val pJour = cyclePercent(TypeCycleHome.JOUR)
    val pSoir = cyclePercent(TypeCycleHome.SOIR)
    val pNuit = cyclePercent(TypeCycleHome.NUIT)

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
            scoreHier = dataState.scoreHier,
            soleilPercent = dataState.soleilPercent,
            soleilDeltaText = dataState.soleilDeltaText,
            terreDeltaText = dataState.terreDeltaText,
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
                saveStore.onHomeTick(LocalDateTime.of(2026, 3, 5, 23, 58))
                saveStore.onHomeTick(LocalDateTime.of(2026, 3, 6, 0, 1))
            },
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val start = empreinteStarState.boutonCenterInRoot
            val end = empreinteStarState.memoireCenterInRoot
            val progress = empreinteStarState.starTravel.value
            val alpha = empreinteStarState.starAlpha.value

            if (start != null && end != null && alpha > 0.01f) {
                val control = Offset(
                    x = (start.x + end.x) / 2f,
                    y = minOf(start.y, end.y) - 180f
                )

                val starPos = quadraticBezierPoint(
                    start = start,
                    end = end,
                    control = control,
                    t = progress
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            TURQUOISE.copy(alpha = alpha * 0.20f),
                            MAUVE.copy(alpha = alpha * 0.12f),
                            Color.Transparent
                        ),
                        center = starPos,
                        radius = 44f
                    ),
                    center = starPos,
                    radius = 44f
                )

                if (progress > 0.04f) {
                    val tailPos = quadraticBezierPoint(
                        start = start,
                        end = end,
                        control = control,
                        t = (progress - 0.05f).coerceAtLeast(0f)
                    )

                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                MAUVE.copy(alpha = alpha * 0.28f),
                                TURQUOISE.copy(alpha = alpha * 0.36f)
                            ),
                            start = tailPos,
                            end = starPos
                        ),
                        start = tailPos,
                        end = starPos,
                        strokeWidth = 8f,
                        cap = StrokeCap.Round
                    )
                }

                drawStar(
                    center = starPos,
                    outerRadius = 14f,
                    innerRadius = 6.2f,
                    color = Color.White.copy(alpha = alpha * 0.96f)
                )

                drawStar(
                    center = starPos,
                    outerRadius = 10f,
                    innerRadius = 4.4f,
                    color = TURQUOISE.copy(alpha = alpha * 0.52f)
                )
            }
        }
    }
}

private fun quadraticBezierPoint(
    start: Offset,
    end: Offset,
    control: Offset,
    t: Float
): Offset {
    val oneMinusT = 1f - t

    val x =
        oneMinusT * oneMinusT * start.x +
                2f * oneMinusT * t * control.x +
                t * t * end.x

    val y =
        oneMinusT * oneMinusT * start.y +
                2f * oneMinusT * t * control.y +
                t * t * end.y

    return Offset(x, y)
}

private fun DrawScope.drawStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    color: Color
) {
    val path = Path()
    val points = 4
    val step = Math.PI / points
    val startAngle = -Math.PI / 2.0

    repeat(points * 2) { i ->
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val angle = startAngle + step * i
        val x = center.x + cos(angle).toFloat() * radius
        val y = center.y + sin(angle).toFloat() * radius

        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }

    path.close()
    drawPath(path = path, color = color)
}