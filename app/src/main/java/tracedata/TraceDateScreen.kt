package com.maxjth.tracememoire.ui.tracedata

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
        filtered.groupBy { it.dayKey }
            .toSortedMap(compareByDescending { it }) // yyyy-MM-dd => tri OK
    }

    Scaffold(
        containerColor = bgDeep,
        topBar = {
            TopAppBar(
                title = { Text("Trace — Données (Écran 4)") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Retour", color = TURQUOISE, fontWeight = FontWeight.SemiBold)
                    }
                }
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
            style = MaterialTheme.typography.labelLarge
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
 * - Tag : toujours un changement (TAG_ON|X / TAG_OFF|X)
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
        }
    }
    return out
}

/**
 * Format lecture humaine.
 * - PERCENT_UPDATE: "Pourcentage" / "72%"
 * - TAG_UPDATE: "Tag" / "ON • Calme" ou "OFF • Calme"
 * - TIME_ADJUST: "Heure" / "MANUAL"
 */
private fun formatEvent(e: TraceEvent): Pair<String, String> {
    return when (e.type) {
        TraceEventType.PERCENT_UPDATE -> "Pourcentage" to "${e.value}%"

        TraceEventType.TAG_UPDATE -> {
            // attend: TAG_ON|Calme / TAG_OFF|Calme
            val parts = e.value.split("|", limit = 2)
            if (parts.size == 2) {
                val action = parts[0].removePrefix("TAG_").replace("_", "")
                val tag = parts[1]
                "Tag" to "$action • $tag"
            } else {
                "Tag" to e.value
            }
        }

        TraceEventType.TIME_ADJUST -> "Heure" to e.value
    }
}