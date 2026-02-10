// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/TraceJourTitleBlock.kt
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycle
import com.maxjth.tracememoire.ui.tracejour.logic.TraceCycleClock
import java.time.LocalTime

@Composable
fun TraceJourTitleBlock(
    modifier: Modifier = Modifier
) {
    // 🔒 Cycle courant (logique officielle)
    val cycle: TraceCycle = remember {
        TraceCycleClock.currentCycle(LocalTime.now())
    }

    // ✅ Sous-titre dynamique (ex: "du Soir")
    val subtitle: String = remember(cycle) {
        TraceCycleClock.subtitleForCycle(cycle)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // TITRE 1
        Text(
            text = "Trace",
            color = Color.White,
            fontSize = 65.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        // TITRE 2
        Text(
            text = "d’état d’âme",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 47.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // SOUS-TITRE (du Matin / du Jour / du Soir / de la Nuit)
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 60.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))
    }
}