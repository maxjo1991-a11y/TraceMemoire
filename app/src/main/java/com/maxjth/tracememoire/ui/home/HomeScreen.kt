// FILE: app/src/main/java/com/maxjth/tracememoire/ui/home/HomeScreen.kt
package com.maxjth.tracememoire.ui.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.components.HomeCycleStatusRect
import com.maxjth.tracememoire.ui.components.HomeMemoryCircle
import com.maxjth.tracememoire.ui.home.logic.HomeCycleTickerLogic
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_MAUVE
import com.maxjth.tracememoire.ui.tracejour.components.screen.save.TraceSaveStore
import java.time.LocalDateTime
import java.util.Calendar

@Composable
fun HomeScreen(
    onAddTrace: () -> Unit,
    onOpenHistory: () -> Unit,
    saveStore: TraceSaveStore
) {
    val traceCount = 0

    val year = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val subtitle = remember(year) { homeSubtitleForYear(year) }

    val changeMs = 20_000L

    val doneCycles = saveStore.completedTodaySet().size.coerceIn(0, 4)
    val progressPercent = doneCycles * 25

    var tickerUi by remember {
        mutableStateOf(
            HomeCycleTickerLogic.HomeTickerUi(
                title = "Cycle ACTIF",
                subtitle = "Cycle disponible.",
                rightHint = "0/4"
            )
        )
    }

    LaunchedEffect(saveStore) {
        HomeCycleTickerLogic.tickerFlow(
            intervalMs = changeMs,
            provider = { saveStore.buildHomeTickerSnapshot(LocalDateTime.now()) }
        ).collect { ui -> tickerUi = ui }
    }

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

                HomeMemoryCircle(
                    traceCount = traceCount,
                    progressPercent = progressPercent,
                    // ✅ FIX: HomeMemoryCircle n’a PAS "now". Il utilise "nowOverride".
                    // null = horloge réelle => couleur pilotée par l’heure automatiquement.
                    nowOverride = null,
                    modifier = Modifier.size(300.dp)
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .widthIn(max = 520.dp)
                    .padding(horizontal = 32.dp)
                    .navigationBarsPadding()
                    .padding(bottom = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Button(
                    onClick = onAddTrace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .border(
                            3.dp,
                            MAUVE.copy(alpha = 0.70f),
                            RoundedCornerShape(32.dp)
                        ),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MAUVE.copy(alpha = 0.14f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Filled.Add, null)
                    Spacer(Modifier.size(14.dp))
                    Text("Ajouter une Mémoire")
                }

                Spacer(Modifier.height(14.dp))

                AnimatedContent(
                    targetState = tickerUi,
                    transitionSpec = { fadeIn(tween(260)) togetherWith fadeOut(tween(260)) },
                    label = "home_cycle_ticker"
                ) { ui ->
                    HomeCycleStatusRect(
                        title = ui.title,
                        subtitle = ui.subtitle,
                        rightHint = ui.rightHint,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onOpenHistory
                    )
                }
            }
        }
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