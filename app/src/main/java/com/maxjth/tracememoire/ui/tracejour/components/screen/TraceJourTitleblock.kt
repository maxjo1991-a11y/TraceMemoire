// FILE: app/src/main/java/com/maxjth/tracememoire/ui/tracejour/components/screen/TraceJourTitleBlock.kt
package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.tracejour.cycle.TraceCycle

@Composable
fun TraceJourTitleBlock(
    cycle: TraceCycle,
    modifier: Modifier = Modifier
) {
    val cycleTitle: String = when (cycle) {
        TraceCycle.JOUR -> "Jour"
        TraceCycle.SOIR -> "Soir"
        TraceCycle.NUIT -> "Nuit"
        TraceCycle.MATIN -> "Matin"
    }

    val tagline = "L'expérience intérieure est une réalité silencieuse"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Trace",
            color = Color.White,
            fontSize = 66.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 66.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "d’état d’âme",
            color = Color.White.copy(alpha = 0.92f),
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = cycleTitle,
            color = Color.White,
            fontSize = 60.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = tagline,
            color = MAUVE.copy(alpha = 0.9f),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp),
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(54.dp))
    }
}