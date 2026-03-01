package com.maxjth.tracememoire.ui.historique.components

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.historique.HistoryUiEvent
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

@Composable
fun HistoryEventCard(
    event: HistoryUiEvent,
    modifier: Modifier = Modifier
) {
    val time = event.time ?: "—"
    val title = event.title
    val detail = buildDetail(event)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MAUVE.copy(alpha = 0.18f),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                // ✅ BG_SOFT au lieu de BG_DEEP alpha : plus clean, moins “sale”
                color = BG_SOFT.copy(alpha = 0.16f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Heure (plus stable, un peu plus lisible)
            Text(
                text = time,
                color = WHITE_SOFT.copy(alpha = 0.62f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Séparateur fin (plus discret)
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(1.dp)
                    .background(WHITE_SOFT.copy(alpha = 0.14f))
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Texte principal
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = WHITE_SOFT.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                if (detail.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = detail,
                        color = WHITE_SOFT.copy(alpha = 0.56f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

private fun buildDetail(event: HistoryUiEvent): String {
    // Priorité: subtitle -> percent -> rien
    event.subtitle?.let { if (it.isNotBlank()) return it }
    event.percent?.let { return "${it}%" }
    return ""
}