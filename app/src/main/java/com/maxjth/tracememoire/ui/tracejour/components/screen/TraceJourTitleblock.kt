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
import com.maxjth.tracememoire.ui.theme.MAUVE
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

    val tagline = "L'expérience intérieure est une réalité silencieuse"

    Column(
        modifier = modifier
            .fillMaxWidth()
            // ✅ Air en haut : assez pour respirer, pas trop pour voler de la place au ring
            .padding(top = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ------------------------------------------------------------
        // ✅ TITRE (LOCK UI) : hiérarchie premium stable
        // Trace = 66sp
        // d’état d’âme = 30sp
        // cycle (Jour/Soir/Nuit) = 60sp
        // ------------------------------------------------------------

        Text(
            text = "Trace",
            color = Color.White,
            fontSize = 66.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 66.sp
        )

        // ✅ micro-espace premium (évite l'effet empilé)
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "d’état d’âme",
            color = Color.White.copy(alpha = 0.92f),
            fontSize = 30.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        // ✅ espace plus généreux avant le cycle (respiration)
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = cycleTitle,
            color = Color.White,
            fontSize = 60.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        // ✅ distance vers la tagline (zone calme)
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

        // ✅ descente vers le bloc contenu (sliders / ring)
        Spacer(modifier = Modifier.height(54.dp))
    }
}