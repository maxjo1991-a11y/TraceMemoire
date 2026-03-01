package com.maxjth.tracememoire.ui.historique.logic

import com.maxjth.tracememoire.ui.historique.HistoryUiEvent
import com.maxjth.tracememoire.ui.structure.TraceEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DayGroup(
    val dayKey: String,
    val prettyDay: String,
    val events: List<HistoryUiEvent>
)

fun buildGroupedHistory(
    events: List<TraceEvent>,
    onlyChanges: Boolean
): List<DayGroup> {

    // 1) Base : tri + filtre (sur TraceEvent)
    val base: List<TraceEvent> = events
        .sortedBy { it.timestamp } // oldest -> newest (utile pour filterOnlyChanges)
        .let { if (onlyChanges) filterOnlyChanges(it) else it }

    // 2) Conversion UI (TraceEvent -> HistoryUiEvent)
    val uiBase: List<HistoryUiEvent> = base.map { e -> e.toHistoryUiEvent() }

    // 3) GroupBy sur le modèle UI
    return uiBase
        .groupBy { it.dayKey.ifBlank { "unknown" } }
        .toSortedMap(compareByDescending { it }) // yyyy-MM-dd => tri ok (newest day first)
        .map { (dayKey, list) ->
            DayGroup(
                dayKey = dayKey,
                prettyDay = prettyDayLabel(dayKey),
                // ✅ Dans la journée : plus récent en haut
                events = list.sortedByDescending { it.sortKey }
            )
        }
}

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

private fun formatTime(ts: Long): String {
    return try {
        val fmt = SimpleDateFormat("HH:mm", Locale.getDefault())
        fmt.format(Date(ts))
    } catch (_: Throwable) {
        "—"
    }
}

/**
 * Mapping central : TraceEvent -> HistoryUiEvent
 * (UI minimal : texte + heure + %)
 */
private fun TraceEvent.toHistoryUiEvent(): HistoryUiEvent {
    val safeId =
        if (this.id.isNotBlank()) this.id
        else "${this.dayKey}_${this.timestamp}"

    val typeName = this.type.name

    val title = when (typeName) {
        "PERCENT" -> "Trace du jour"
        "NOTE" -> "Note"
        "TAGS" -> "Tags"
        else -> typeName
            .lowercase()
            .replaceFirstChar { it.uppercase() }
    }

    val subtitle: String? = when (typeName) {
        "NOTE", "TAGS" -> this.value?.takeIf { it.isNotBlank() }
        else -> null
    }

    // ✅ percent UNIQUEMENT pour PERCENT
    val percent: Int? = if (typeName == "PERCENT") this.value?.toIntOrNull() else null

    val time = formatTime(this.timestamp)

    return HistoryUiEvent(
        id = safeId,
        dayKey = this.dayKey.ifBlank { "unknown" },
        prettyDay = this.dayKey.ifBlank { "unknown" }, // DayGroup.prettyDay est la vraie étiquette
        title = title,
        subtitle = subtitle,
        percent = percent,
        time = time
    )
}

/**
 * ✅ clé de tri interne (plus fiable que time String)
 * Si ton HistoryUiEvent n’a pas ça, ajoute-le dans HistoryUiEvent (models)
 * sinon remplace par: list.sortedByDescending { it.time ?: "" } (moins bon)
 */
private val HistoryUiEvent.sortKey: String
    get() = "${dayKey}_${time ?: "00:00"}"