package com.maxjth.tracememoire.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.item
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxjth.tracememoire.ui.model.TraceEvent
import com.maxjth.tracememoire.ui.model.TraceEventType
import com.maxjth.tracememoire.ui.theme.BG_DEEP
import com.maxjth.tracememoire.ui.theme.BG_SOFT
import com.maxjth.tracememoire.ui.theme.MAUVE
import com.maxjth.tracememoire.ui.theme.TURQUOISE
import com.maxjth.tracememoire.ui.theme.WHITE_SOFT
import com.maxjth.tracememoire.ui.tracejour.TraceEventStore
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit) {

    val events by TraceEventStore.events.collectAsState()
    var onlyChanges by remember { mutableStateOf(false) }

    val bgDeep = BG_DEEP
    val bgSlight = BG_DEEP.copy(alpha = 0.92f)

    // 1) Tri + option onlyChanges
    // 2) Groupement par dayKey
    val grouped: List<DayGroup> = remember(events, onlyChanges) {
        val base = events
            .sortedBy { it.timestamp } // timeline naturelle
            .let { if (onlyChanges) filterOnlyChanges(it) else it }

        base
            .groupBy { it.dayKey.ifBlank { "unknown" } }
            .toSortedMap(compareByDescending { it }) // yyyy-MM-dd => tri lexical OK
            .map { (dayKey, list) ->
                DayGroup(
                    dayKey = dayKey,
                    prettyDay = prettyDayLabel(dayKey),
                    events = list.sortedBy { it.timestamp }
                )
            }
    }

    Scaffold(
        containerColor = bgDeep,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Historique",
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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = bgDeep
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(bgDeep, bgSlight, bgDeep)))
                .padding(padding)
        ) {

            // ─────────────────────────────
            // AUCUN ÉVÉNEMENT -> écran calme
            // ─────────────────────────────
            if (grouped.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    androidx.compose.material3.Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = TURQUOISE.copy(alpha = 0.75f),
                        modifier = Modifier.size(44.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Bientôt : tes traces s’afficheront ici.",
                        color = WHITE_SOFT.copy(alpha = 0.78f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Lecture calme. Sans pression.",
                        color = WHITE_SOFT.copy(alpha = 0.50f),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }

            } else {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {

                    // ─────────────────────────────
                    // SWITCH ONLY CHANGES
                    // ─────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Voir seulement les changements",
                                color = WHITE_SOFT.copy(alpha = 0.90f),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "Masque les doublons consécutifs (plus lisible).",
                                color = WHITE_SOFT.copy(alpha = 0.45f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Switch(
                            checked = onlyChanges,
                            onCheckedChange = { onlyChanges = it }
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // ─────────────────────────────
                    // LISTE GROUPÉE PAR JOUR
                    // ─────────────────────────────
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        grouped.forEach { group ->

                            item(key = "day_${group.dayKey}") {
                                DayHeader(
                                    prettyDay = group.prettyDay,
                                    dayKey = group.dayKey,
                                    count = group.events.size
                                )
                            }

                            items(
                                items = group.events,
                                key = { it.id }
                            ) { e ->
                                HistoryEventCard(e)
                            }
                        }

                        item { Spacer(Modifier.height(16.dp)) }
                    }
                }
            }
        }
    }
}

/* ---------------------------- UI ---------------------------- */

@Composable
private fun DayHeader(prettyDay: String, dayKey: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = prettyDay,
                color = WHITE_SOFT.copy(alpha = 0.95f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = dayKey,
                color = WHITE_SOFT.copy(alpha = 0.35f),
                style = MaterialTheme.typography.bodySmall
            )
        }

        // ✅ Pastille "X événements"
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
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = "$count év.",
                color = WHITE_SOFT.copy(alpha = 0.72f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun HistoryEventCard(e: TraceEvent) {

    val (title, detail) = eventLabel(e)

    Box(
        modifier = Modifier
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
        Row(verticalAlignment = Alignment.CenterVertically) {

            Text(
                text = e.hourLabel,
                color = WHITE_SOFT.copy(alpha = 0.55f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(1.dp)
                    .background(WHITE_SOFT.copy(alpha = 0.18f))
            )

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = WHITE_SOFT.copy(alpha = 0.90f),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (detail.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
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

/* ---------------------------- Data / Helpers ---------------------------- */

private data class DayGroup(
    val dayKey: String,
    val prettyDay: String,
    val events: List<TraceEvent>
)

/**
 * “Voir seulement les changements” :
 * enlève les doublons CONSÉCUTIFS identiques (même type + même value)
 */
private fun filterOnlyChanges(list: List<TraceEvent>): List<TraceEvent> {
    if (list.isEmpty()) return emptyList()

    val out = ArrayList<TraceEvent>(list.size)
    var lastSig: String? = null

    for (e in list) {
        val sig = "${e.type.name}|${e.value}"
        if (sig != lastSig) {
            out.add(e)
            lastSig = sig
        }
    }
    return out
}

private fun eventLabel(e: TraceEvent): Pair<String, String> {
    return when (e.type) {

        TraceEventType.PERCENT_UPDATE -> {
            "Pourcentage" to "${e.value}%"
        }

        TraceEventType.TAG_UPDATE -> {
            val v = e.value
            when {
                v.startsWith("TAG_ON|") -> "Tag activé" to v.removePrefix("TAG_ON|")
                v.startsWith("TAG_OFF|") -> "Tag retiré" to v.removePrefix("TAG_OFF|")
                v.startsWith("ON:") -> "Tag activé" to v.removePrefix("ON:")
                v.startsWith("OFF:") -> "Tag retiré" to v.removePrefix("OFF:")
                else -> "Tag" to v
            }
        }

        TraceEventType.TIME_ADJUST -> {
            "Heure ajustée" to "Modification manuelle"
        }

        else -> {
            "Événement" to e.value
        }
    }
}

private fun prettyDayLabel(dayKey: String): String {
    return try {
        val inFmt = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outFmt = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        val date = inFmt.parse(dayKey)
        if (date != null) outFmt.format(date) else dayKey
    } catch (_: Throwable) {
        dayKey
    }
}