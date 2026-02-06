package com.maxjth.tracememoire.ui.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.model.TraceEvent
import com.maxjth.tracememoire.ui.model.TraceEventType
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun HistoryEventCard(
    event: TraceEvent,
    modifier: Modifier = Modifier
) {
    val (title, detail) = eventLabel(event)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MAUVE.copy(alpha = 0.18f),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = BG_DEEP.copy(alpha = 0.18f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Heure
            Text(
                text = event.hourLabel,
                color = WHITE_SOFT.copy(alpha = 0.55f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Séparateur fin
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(1.dp)
                    .background(WHITE_SOFT.copy(alpha = 0.18f))
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Texte principal
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = WHITE_SOFT.copy(alpha = 0.90f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                if (detail.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = detail,
                        color = WHITE_SOFT.copy(alpha = 0.52f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/* ---------------------------- LABELS ---------------------------- */

private fun eventLabel(event: TraceEvent): Pair<String, String> {
    return when (event.type) {

        TraceEventType.PERCENT_UPDATE -> {
            "Pourcentage" to "${event.value}%"
        }

        TraceEventType.TAG_UPDATE -> {
            val v = event.value
            when {
                v.startsWith("TAG_ON|")  -> "Tag activé" to v.removePrefix("TAG_ON|")
                v.startsWith("TAG_OFF|") -> "Tag retiré" to v.removePrefix("TAG_OFF|")
                v.startsWith("ON:")      -> "Tag activé" to v.removePrefix("ON:")
                v.startsWith("OFF:")     -> "Tag retiré" to v.removePrefix("OFF:")
                else                     -> "Tag" to v
            }
        }

        TraceEventType.TIME_ADJUST -> {
            "Heure ajustée" to "Modification manuelle"
        }

        else -> {
            "Événement" to event.value
        }
    }
}
