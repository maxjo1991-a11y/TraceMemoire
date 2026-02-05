package com.maxjth.tracememoire.ui.tracejour.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.maxjth.tracememoire.ui.model.TraceEvent
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TraceEventRow(e: TraceEvent) {

    val label = when (e.type.name) {
        "PERCENT_UPDATE" -> "Pourcentage"
        "TAG_UPDATE" -> "Tag"
        "TIME_ADJUST" -> "Heure"
        else -> "Event"
    }

    // TAG_ON|11:25|Calme → ON • Calme (11:25)
    val prettyValue: String = if (e.type.name == "TAG_UPDATE") {
        val parts = e.value.split("|", limit = 3)
        if (parts.size == 3) {
            val action = parts[0]
            val hour = parts[1]
            val tag = parts[2]
            when (action) {
                "TAG_ON" -> "ON • $tag ($hour)"
                "TAG_OFF" -> "OFF • $tag ($hour)"
                else -> e.value
            }
        } else e.value
    } else e.value

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MAUVE.copy(alpha = 0.18f),
                shape = RoundedCornerShape(14.dp)
            )
            .background(
                color = BG_SOFT.copy(alpha = 0.22f),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = e.hourLabel,
                color = WHITE_SOFT.copy(alpha = 0.55f),
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = label,
                color = WHITE_SOFT.copy(alpha = 0.85f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = "— $prettyValue",
                color = WHITE_SOFT.copy(alpha = 0.55f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

