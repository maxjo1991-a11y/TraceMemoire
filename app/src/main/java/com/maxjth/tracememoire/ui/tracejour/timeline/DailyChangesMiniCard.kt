package com.maxjth.tracememoire.ui.tracejour.timeline


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.maxjth.tracememoire.ui.model.TraceEvent
import com.maxjth.tracememoire.ui.model.TraceEventType
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT

/**
 * TRUST HEAVEN — Mini bloc calme
 *
 * - Résumé simple
 * - Compact
 * - Aucun calcul lourd
 * - Cliquable (dépliable)
 * - Ignore les events non pertinents
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DailyChangesMiniCard(
    title: String = "Changements",
    subtitle: String = "Résumé rapide (clique pour voir)",
    events: List<TraceEvent>,
    maxPreview: Int = 4,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Nettoyage + labels lisibles
    val clean = remember(events) {
        events
            .sortedByDescending { it.timestamp }
            .mapNotNull { miniLabel(it) }
    }

    val preview = clean.take(maxPreview)
    val shown = if (expanded) clean else preview

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MAUVE.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
            .background(BG_SOFT.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
            .clickable(enabled = clean.isNotEmpty()) { expanded = !expanded }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column {

            // ───────── HEADER ─────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = WHITE_SOFT.copy(alpha = 0.92f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MAUVE.copy(alpha = 0.22f),
                            shape = RoundedCornerShape(999.dp)
                        )
                        .background(
                            color = BG_SOFT.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = clean.size.toString(),
                        color = WHITE_SOFT.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = if (clean.isEmpty()) {
                    "Aucun changement pour l’instant."
                } else subtitle,
                color = WHITE_SOFT.copy(alpha = if (clean.isEmpty()) 0.42f else 0.55f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(10.dp))

            // ───────── CONTENU ─────────
            if (shown.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    shown.forEach { line ->
                        MiniPill(text = line)
                    }
                }

                Spacer(Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (expanded) "Réduire" else "Voir tout",
                        color = TURQUOISE.copy(alpha = 0.85f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.weight(1f))

                    Text(
                        text = if (expanded) "▲" else "▼",
                        color = TURQUOISE.copy(alpha = 0.55f),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniPill(text: String) {
    Box(
        modifier = Modifier
            .border(1.dp, MAUVE.copy(alpha = 0.16f), RoundedCornerShape(999.dp))
            .background(BG_SOFT.copy(alpha = 0.14f), RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = WHITE_SOFT.copy(alpha = 0.82f),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Mini label ultra sûr.
 * Retourne null si l’event ne doit PAS apparaître.
 */
private fun miniLabel(e: TraceEvent): String? {
    return when (e.type) {

        TraceEventType.PERCENT_UPDATE ->
            "${e.value}%"

        TraceEventType.TAG_UPDATE -> {
            val parts = e.value.split("|", limit = 3)
            if (parts.size == 3) {
                when (parts[0]) {
                    "TAG_ON" -> "+ ${parts[2]}"
                    "TAG_OFF" -> "- ${parts[2]}"
                    else -> null
                }
            } else null
        }

        TraceEventType.TIME_ADJUST ->
            "Heure"

        // Tous les autres types (TRACE_CREATE, NOTE_UPDATE, etc.)
        else -> null
    }
}