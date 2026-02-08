package com.maxjth.tracememoire.ui.history.logic

import com.maxjth.tracememoire.ui.history.HistoryUiEvent
import com.maxjth.tracememoire.ui.model.TraceEvent
import java.text.SimpleDateFormat
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

    // 1) Base : tri + filtre (sur TraceEvent, c’est correct)
    val base: List<TraceEvent> = events
        .sortedBy { it.timestamp }
        .let { if (onlyChanges) filterOnlyChanges(it) else it }

    // 2) Conversion UI (TraceEvent -> HistoryUiEvent)
    val uiBase: List<HistoryUiEvent> = base.map { e ->
        e.toHistoryUiEvent()
    }

    // 3) GroupBy sur le modèle UI (pas sur TraceEvent)
    return uiBase
        .groupBy { it.dayKey.ifBlank { "unknown" } }
        .toSortedMap(compareByDescending { it }) // yyyy-MM-dd => tri OK
        .map { (dayKey, list) ->
            DayGroup(
                dayKey = dayKey,
                prettyDay = prettyDayLabel(dayKey),
                events = list.sortedBy { it.time ?: "" }
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

/**
 * Mapping central : TraceEvent -> HistoryUiEvent
 * (On reste “UI minimal” comme tu veux : texte + heure + %)
 */
private fun TraceEvent.toHistoryUiEvent(): HistoryUiEvent {
    val safeId =
        if (this.id.isNotBlank()) this.id
        else "${this.dayKey}_${this.timestamp}"

    val title = when (this.type.name) {
        "PERCENT" -> "Trace du jour"
        "NOTE" -> "Note"
        "TAGS" -> "Tags"
        else -> this.type.name
            .lowercase()
            .replaceFirstChar { it.uppercase() }
    }

    val subtitle: String? = when (this.type.name) {
        "NOTE", "TAGS" -> this.value?.takeIf { it.isNotBlank() }
        else -> null
    }

    val percent: Int? = this.value?.toIntOrNull()

    return HistoryUiEvent(
        id = safeId,
        dayKey = this.dayKey.ifBlank { "unknown" },
        prettyDay = this.dayKey.ifBlank { "unknown" }, // remplacé plus tard
        title = title,
        subtitle = subtitle,
        percent = percent,
        time = null // ✅ IMPORTANT : pas de time pour l’instant
    )
}