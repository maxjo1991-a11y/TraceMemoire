package com.maxjth.tracememoire.ui.tracejour.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.model.TraceEvent
import com.maxjth.tracememoire.ui.model.TraceEventType
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun TraceEventRow(
    event: TraceEvent,
    modifier: Modifier = Modifier
) {
    val ui = rememberEventUi(event)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MAUVE.copy(alpha = 0.16f), RoundedCornerShape(16.dp))
            .background(BG_SOFT.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Accent barre verticale (calme) ──
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .background(ui.accent.copy(alpha = 0.75f), RoundedCornerShape(999.dp))
                    .padding(vertical = 14.dp)
            )

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {

                // Ligne 1 : titre
                Text(
                    text = ui.title,
                    color = WHITE_SOFT.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Ligne 2 : détail (si présent)
                if (ui.detail.isNotBlank()) {
                    Text(
                        text = ui.detail,
                        color = WHITE_SOFT.copy(alpha = 0.62f),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(10.dp))

            // Heure (si tu l’as)
            val hour = event.hourLabel
            if (hour.isNotBlank()) {
                Text(
                    text = hour,
                    color = WHITE_SOFT.copy(alpha = 0.55f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private data class EventUi(
    val title: String,
    val detail: String,
    val accent: androidx.compose.ui.graphics.Color
)

@Composable
private fun rememberEventUi(e: TraceEvent): EventUi {
    return when (e.type) {

        TraceEventType.PERCENT_UPDATE -> {
            // e.value = "72"
            EventUi(
                title = "Pourcentage",
                detail = "${e.value}%",
                accent = TURQUOISE
            )
        }

        TraceEventType.TAG_UPDATE -> {
            // e.value = "TAG_ON|11:25|Calme"  ou  "TAG_OFF|11:25|Calme"
            val parts = e.value.split("|", limit = 3)
            val action = parts.getOrNull(0).orEmpty()
            val tag = parts.getOrNull(2).orEmpty()

            val (title, detail, accent) = when (action) {
                "TAG_ON" -> Triple("Tag ajouté", tag, TURQUOISE)
                "TAG_OFF" -> Triple("Tag retiré", tag, MAUVE)
                else -> Triple("Tag", e.value, WHITE_SOFT)
            }

            EventUi(
                title = title,
                detail = detail,
                accent = accent
            )
        }

        TraceEventType.TIME_ADJUST -> {
            EventUi(
                title = "Heure",
                detail = e.value.ifBlank { "Ajustement" },
                accent = MAUVE
            )
        }

        // ✅ Ces deux-là évitent ton erreur “when must be exhaustive”
        TraceEventType.TRACE_CREATE -> {
            EventUi(
                title = "Trace créée",
                detail = "",
                accent = WHITE_SOFT
            )
        }

        TraceEventType.NOTE_UPDATE -> {
            EventUi(
                title = "Note",
                detail = "Mise à jour",
                accent = WHITE_SOFT
            )
        }

        // 🔒 Sûreté totale : si tu ajoutes d’autres types plus tard, ça compile pareil
        else -> {
            EventUi(
                title = "Événement",
                detail = e.value,
                accent = WHITE_SOFT
            )
        }
    }
}