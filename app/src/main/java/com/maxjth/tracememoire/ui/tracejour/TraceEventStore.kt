package com.maxjth.tracememoire.ui.tracejour

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
 */
object TraceEventStore {

    private val _events = MutableStateFlow<List<TraceEvent>>(emptyList())
    val events: StateFlow<List<TraceEvent>> = _events

    /** Formatter heure (24h) */
    private val hourFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    /** Formatter jour (clé stable) */
    private val dayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /** Ajout générique d’événement */
    private fun addEvent(
        type: TraceEventType,
        value: String,
        customTimestamp: Long? = null
    ) {
        val timestamp = customTimestamp ?: System.currentTimeMillis()
        val date = Date(timestamp)

        val event = TraceEvent(
            id = UUID.randomUUID().toString(),
            dayKey = dayFormatter.format(date),   // ex: 2026-02-05
            timestamp = timestamp,
            hourLabel = hourFormatter.format(date), // ex: 11:25
            type = type,
            value = value
        )

        _events.value = _events.value + event
    }

    /** Changement du pourcentage (1 event quand on relâche) */
    fun recordPercent(percent: Int) {
        addEvent(
            type = TraceEventType.PERCENT_UPDATE,
            value = percent.toString()
        )
    }

    /**
     * Tag ON / OFF
     * value attendue:
     *  - TAG_ON|Calme
     *  - TAG_OFF|Calme
     */
    fun recordTag(action: String, tagLabel: String) {
        addEvent(
            type = TraceEventType.TAG_UPDATE,
            value = "$action|$tagLabel"
        )
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