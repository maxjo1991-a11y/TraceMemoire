package com.maxjth.tracememoire.ui.tracejour.components.screen

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.model.TraceEvent
import com.maxjth.tracememoire.ui.tracejour.timeline.TraceEventRow
import kotlin.collections.forEach
@Composable
fun TraceJourTitleBlock(subtitleTint: Color) {

    // ✅ protège le haut (status bar) + donne une marge stable
    Spacer(Modifier.statusBarsPadding())
    Spacer(Modifier.height(10.dp))

    Text(
        text = "Trace",
        color = Color.White,
        fontSize = 67.sp,
        lineHeight = 62.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    )

    Text(
        text = "d’état d’âme",
        color = Color.White,
        fontSize = 59.sp,
        lineHeight = 58.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
    )

    Spacer(Modifier.height(10.dp))

    Text(
        text = "Où en es-tu, là, maintenant ?",
        color = subtitleTint,
        fontSize = 18.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )

    Spacer(Modifier.height(12.dp))
}






@Composable
fun TraceJourTimelineBlock(events: List<TraceEvent>) {
    if (events.isEmpty()) {
        Text(
            text = "Aucun événement pour l’instant.",
            color = Color.White.copy(alpha = 0.38f)
        )
        Spacer(Modifier.height(24.dp))
        return
    }

    events.forEach { e ->
        TraceEventRow(e)
        Spacer(Modifier.height(8.dp))
    }
    Spacer(Modifier.height(26.dp))
}