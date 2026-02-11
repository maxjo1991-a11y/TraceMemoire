package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycle
import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycleClock
import java.time.LocalTime

@Composable
fun TraceJourTitleBlock(
    modifier: Modifier = Modifier
) {
    val cycle: TraceCycle = remember {
        TraceCycleClock.currentCycle(LocalTime.now())
    }

    val cycleTitle: String = remember(cycle) {
        when (cycle) {
            TraceCycle.JOUR -> "Jour"
            TraceCycle.SOIR -> "Soir"
            TraceCycle.NUIT -> "Nuit"
            else -> cycle.name.lowercase().replaceFirstChar { it.uppercase() }
        }
    }

    val tagline = "Le temps n’explique pas. Il mémorise"

    Column(
        modifier = modifier
            .fillMaxWidth()
            // ✅ Plus d’air en haut (important pour équilibre global)
            .padding(top = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Trace",
            color = WHITE_SOFT,
            fontSize = 64.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 64.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "d’état d’âme",
            color = WHITE_SOFT.copy(alpha = 0.92f),
            fontSize = 48.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            lineHeight = 50.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = cycleTitle,
            color = WHITE_SOFT.copy(alpha = 0.88f),
            fontSize = 64.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 64.sp
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = tagline,
            color = MAUVE.copy(alpha = 0.78f),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp),
            lineHeight = 24.sp
        )

        // ✅ Descente PLUS FRANCHE du bloc sliders
        Spacer(modifier = Modifier.height(62.dp))
    }
}