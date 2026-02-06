package com.maxjth.tracememoire.ui.history.logic

import com.maxjth.tracememoire.ui.model.TraceEvent
import java.text.SimpleDateFormat
import java.util.Locale

data class DayGroup(
    val dayKey: String,
    val prettyDay: String,
    val events: List<TraceEvent>
)

fun buildGroupedHistory(
    events: List<TraceEvent>,
    onlyChanges: Boolean
): List<DayGroup> {

    val base = events
        .sortedBy { it.timestamp }
        .let { if (onlyChanges) filterOnlyChanges(it) else it }

    return base
        .groupBy { it.dayKey.ifBlank { "unknown" } }
        .toSortedMap(compareByDescending { it }) // yyyy-MM-dd => tri OK
        .map { (dayKey, list) ->
            DayGroup(
                dayKey = dayKey,
                prettyDay = prettyDayLabel(dayKey),
                events = list.sortedBy { it.timestamp }
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

