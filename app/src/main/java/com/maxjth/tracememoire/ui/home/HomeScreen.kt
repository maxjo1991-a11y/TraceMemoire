// FILE: app/src/main/java/com/maxjth/tracememoire/ui/home/HomeScreen.kt
package com.maxjth.tracememoire.ui.home

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.components.HomeCycleStatusRect
import com.maxjth.tracememoire.ui.components.HomeMemoryCircle
import com.maxjth.tracememoire.ui.components.YesterdayPercentCircle
import com.maxjth.tracememoire.ui.home.logic.HomeAmbientPhrasesRect
import com.maxjth.tracememoire.ui.home.logic.HomeCycleTickerLogic
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.TraceSaveStore
import java.time.LocalDateTime
import java.util.Calendar

private data class HomeRectUi(
    val ticker: HomeCycleTickerLogic.HomeTickerUi,
    val phrase: String
)

@Composable
fun HomeScreen(
    onAddTrace: () -> Unit,
    onOpenHistory: () -> Unit,
    saveStore: TraceSaveStore
) {
    val context = LocalContext.current

    // TODO: brancher ton vrai traceCount (quand prêt)
    val traceCount = 0

    val year = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val subtitle = remember(year) { homeSubtitleForYear(year) }

    val changeMs = 20_000L

    // ✅ Phrase ambiante
    val ambientPhrase = HomeAmbientPhrasesRect.rememberAmbientPhraseRect(
        periodMs = 12_000L,
        shuffleMode = true
    )

    // ✅ UI ticker (cycle actif)
    var tickerUi by remember {
        mutableStateOf(
            HomeCycleTickerLogic.HomeTickerUi(
                title = "Cycle ACTIF",
                subtitle = "Cycle disponible.",
                rightHint = "0/4"
            )
        )
    }

    // ✅ IMPORTANT:
    // - On charge les prefs UNE FOIS à l’arrivée sur Home.
    // - Ça empêche le “0%” juste parce que lastPercent est encore null.
    // - Ajuste seedBase / cycleKey si tu utilises autre chose.
    LaunchedEffect(Unit) {
        ensureHomeLoadedOnce(context, saveStore)
    }

    // ✅ Flow ticker (inchangé)
    LaunchedEffect(saveStore) {
        HomeCycleTickerLogic.tickerFlow(
            intervalMs = changeMs,
            provider = { saveStore.buildHomeTickerSnapshot(LocalDateTime.now()) }
        ).collect { ui -> tickerUi = ui }
    }

    val rectUi = remember(tickerUi, ambientPhrase) {
        HomeRectUi(ticker = tickerUi, phrase = ambientPhrase)
    }

    // ✅ Jupiter:
    // - ON NE MET PLUS JAMAIS "?: 0"
    // - si lastPercent est null (pas chargé / jamais calculé), on affiche traceCount au centre (pas 0% fake)
    // - si lastPercent existe, on l’affiche en %
    val jupiterPercent: Int? = saveStore.lastPercent.value?.coerceIn(0, 100)

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
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 210.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Trace\nMémoire",
                    fontSize = 61.sp,
                    lineHeight = 60.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = subtitle,
                    color = MAUVE.copy(alpha = 0.9f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp),
                    lineHeight = 24.sp
                )

                Spacer(Modifier.height(30.dp))

                // ✅ Jupiter + Mars : FIX SANS CLEAR / SANS OFFSCREEN
                Box(
                    modifier = Modifier.size(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // JUPITER — aujourd’hui
                    HomeMemoryCircle(
                        traceCount = traceCount,
                        progressPercent = jupiterPercent,          // ✅ Int? (null => pas de 0% fake)
                        nowOverride = null,
                        modifier = Modifier.matchParentSize(),
                        numberEntryScale = 0.92f                   // ✅ si tu veux rapetisser un peu le 55/57
                    )

                    // MARS — fond BG_DEEP en cercle derrière
                    Box(
                        modifier = Modifier
                            .size(124.dp)
                            .align(Alignment.CenterStart)
                            .offset(x = (-16).dp, y = (72).dp)
                            .background(BG_DEEP, CircleShape)
                    )

                    // MARS — hier (par-dessus)
                    YesterdayPercentCircle(
                        percentYesterday = saveStore.yesterdayPercent.value,
                        modifier = Modifier
                            .size(124.dp)
                            .align(Alignment.CenterStart)
                            .offset(x = (-16).dp, y = (72).dp),
                        sizeDp = 116,
                        numberScale = 0.78f,
                        marsBreathSlowFactor = 1.25f
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
                    .padding(horizontal = 32.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    onClick = onAddTrace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .border(
                            2.dp,
                            MAUVE.copy(alpha = 0.40f),
                            RoundedCornerShape(30.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MAUVE.copy(alpha = 0.10f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.55f)
                    )

                    Spacer(Modifier.size(15.dp))

                    Text(
                        text = "Ajouter une Mémoire",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.3.sp,
                        color = Color(0xFFF5F5F5)
                    )
                }

                Spacer(Modifier.height(14.dp))

                AnimatedContent(
                    targetState = rectUi,
                    transitionSpec = { fadeIn(tween(260)) togetherWith fadeOut(tween(260)) },
                    label = "home_cycle_ticker"
                ) { state ->
                    HomeCycleStatusRect(
                        leftTitle = state.ticker.title,
                        rightCounter = state.ticker.rightHint,
                        ambientLine = state.phrase,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onOpenHistory
                    )
                }
            }
        }
    }
}

/**
 * ✅ Charge les prefs HOME une seule fois.
 * - On ne met PAS de 0 par défaut.
 * - Si jamais tu n’as encore rien sauvegardé, lastPercent restera null => HomeMemoryCircle affiche traceCount.
 */
private fun ensureHomeLoadedOnce(
    context: Context,
    saveStore: TraceSaveStore
) {
    // 🔒 seedBase / cycleKey : valeurs “safe” pour Home.
    // Important: loadFromPrefs() a besoin d’un cycleKey + sliderKeys.
    // Pour Home, on s’en fout des sliders ici -> on envoie une liste vide.
    val seedBase = "TRACE_MEMOIRE" // ⚠️ si tu as déjà un seedBase officiel, remplace-le ici
    val cycleKey = "JOUR"          // valeur valide, juste pour satisfaire la signature

    runCatching {
        saveStore.loadFromPrefs(
            context = context,
            seedBase = seedBase,
            cycleKey = cycleKey,
            sliderKeys = emptyList()
        )
    }
}

private fun homeSubtitleForYear(year: Int): String {
    return when (year) {
        2026 -> "Le temps n'explique pas. Il mémorise"
        2027 -> "Ce qui revient laisse une trace."
        2028 -> "La mémoire révèle ce qui insistait."
        2029 -> "Ce qui a été noté ne disparaît plus."
        else -> "Le temps fait la mémoire."
    }
}