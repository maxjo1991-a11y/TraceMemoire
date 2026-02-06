package com.maxjth.tracememoire.ui.tracedata

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.maxjth.tracememoire.ui.model.TraceEvent
import com.maxjth.tracememoire.ui.model.TraceEventType
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.TraceEventStore
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceDataScreen(
    onBack: () -> Unit,
    eventsFlow: StateFlow<List<TraceEvent>> = TraceEventStore.events
) {
    val events by eventsFlow.collectAsState()
    var onlyChanges by remember { mutableStateOf(true) }

    val bgDeep = BG_SOFT
    val bgSlight = BG_SOFT.copy(alpha = 0.92f)

    // 1) Trier par timestamp
    val sorted = remember(events) { events.sortedBy { it.timestamp } }

    // 2) Filtrer "seulement les changements"
    val filtered = remember(sorted, onlyChanges) {
        if (!onlyChanges) sorted else keepOnlyChanges(sorted)
    }

    // 3) Grouper par dayKey (dernier jour en haut)
    val grouped = remember(filtered) {
        filtered
            .groupBy { it.dayKey.ifBlank { "unknown" } }
            .toSortedMap(compareByDescending { it }) // yyyy-MM-dd => tri lexical OK
    }

    Scaffold(
        containerColor = bgDeep,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Trace — Données",
                        color = WHITE_SOFT,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(
                            text = "Retour",
                            color = TURQUOISE,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgDeep)
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgDeep, bgSlight, bgDeep)))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp, bottom = 14.dp)
            ) {

                // Toggle "voir seulement les changements"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Voir seulement les changements",
                        color = WHITE_SOFT.copy(alpha = 0.80f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = onlyChanges,
                        onCheckedChange = { onlyChanges = it }
                    )
                }

                Spacer(Modifier.height(12.dp))

                if (grouped.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aucune donnée pour l’instant.",
                            color = WHITE_SOFT.copy(alpha = 0.40f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        grouped.forEach { (dayKey, dayEvents) ->
                            item(key = "day_$dayKey") {
                                DayBlock(dayKey = dayKey, events = dayEvents)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayBlock(dayKey: String, events: List<TraceEvent>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MAUVE.copy(alpha = 0.18f), RoundedCornerShape(18.dp))
            .background(BG_SOFT.copy(alpha = 0.22f), RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Text(
            text = dayKey,
            color = WHITE_SOFT.copy(alpha = 0.90f),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(10.dp))

        // Timeline lisible
        events.sortedBy { it.timestamp }.forEach { e ->
            TimelineRow(e)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun TimelineRow(e: TraceEvent) {
    val (label, detail) = formatEvent(e)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = e.hourLabel,
            color = WHITE_SOFT.copy(alpha = 0.55f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = label,
            color = WHITE_SOFT.copy(alpha = 0.88f),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = "— $detail",
            color = WHITE_SOFT.copy(alpha = 0.55f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * ✅ Ne garde que les changements “réels”.
 * - Pourcentage : garde seulement si différent du précédent % (dans la timeline)
 * - Tag : toujours un changement (ON/OFF)
 * - TIME_ADJUST : toujours un changement
 */
private fun keepOnlyChanges(sorted: List<TraceEvent>): List<TraceEvent> {
    val out = ArrayList<TraceEvent>(sorted.size)
    var lastPercent: String? = null

    for (e in sorted) {
        when (e.type) {
            TraceEventType.PERCENT_UPDATE -> {
                val cur = e.value
                if (cur != lastPercent) {
                    out.add(e)
                    lastPercent = cur
                }
            }
            TraceEventType.TAG_UPDATE -> out.add(e)
            TraceEventType.TIME_ADJUST -> out.add(e)

            // ✅ sécurité si tu ajoutes d’autres types plus tard
            else -> out.add(e)
        }
    }
    return out
}

/**
 * Format lecture humaine.
 * Supporte:
 * - PERCENT_UPDATE: "Pourcentage" / "72%"
 * - TAG_UPDATE:
 *    • "TAG_ON|Calme"
 *    • "TAG_OFF|Calme"
 *    • "TAG_ON|12:34|Calme"
 *    • "TAG_OFF|12:34|Calme"
 * - TIME_ADJUST: "Heure" / value
 */
private fun formatEvent(e: TraceEvent): Pair<String, String> {
    return when (e.type) {
        TraceEventType.PERCENT_UPDATE -> "Pourcentage" to "${e.value}%"

        TraceEventType.TAG_UPDATE -> {
            val parts = e.value.split("|")
            // formats possibles:
            // 2 parts: TAG_ON, tag
            // 3 parts: TAG_ON, hh:mm, tag
            if (parts.size >= 2) {
                val action = parts[0]
                    .removePrefix("TAG_")
                    .replace("_", "")
                    .uppercase()

                val tag = parts.last() // prend le dernier token = le tag
                val time = if (parts.size >= 3) parts[1] else null

                val detail = if (!time.isNullOrBlank()) "$action • $tag • $time" else "$action • $tag"
                "Tag" to detail
            } else {
                "Tag" to e.value
            }
        }

        TraceEventType.TIME_ADJUST -> "Heure" to e.value

        else -> "Événement" to e.value
    }
}