package com.maxjth.tracememoire.ui.tracejour.components.store

import com.maxjth.tracememoire.ui.model.TraceEvent
import com.maxjth.tracememoire.ui.model.TraceEventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Store central des événements de la journée.
 * Source de vérité unique.
 * Rien n'est écrasé. Tout est tracé.
 *
 * Format TAG_UPDATE verrouillé :
 *   "TAG_ON|HH:mm|Tag"
 *   "TAG_OFF|HH:mm|Tag"
 */
object TraceEventStore {

    private val _events = MutableStateFlow<List<TraceEvent>>(emptyList())
    val events: StateFlow<List<TraceEvent>> = _events

    /** Formatter heure (24h) */
    private val hourFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    /** Formatter jour (clé stable) */
    private val dayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun nowMs(): Long = System.currentTimeMillis()
    private fun hourLabel(ms: Long): String = hourFormatter.format(Date(ms))
    private fun dayKey(ms: Long): String = dayFormatter.format(Date(ms))

    /** Ajout générique d’événement */
    private fun addEvent(
        type: TraceEventType,
        value: String,
        customTimestamp: Long? = null
    ) {
        val timestamp = customTimestamp ?: nowMs()

        val event = TraceEvent(
            id = UUID.randomUUID().toString(),
            dayKey = dayKey(timestamp),
            timestamp = timestamp,
            hourLabel = hourLabel(timestamp),
            type = type,
            value = value
        )

        _events.value = _events.value + event
    }

    /** Changement du pourcentage (1 event quand on relâche) */
    fun recordPercent(percent: Int) {
        addEvent(
            type = TraceEventType.PERCENT_UPDATE,
            value = percent.coerceIn(0, 100).toString()
        )
    }

    /**
     * Tag ON / OFF
     *
     * ✅ Si action est déjà complet (ex: "TAG_ON|11:25|Calme"),
     * on le garde tel quel.
     *
     * ✅ Sinon, on reconstruit un format propre "TAG_ON|HH:mm|tagLabel".
     */
    fun recordTag(action: String, tagLabel: String) {
        val clean = normalizeTagValue(action = action, tagLabel = tagLabel)
        addEvent(
            type = TraceEventType.TAG_UPDATE,
            value = clean
        )
    }

    private fun normalizeTagValue(action: String, tagLabel: String): String {
        val parts = action.split("|")

        // Cas 1) action complet : TAG_ON|HH:mm|Tag
        if (parts.size >= 3 && (parts[0] == "TAG_ON" || parts[0] == "TAG_OFF")) {
            val a = parts[0]
            val hour = parts[1].ifBlank { hourLabel(nowMs()) }
            val tag = parts[2].ifBlank { tagLabel }
            return "$a|$hour|$tag"
        }

        // Cas 2) action simple : TAG_ON / TAG_OFF
        if (action == "TAG_ON" || action == "TAG_OFF") {
            val hour = hourLabel(nowMs())
            return "$action|$hour|$tagLabel"
        }

        // Cas 3) fallback ultra safe (on force OFF si inconnu)
        val hour = hourLabel(nowMs())
        return "TAG_OFF|$hour|$tagLabel"
    }

    /** Ajustement manuel de l’heure */
    fun recordTimeAdjustment(timestamp: Long) {
        addEvent(
            type = TraceEventType.TIME_ADJUST,
            value = "MANUAL",
            customTimestamp = timestamp
        )
    }

    /** Reset (nouvelle journée, plus tard) */
    fun clear() {
        _events.value = emptyList()
    }
}